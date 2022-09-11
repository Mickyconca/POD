package ar.edu.itba.pod.server.Servant;

import ar.edu.itba.pod.exceptions.*;
import ar.edu.itba.pod.flight.FlightStatus;
import ar.edu.itba.pod.services.NotificationsServiceClient;
import ar.edu.itba.pod.services.SeatService;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class SeatServant implements SeatService, Serializable {

    private final Data data;

    public SeatServant(Data data) {
        this.data = data;
    }

    @Override
    public boolean status(String flightCode, int rowNumber, char colLetter, String passenger) throws RemoteException {
        Flight flight = this.data.getFlightByCode(flightCode);
        if (flight != null) {
            Seat seat = flight.getSeat(rowNumber, colLetter);
            if (seat != null) {
                return seat.isEmpty();
            }
            throw new SeatNotFoundException();
        }
        throw new FlightNotFoundException();
    }

    @Override
    public void assign(String flightCode, int rowNumber, char colLetter, String passenger) throws RemoteException {
        Flight flight = this.data.getFlightByCode(flightCode);
        if (flight != null) {
            Passenger passengerInfo = flight.getPassenger(passenger);
            if (passengerInfo.hasSeatAssigned()) {
                throw new PassengerWithSeatAlreadyAssignedException();
            }
            Seat seat = flight.getSeat(rowNumber, colLetter);
            if (seat != null) {
                if (seat.isEmpty() && flight.getStatus() == FlightStatus.PENDING) {
                    if (passengerInfo.getCategory().getCategoryId() >= seat.getCategory().getCategoryId()) {
                        passengerInfo.setSeat(seat);
                        seatAssignedNotification(flight, passengerInfo);
                        return;
                    }
                    throw new InvalidSeatCategoryException();
                }
                throw new SeatNotEmptyException();
            }
        }
    }

    @Override
    public void move(String flightCode, String passenger, int rowNumber, char colLetter) throws RemoteException {
        Flight flight = this.data.getFlightByCode(flightCode);
        Passenger passengerInfo = flight.getPassenger(passenger);
        if (flight.getFlightCode().equals(flightCode)) {
            if (passengerInfo.hasSeatAssigned()) {
                Seat oldSeat = passengerInfo.getSeat();
                try {
                    passengerInfo.setSeat(null);
                    assign(flightCode, rowNumber, colLetter, passenger);
                    seatMovedNotification(flight,passengerInfo,oldSeat);
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
        Flight f = this.data.getFlightByCode(flightCode);
        Passenger p = f.getPassenger(passenger);
        List<AlternativeFlight> alternativeFlights = data.getAlternatives(f.getDestination(), p);
        return alternativeFlights.stream().map(AlternativeFlight::toString).collect(Collectors.toList());
    }

    @Override
    public void changeTicket(String originalFlightCode, String alternativeFlightCode, String passenger) throws RemoteException {
        Flight oldFlight = this.data.getFlightByCode(originalFlightCode);
        Passenger p = oldFlight.getPassenger(passenger);

        if (oldFlight.getStatus() != FlightStatus.CONFIRMED) {

            List<AlternativeFlight> alternativeFlights = data.getAlternatives(oldFlight.getDestination(), p);
            Optional<AlternativeFlight> alternativeFlight = alternativeFlights.stream().filter((AlternativeFlight af) -> af.getFlight().getFlightCode().equals(alternativeFlightCode)).findFirst();
            if (alternativeFlight.isPresent()) {
                if (p.hasSeatAssigned()) {
                    p.getSeat().setEmpty(true);
                    p.setSeat(null);
                }
                oldFlight.removePassenger(p);
                alternativeFlight.get().getFlight().addPassenger(p);
                changeTicketNotification(oldFlight, alternativeFlight.get().getFlight(),p);
            }
            throw new FlightNotFoundException();
        }
        throw new FlightAlreadyConfirmedException();
    }

    private void seatAssignedNotification(Flight flight, Passenger passenger) {
        if (data.getPassengersNotifications().containsKey(passenger.getName()) && data.getPassengersNotifications().get(passenger.getName()).containsKey(flight.getFlightCode())) {
            for (NotificationsServiceClient handler : data.getPassengersNotifications().get(passenger.getName()).get(flight.getFlightCode())) {
                handler.onSeatAssigned(passenger.getSeat().getCategory().getCategory(), passenger.getSeat().getRowNumber(), passenger.getSeat().getColLetter(), flight.getFlightCode(), flight.getDestination());
            }
        }
    }

    private void seatMovedNotification(Flight flight, Passenger passenger, Seat oldSeat){
        if (data.getPassengersNotifications().containsKey(passenger.getName()) && data.getPassengersNotifications().get(passenger.getName()).containsKey(flight.getFlightCode())) {
            for (NotificationsServiceClient handler : data.getPassengersNotifications().get(passenger.getName()).get(flight.getFlightCode())) {
                handler.onSeatChanged(oldSeat.getCategory().getCategory(), oldSeat.getRowNumber(),oldSeat.getColLetter(), passenger.getSeat().getCategory().getCategory(),passenger.getSeat().getRowNumber(), passenger.getSeat().getColLetter(), flight.getFlightCode(), flight.getDestination());
            }
        }
    }

    private void changeTicketNotification(Flight oldFlight, Flight newFlight, Passenger passenger){
        if (data.getPassengersNotifications().containsKey(passenger.getName()) && data.getPassengersNotifications().get(passenger.getName()).containsKey(oldFlight.getFlightCode())) {
            for (NotificationsServiceClient handler : data.getPassengersNotifications().get(passenger.getName()).get(oldFlight.getFlightCode())){
                data.getPassengersNotifications().get(passenger.getName()).remove(oldFlight.getFlightCode());
                data.getPassengersNotifications().get(passenger.getName()).putIfAbsent(newFlight.getFlightCode(), new LinkedList<>());
                data.getPassengersNotifications().get(passenger.getName()).get(newFlight.getFlightCode()).add(handler);
                handler.onTicketChange(oldFlight.getFlightCode(), oldFlight.getDestination(), newFlight.getFlightCode(), newFlight.getDestination());
            }
        }

    }

}
