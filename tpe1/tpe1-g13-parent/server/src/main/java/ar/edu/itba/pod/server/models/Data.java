package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.exceptions.FlightNotFoundException;
import ar.edu.itba.pod.flight.*;
import ar.edu.itba.pod.services.NotificationsServiceClient;

import java.util.*;

public class Data {
    private Map<String, PlaneModel> planeModels;
    private Map<String, Flight> flights;
    private Map<String, Map<String,List<NotificationsServiceClient>>> passengersNotifications; //Map<passengerName,<flightCode,handler>>

    public Data() {
        this.flights = new HashMap<>();
        this.planeModels = new HashMap<>();
        this.passengersNotifications = new HashMap<>();
    }

    public Flight getFlightByCode(String flightCode){
        Flight flight = flights.get(flightCode);
        if(flight == null){
            throw new FlightNotFoundException();
        }
        return flight;
    }

    public List<AlternativeFlight> getAlternatives(String destination, Passenger passenger){
        List<AlternativeFlight> alternatives = new LinkedList<>();
        for(Flight flight : flights.values()){
            if(flight.getDestination().equals(destination) && flight.getStatus() == FlightStatus.PENDING){
                for(int cat = passenger.getCategory().getCategoryId() ; cat < Category.values().length ; cat++){
                    int capacity = flight.getCategoryCapacity(Category.getCategoryById(cat));
                    if(capacity > 0){
                        alternatives.add(new AlternativeFlight(flight, Category.getCategoryById(cat),capacity));
                    }
                }
            }
        }
        Collections.sort(alternatives);
        return alternatives;
    }

    public Map<String, PlaneModel> getPlaneModels() {
        return planeModels;
    }

    public void setPlaneModels(Map<String, PlaneModel> planeModels) {
        this.planeModels = planeModels;
    }

    public Map<String, Flight> getFlights() {
        return flights;
    }

    public void setFlights(Map<String, Flight> flights) {
        this.flights = flights;
    }

    public Map<String, Map<String, List<NotificationsServiceClient>>> getPassengersNotifications() {
        return passengersNotifications;
    }

    public void setPassengersNotifications(Map<String, Map<String, List<NotificationsServiceClient>>> passengersNotifications) {
        this.passengersNotifications = passengersNotifications;
    }
}
