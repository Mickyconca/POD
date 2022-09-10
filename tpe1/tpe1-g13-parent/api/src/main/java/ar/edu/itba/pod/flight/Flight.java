package ar.edu.itba.pod.flight;

import ar.edu.itba.pod.exceptions.PassengerAlreadyInFlightException;
import ar.edu.itba.pod.exceptions.PassengerNotFoundException;
import ar.edu.itba.pod.exceptions.SeatNotFoundException;

import java.util.*;

public class Flight {

    private final String flightCode;
    private final String destination;
    private final PlaneModel planeModel;
    private FlightStatus status;
    private Map<String, Seat> seats = new HashMap<>();
    private Map<String, Passenger> passengers;

    public Flight(PlaneModel planeModel, String flightCode, String destination, Map<String, Passenger> passengers) {
        this.flightCode = flightCode;
        this.status = FlightStatus.PENDING;
        this.planeModel = planeModel;
        this.destination = destination;
        this.generateSeats();
        this.passengers = passengers;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public String getDestination() {
        return destination;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public PlaneModel getPlaneModel() {
        return planeModel;
    }

    public Map<String, Seat> getSeats() {
        return seats;
    }

    public void setSeats(Map<String, Seat> seats) {
        this.seats = seats;
    }

    public Passenger getPassenger(String passenger) {
        Passenger p = passengers.get(passenger);
        if (p == null) {
            throw new PassengerNotFoundException();
        }
        return p;
    }

    public Map<String, Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(Map<String, Passenger> passengers) {
        this.passengers = passengers;
    }

    public Seat getSeat(int rowNumber, char colLetter) {
        String key = rowNumber + String.valueOf(colLetter);
        Seat seat = seats.get(key);
        if (seat == null) {
            throw new SeatNotFoundException();
        }
        return seat;
    }

    public void removePassenger(Passenger p) {
        if (passengers.get(p.getName()) != null) {
            passengers.remove(p.getName());
        } else {
            throw new PassengerNotFoundException();
        }
    }

    public void addPassenger(Passenger p) {
        if (passengers.get(p.getName()) == null) {
            passengers.put(p.getName(), p);
        } else {
            throw new PassengerAlreadyInFlightException();
        }
    }

    private void generateSeats() {
        for (Category c : Category.values()) {
            for (int[] seatNumbers : planeModel.categories) {
                for (int i = 1; i <= seatNumbers[0]; i++) {
                    for (int j = 0; j < seatNumbers[1]; j++) {
                        String key = String.valueOf(i) + (char) ('A' + j);
                        seats.put(key, new Seat(i, (char) ('A' + j), c));
                    }
                }
            }
        }
    }

    public int getCategoryCapacity(Category category) {
        int capacity = 0;
        for (Seat seat : seats.values()) {
            if (seat.isEmpty() && seat.getCategory() == category) {
                capacity++;
            }
        }
        return capacity;
    }

}
