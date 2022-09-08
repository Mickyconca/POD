package ar.edu.itba.pod.flight;

import java.util.*;

public class Flight {

    private final String flightCode;
    private final String destination;
    private final PlaneModel planeModel;
    private FlightStatus status;
    private Map<Category, List<Seat>> seats = new HashMap<>();
    private Map<Category, Set<String>> passengers;

    public Flight(PlaneModel planeModel, String flightCode, String destination, Map<Category,Set<String>> passengers) {
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

    public void setStatus(FlightStatus status){
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

    public Map<Category, List<Seat>> getSeats() {
        return seats;
    }

    public void setSeats(Map<Category, List<Seat>> seats) {
        this.seats = seats;
    }

    private void generateSeats(){

        for(Category c: Category.values()){
            List<Seat> categorySeats = new ArrayList<>();
            for(int[] seatNumbers: planeModel.categories){
                for(int i=1; i <= seatNumbers[0]; i++){
                    for(int j=0; j < seatNumbers[1]; j++){
                        categorySeats.add(new Seat(i, (char) ('A' + j)));
                    }
                }
            }
            seats.put(c, categorySeats);
        }

    }
}
