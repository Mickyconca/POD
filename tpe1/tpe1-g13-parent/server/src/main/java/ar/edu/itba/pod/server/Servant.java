package ar.edu.itba.pod.server;

import ar.edu.itba.pod.exceptions.*;
import ar.edu.itba.pod.flight.Category;
import ar.edu.itba.pod.flight.FlightStatus;
import ar.edu.itba.pod.server.models.*;
import ar.edu.itba.pod.services.*;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Servant implements FlightService {
    private final Map<String, PlaneModel> planeModels = new HashMap<>();
    private final Map<String, Flight> flights = new HashMap<>();
    private final Map<String, Map<String, List<NotificationsServiceClient>>> passengersNotifications = new HashMap<>(); //Map<passengerName,<flightCode,handler>>

    private final ExecutorService executor;

    private final Object planeModelsLock = new Object();
    private final Object flightsLock = new Object();

    public Servant() {
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public void registerPlaneModel(String name, int[] businessSeats, int[] premiumSeats, int[] economySeats) throws DuplicateModelException, RemoteException {
        synchronized (planeModelsLock) {
            if (planeModels.containsKey(name)) {
                throw new DuplicateModelException();
            }
            if (Arrays.stream(businessSeats).anyMatch(i -> i < 0) || Arrays.stream(premiumSeats).anyMatch(i -> i < 0) || Arrays.stream(economySeats).anyMatch(i -> i < 0)) {
                throw new InvalidModelException();
            }
            this.planeModels.put(name, new PlaneModel(name, businessSeats, premiumSeats, economySeats));
        }
    }

    //Boeing 787;AA100;JFK;BUSINESS#John,ECONOMY#Juliet,BUSINESS#Elizabeth
    @Override
    public void registerFlight(String modelName, String flightCode, String destinyAirport, Map<Category, Set<String>> passengers) throws RemoteException {
        Map<String, Passenger> formattedPassengers = new HashMap<>();
        for (Map.Entry<Category, Set<String>> entry : passengers.entrySet()) {
            for (String p : entry.getValue()) {
                formattedPassengers.put(p, new Passenger(p, entry.getKey()));
            }
        }

        synchronized (flightsLock) {
            if (flights.containsKey(flightCode)) {
                throw new DuplicateFlightCodeException();
            }
            if (!planeModels.containsKey(modelName)) {
                throw new ModelNotFoundException();
            }
            PlaneModel planeModel = planeModels.get(modelName);
            this.flights.put(flightCode, new Flight(planeModel, flightCode, destinyAirport, formattedPassengers));
        }
    }

    @Override
    public FlightStatus flightStatus(String flightCode) throws RemoteException {
        synchronized (flightsLock) {
            if (!flights.containsKey(flightCode)) {
                throw new FlightNotFoundException();
            }
            return flights.get(flightCode).getStatus();
        }
    }

    @Override
    public void confirmFlight(String flightCode) throws RemoteException {
        synchronized (flightsLock) {
            if (!flights.containsKey(flightCode)) {
                throw new FlightNotFoundException();
            }
            flights.get(flightCode).setStatus(FlightStatus.CONFIRMED);

            handleStatusChangeNotifications(flights.get(flightCode));
        }
    }

    @Override
    public void cancelFlight(String flightCode) throws RemoteException {
        synchronized (flightsLock) {
            if (!flights.containsKey(flightCode)) {
                throw new FlightNotFoundException();
            }
            flights.get(flightCode).setStatus(FlightStatus.CANCELLED);
            handleStatusChangeNotifications(flights.get(flightCode));
        }
    }

    @Override
    public List<String> changeFlightTickets(String flightCode) throws RemoteException {
        Flight flight = getFlightByCode(flightCode);
        List<Passenger> sortedPassengers = flight.getPassengers().values().stream().sorted(Comparator.comparing(Passenger::getName)).collect(Collectors.toList());
        int added = 0;
        List<String> notAdded = new LinkedList<>();
        for (Passenger p : sortedPassengers) {
            if (changeTicket(flight, p)) {
                added++;
            } else {
                notAdded.add("Cannot find alternative flight for " + p.getName() + " with Ticket " + flightCode);
            }

        }
        notAdded.add(0, String.valueOf(added));
        return notAdded;
    }

    private boolean changeTicket(Flight originalFlight, Passenger passenger) throws RemoteException {
        List<AlternativeFlight> alternativeFlights = getAlternatives(originalFlight.getFlightCode(), originalFlight.getDestination(), passenger);
        if (alternativeFlights.isEmpty()) {
            return false;
        }
        //find best alternative
        AlternativeFlight alternativeFlight = alternativeFlights.get(0);
        int altCapacity = alternativeFlight.getFlight().getCategoryCapacity(alternativeFlight.getCategory());
        Category category = alternativeFlight.getCategory();
        for (AlternativeFlight af : alternativeFlights.stream().filter((AlternativeFlight af) -> af.getCategory() == category).collect(Collectors.toList())) {
            int newCapacity = af.getFlight().getCategoryCapacity(af.getCategory());
            if (newCapacity > altCapacity) {
                alternativeFlight = af;
                altCapacity = newCapacity;
            } else if (newCapacity == altCapacity) {
                if (af.getFlight().getFlightCode().compareTo(alternativeFlight.getFlight().getFlightCode()) < 0) {
                    alternativeFlight = af;
                }
            }
        }


        if (passenger.hasSeatAssigned()) {
            passenger.getSeat().setEmpty(true);
            passenger.setSeat(null);
        }
        originalFlight.removePassenger(passenger);
        alternativeFlight.getFlight().addPassenger(passenger);
        return true;
    }

    private void handleStatusChangeNotifications(final Flight flight) {
        List<String> passengersWithNotifications = flight.getPassengers().keySet().stream().filter(key -> passengersNotifications.containsKey(key)
                && passengersNotifications.get(key).containsKey(flight.getFlightCode())).collect(Collectors.toList());
        Passenger passenger;
        for (String p : passengersWithNotifications) {
            passenger = flight.getPassenger(p);
            for (NotificationsServiceClient handler : passengersNotifications.get(p).get(flight.getFlightCode())) {
                Passenger finalPassenger = passenger;
                if (finalPassenger.hasSeatAssigned()) {
                    executor.execute(() -> {
                        try {
                            handler.onStatusChange(flight.getFlightCode(), flight.getDestination(), flight.getStatus().getStatus(), finalPassenger.getSeat().getCategory().getCategory(), finalPassenger.getSeat().getRowNumber(), finalPassenger.getSeat().getColLetter());
                        } catch (RemoteException e) {
                            //No notification
                        }
                    });
                } else {
                    executor.execute(() -> {
                        try {
                            handler.onStatusChange(flight.getFlightCode(), flight.getDestination(), flight.getStatus().getStatus(), finalPassenger.getCategory().getCategory(), null, null);
                        } catch (RemoteException e) {
                            //No notification
                        }
                    });
                }
                if (flight.getStatus() == FlightStatus.CONFIRMED) {
                    passengersNotifications.get(p).remove(flight.getFlightCode());
                    if (passengersNotifications.get(p).isEmpty()) { //todo check if this is ok
                        passengersNotifications.remove(p);
                    }
                }
            }
        }
    }

    @Override
    public boolean status(String flightCode, int rowNumber, char colLetter, String passenger) throws RemoteException {
        Flight flight = getFlightByCode(flightCode);
        Seat seat = flight.getSeat(rowNumber, colLetter);
        if (seat != null) {
            return seat.isEmpty();
        }
        throw new SeatNotFoundException();
    }

    @Override
    public void assign(String flightCode, int rowNumber, char colLetter, String passenger) throws RemoteException {
        Flight flight = getFlightByCode(flightCode);

        Passenger passengerInfo = flight.getPassenger(passenger);
        if (passengerInfo.hasSeatAssigned()) {
            throw new PassengerWithSeatAlreadyAssignedException();
        }
        if (flight.getStatus() == FlightStatus.PENDING) {
            Seat seat = flight.getSeat(rowNumber, colLetter);
            if (seat != null) {
                if (seat.isEmpty()) {
                    if (passengerInfo.getCategory().getCategoryId() <= seat.getCategory().getCategoryId()) {
                        passengerInfo.setSeat(seat);
                        seat.setEmpty(false);
                        seatAssignedNotification(flight, passengerInfo);
                        return;
                    }
                    throw new InvalidSeatCategoryException();
                }else{
                    throw new SeatNotEmptyException();
                }
            }else{
                throw new SeatNotFoundException();
            }
        }else if(flight.getStatus() == FlightStatus.CONFIRMED){
            throw new FlightAlreadyConfirmedException();
        }else{
            throw new FlightCancelledException();
        }

    }

    @Override
    public void move(String flightCode, String passenger, int rowNumber, char colLetter) throws RemoteException {
        Flight flight = getFlightByCode(flightCode);
        Passenger passengerInfo = flight.getPassenger(passenger);
        if (flight.getFlightCode().equals(flightCode)) {
            if (passengerInfo.hasSeatAssigned()) {
                Seat oldSeat = passengerInfo.getSeat();
                try {
                    passengerInfo.setSeat(null);
                    assign(flightCode, rowNumber, colLetter, passenger);
                    seatMovedNotification(flight, passengerInfo, oldSeat);
                } catch (Exception e) {
                    passengerInfo.setSeat(oldSeat);
                    e.printStackTrace();
                }
            }
        }
    }

    //    JFK | AA119 | 3 BUSINESS
//    JFK | AA103 | 18 PREMIUM_ECONOMY
    @Override
    public List<String> alternatives(String flightCode, String passenger) throws RemoteException {
        Flight f = getFlightByCode(flightCode);
        Passenger p = f.getPassenger(passenger);
        List<AlternativeFlight> alternativeFlights = getAlternatives(flightCode, f.getDestination(), p);
        return alternativeFlights.stream().map(AlternativeFlight::toString).collect(Collectors.toList());
    }

    @Override
    public void changeTicket(String originalFlightCode, String alternativeFlightCode, String passenger) throws RemoteException {
        Flight oldFlight = getFlightByCode(originalFlightCode);
        Passenger p = oldFlight.getPassenger(passenger);

        if (oldFlight.getStatus() != FlightStatus.CONFIRMED) {

            List<AlternativeFlight> alternativeFlights = getAlternatives(originalFlightCode, oldFlight.getDestination(), p);
            Optional<AlternativeFlight> alternativeFlight = alternativeFlights.stream().filter((AlternativeFlight af) -> af.getFlight().getFlightCode().equals(alternativeFlightCode)).findFirst();
            if (alternativeFlight.isPresent()) {
                if (p.hasSeatAssigned()) {
                    p.getSeat().setEmpty(true);
                    p.setSeat(null);
                }
                oldFlight.removePassenger(p);
                alternativeFlight.get().getFlight().addPassenger(p);
                changeTicketNotification(oldFlight, alternativeFlight.get().getFlight(), p);
            }
            throw new FlightNotFoundException();
        }
        throw new FlightAlreadyConfirmedException();
    }

    @Override
    public List<List<String>> flightSeats(String flightCode) throws RemoteException {
        Flight flight = getFlightByCode(flightCode);
        List<List<String>> results = new LinkedList<>();
        for (Map.Entry<Integer, Map<Character, Seat>> rows : flight.getSeats().entrySet()) {
            results.add(new LinkedList<>());
            for (Seat seat : rows.getValue().values()) {
                results.get(rows.getKey() - 1).add(seatInfoToString(flight, seat));
            }
            Category rowCategory = rows.getValue().get('A').getCategory();
            results.get(rows.getKey() - 1).add(rowCategory.getCategory());
        }
        return results;
    }

    @Override
    public List<List<String>> flightSeatsByCategory(String flightCode, Category category) throws RemoteException {
        Flight flight = getFlightByCode(flightCode);
        int[] categoryRowIndexes = flight.getPlaneModel().getCategoryRowIndexes(category);
        List<List<String>> results = new LinkedList<>();
        for (int i = categoryRowIndexes[0]; i <= categoryRowIndexes[1]; i++) {
            results.add(new LinkedList<>());
            for (Seat seat : flight.getSeats().get(i).values()) {
                results.get(i).add(seatInfoToString(flight, seat));
            }
            Category rowCategory = flight.getSeat(i, 'A').getCategory();
            results.get(i).add(rowCategory.getCategory());
        }
        return results;
    }

    @Override
    public List<String> flightSeatsByRow(String flightCode, int rowNumber) throws RemoteException {
        Flight flight = getFlightByCode(flightCode);
        List<String> results = new LinkedList<>();
        for (Seat seat : flight.getSeats().get(rowNumber).values()) {
            results.add(seatInfoToString(flight, seat));
        }
        results.add(flight.getSeats().get(rowNumber).get('A').getCategory().getCategory());
        return results;
    }

    @Override
    public void registerPassengerForNotifications(String passengerName, String flightCode, NotificationsServiceClient handler) {
        Flight flight = getFlightByCode(flightCode); //throws exception if doesnt exist
        if (flight.getStatus() != FlightStatus.CONFIRMED) {
            flight.getPassenger(passengerName); //throws exc if passenger not in flight
            passengersNotifications.putIfAbsent(passengerName, new HashMap<>());
            passengersNotifications.get(passengerName).putIfAbsent(flightCode, new LinkedList<>());
            passengersNotifications.get(passengerName).get(flightCode).add(handler);
            executor.execute(() -> {
                try {
                    handler.onPassengerRegistered(flightCode, flight.getDestination());
                } catch (RemoteException e) {
                    //No notification
                }
            });
        } else {
            throw new FlightAlreadyConfirmedException();
        }
    }

    private String seatInfoToString(Flight flight, Seat seat) {
        StringBuilder data = new StringBuilder(seat.getRowNumber() + " " + seat.getColLetter() + " ");
        if (seat.isEmpty()) {
            data.append("*").append(" ");
        } else {
            Optional<Passenger> passenger = flight.getPassengers().values().stream().filter((Passenger p) -> p.getSeat() == seat).findFirst();
            if (passenger.isPresent()) {
                data.append(passenger.get().getName().charAt(0)).append(" ");
            } else {
                throw new PassengerNotFoundException();
            }
        }
        return data.toString();
    }

    private void seatAssignedNotification(Flight flight, Passenger passenger) {
        if (passengersNotifications.containsKey(passenger.getName()) && passengersNotifications.get(passenger.getName()).containsKey(flight.getFlightCode())) {
            for (NotificationsServiceClient handler : passengersNotifications.get(passenger.getName()).get(flight.getFlightCode())) {
                executor.execute(() -> {
                    try {
                        handler.onSeatAssigned(passenger.getSeat().getCategory().getCategory(), passenger.getSeat().getRowNumber(), passenger.getSeat().getColLetter(), flight.getFlightCode(), flight.getDestination());
                    } catch (RemoteException e) {
                        //No notification
                    }
                });
            }
        }
    }

    private void seatMovedNotification(Flight flight, Passenger passenger, Seat oldSeat) {
        if (passengersNotifications.containsKey(passenger.getName()) && passengersNotifications.get(passenger.getName()).containsKey(flight.getFlightCode())) {
            for (NotificationsServiceClient handler : passengersNotifications.get(passenger.getName()).get(flight.getFlightCode())) {
                executor.execute(() -> {
                    try {
                        handler.onSeatChanged(oldSeat.getCategory().getCategory(), oldSeat.getRowNumber(), oldSeat.getColLetter(), passenger.getSeat().getCategory().getCategory(), passenger.getSeat().getRowNumber(), passenger.getSeat().getColLetter(), flight.getFlightCode(), flight.getDestination());
                    } catch (RemoteException e) {
                        //No notification
                    }
                });
            }
        }
    }

    private void changeTicketNotification(Flight oldFlight, Flight newFlight, Passenger passenger) {
        if (passengersNotifications.containsKey(passenger.getName()) && passengersNotifications.get(passenger.getName()).containsKey(oldFlight.getFlightCode())) {
            for (NotificationsServiceClient handler : passengersNotifications.get(passenger.getName()).get(oldFlight.getFlightCode())) {
                passengersNotifications.get(passenger.getName()).remove(oldFlight.getFlightCode());
                passengersNotifications.get(passenger.getName()).putIfAbsent(newFlight.getFlightCode(), new LinkedList<>());
                passengersNotifications.get(passenger.getName()).get(newFlight.getFlightCode()).add(handler);
                executor.execute(() -> {
                    try {
                        handler.onTicketChange(oldFlight.getFlightCode(), oldFlight.getDestination(), newFlight.getFlightCode(), newFlight.getDestination());
                    } catch (RemoteException e) {
                        //No notification
                    }
                });
            }
        }
    }


    private Flight getFlightByCode(String flightCode) {
        Flight flight = flights.get(flightCode);
        if (flight == null) {

            throw new FlightNotFoundException();
        }
        return flight;
    }

    private List<AlternativeFlight> getAlternatives(String flightCode, String destination, Passenger passenger) {
        List<AlternativeFlight> alternatives = new LinkedList<>();
        for (Flight flight : flights.values()) {
            if (!flight.getFlightCode().equals(flightCode) && flight.getDestination().equals(destination) && flight.getStatus() == FlightStatus.PENDING) {
                for (int cat = passenger.getCategory().getCategoryId(); cat < Category.values().length; cat++) {
                    int capacity = flight.getCategoryCapacity(Category.getCategoryById(cat));
                    if (capacity > 0) {
                        alternatives.add(new AlternativeFlight(flight, Category.getCategoryById(cat), capacity));
                    }
                }
            }
        }
        if (alternatives.isEmpty()) {
            throw new NoAlternativesException(flightCode, passenger.getName());
        }
        Collections.sort(alternatives);

        return alternatives;
    }


}
