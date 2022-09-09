package ar.edu.itba.pod.server.Servant;

import ar.edu.itba.pod.exceptions.*;
import ar.edu.itba.pod.flight.Flight;
import ar.edu.itba.pod.flight.FlightStatus;
import ar.edu.itba.pod.flight.Passenger;
import ar.edu.itba.pod.flight.Seat;
import ar.edu.itba.pod.services.SeatService;
import org.slf4j.Logger;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;

public class SeatServant implements SeatService, Serializable {

    private Data data;

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
            if(passengerInfo.hasSeatAssigned()){
                Seat oldSeat = passengerInfo.getSeat();
                try {
                    passengerInfo.setSeat(null);
                    assign(flightCode, rowNumber, colLetter, passenger);
                }catch (Exception e){
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
        List<Flight> alternativeFlights = data.getAlternatives(f.getDestination());
        List<String> results = new LinkedList<>();
        for(Flight flight: alternativeFlights) {
            results.add("flight.getDestination()" + "\t|\t" + flight.getFlightCode() + "\t|\t" + flight.getSeat() )
        }
    }

    @Override
    public void changeTicket(String originalFlightCode, String alternativeFlightCode, String passenger) throws RemoteException {

    }
}