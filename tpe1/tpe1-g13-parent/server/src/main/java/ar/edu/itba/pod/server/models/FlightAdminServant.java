// package ar.edu.itba.pod.server.models;
//
//import ar.edu.itba.pod.exceptions.*;
//import ar.edu.itba.pod.flight.*;
//import ar.edu.itba.pod.services.FlightAdminService;
//import ar.edu.itba.pod.services.NotificationsServiceClient;
//
//import java.io.Serializable;
//import java.rmi.RemoteException;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class FlightAdminServant implements FlightAdminService, Serializable {
//
//    private final Data data;
//    private final Object planeModelsLock = new Object();
//    private final Object flightsLock = new Object();
//
//    public FlightAdminServant(Data data) {
//        this.data = data;
//    }
//
//
//    @Override
//    public void registerPlaneModel(String name, int[] businessSeats, int[] premiumSeats, int[] economySeats) throws DuplicateModelException, RemoteException {
//        synchronized (planeModelsLock) {
//            if (data.getPlaneModels().containsKey(name)) {
//                throw new DuplicateModelException();
//            }
//            if (Arrays.stream(businessSeats).anyMatch(i -> i < 0) || Arrays.stream(premiumSeats).anyMatch(i -> i < 0) || Arrays.stream(economySeats).anyMatch(i -> i < 0)) {
//                throw new InvalidModelException();
//            }
//            this.data.getPlaneModels().put(name, new PlaneModel(name, businessSeats, premiumSeats, economySeats));
//        }
//    }
//
//    //Boeing 787;AA100;JFK;BUSINESS#John,ECONOMY#Juliet,BUSINESS#Elizabeth
//    @Override
//    public void registerFlight(String modelName, String flightCode, String destinyAirport, Map<Category, Set<String>> passengers) throws RemoteException {
//        Map<String, Passenger> formattedPassengers = new HashMap<>();
//        for(Map.Entry<Category, Set<String>> entry : passengers.entrySet()){
//            for(String p : entry.getValue()){
//                formattedPassengers.put(p,new Passenger(p,entry.getKey()));
//            }
//        }
//
//        synchronized (flightsLock) {
//            if (data.getFlights().containsKey(flightCode)) {
//                throw new DuplicateFlightCodeException();
//            }
//            if (!data.getPlaneModels().containsKey(modelName)) {
//                throw new ModelNotFoundException();
//            }
//            PlaneModel planeModel = data.getPlaneModels().get(modelName);
//            this.data.getFlights().put(flightCode, new Flight(planeModel, flightCode, destinyAirport, formattedPassengers));
//        }
//    }
//
//    @Override
//    public FlightStatus flightStatus(String flightCode) throws RemoteException {
//        synchronized (flightsLock) {
//            if (!data.getFlights().containsKey(flightCode)) {
//                throw new FlightNotFoundException();
//            }
//            return data.getFlights().get(flightCode).getStatus();
//        }
//    }
//
//    @Override
//    public void confirmFlight(String flightCode) throws RemoteException {
//        synchronized (flightsLock) {
//            if (!data.getFlights().containsKey(flightCode)) {
//                throw new FlightNotFoundException();
//            }
//            data.getFlights().get(flightCode).setStatus(FlightStatus.CONFIRMED);
//            handleStatusChangeNotifications(data.getFlights().get(flightCode));
//        }
//    }
//
//    @Override
//    public void cancelFlight(String flightCode) throws RemoteException {
//        synchronized (flightsLock) {
//            if (!data.getFlights().containsKey(flightCode)) {
//                throw new FlightNotFoundException();
//            }
//            data.getFlights().get(flightCode).setStatus(FlightStatus.CANCELLED);
//            handleStatusChangeNotifications(data.getFlights().get(flightCode));
//        }
//    }
//
//    @Override
//    public void changeFlightTickets(String flightCode) throws RemoteException {
//        Flight flight = data.getFlightByCode(flightCode);
//        List<Passenger> sortedPassengers = flight.getPassengers().values().stream().sorted(Comparator.comparing(Passenger::getName)).collect(Collectors.toList());
//        for (Passenger p : sortedPassengers) {
//            changeTicket(flight, p);
//        }
//    }
//
//    private void changeTicket(Flight originalFlight, Passenger passenger) throws RemoteException {
//        List<AlternativeFlight> alternativeFlights = data.getAlternatives(originalFlight.getDestination(), passenger);
//        if(alternativeFlights.isEmpty()){
//            throw new NoAlternativesException(originalFlight.getFlightCode(), passenger.getName());
//        }
//        //find best alternative
//        AlternativeFlight alternativeFlight = alternativeFlights.get(0);
//        int altCapacity = alternativeFlight.getFlight().getCategoryCapacity(alternativeFlight.getCategory());
//        Category category = alternativeFlight.getCategory();
//        for (AlternativeFlight af : alternativeFlights.stream().filter((AlternativeFlight af) -> af.getCategory() == category).collect(Collectors.toList())) {
//            int newCapacity = af.getFlight().getCategoryCapacity(af.getCategory());
//            if (newCapacity > altCapacity) {
//                alternativeFlight = af;
//                altCapacity = newCapacity;
//            } else if (newCapacity == altCapacity) {
//                if (af.getFlight().getFlightCode().compareTo(alternativeFlight.getFlight().getFlightCode()) < 0) {
//                    alternativeFlight = af;
//                }
//            }
//        }
//
//        if (passenger.hasSeatAssigned()) {
//            passenger.getSeat().setEmpty(true);
//            passenger.setSeat(null);
//        }
//        originalFlight.removePassenger(passenger);
//        alternativeFlight.getFlight().addPassenger(passenger);
//    }
//
//    private void handleStatusChangeNotifications(final Flight flight){
//        List<String> passengersWithNotifications = flight.getPassengers().keySet().stream().filter(key -> data.getPassengersNotifications().containsKey(key)
//                && data.getPassengersNotifications().get(key).containsKey(flight.getFlightCode())).collect(Collectors.toList());
//        Passenger passenger;
//        for (String p : passengersWithNotifications){
//            passenger = flight.getPassenger(p);
//            for(NotificationsServiceClient handler : data.getPassengersNotifications().get(p).get(flight.getFlightCode())){
//                if(passenger.hasSeatAssigned()){
//                    handler.onStatusChange(flight.getFlightCode(), flight.getDestination(), flight.getStatus().getStatus(), passenger.getSeat().getCategory().getCategory(),passenger.getSeat().getRowNumber(),passenger.getSeat().getColLetter());
//                }else{
//                    handler.onStatusChange(flight.getFlightCode(), flight.getDestination(), flight.getStatus().getStatus(), passenger.getCategory().getCategory(),null,null);
//                }
//                if(flight.getStatus() == FlightStatus.CONFIRMED){
//                    data.getPassengersNotifications().get(p).remove(flight.getFlightCode());
//                    if (data.getPassengersNotifications().get(p).isEmpty()){ //todo check if this is ok
//                        data.getPassengersNotifications().remove(p);
//                    }
//                }
//            }
//        }
//    }
//
//
//
//
//}
//
//
//
//
//
//
//
