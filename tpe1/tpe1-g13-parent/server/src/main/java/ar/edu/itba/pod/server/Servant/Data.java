package ar.edu.itba.pod.server.Servant;

import ar.edu.itba.pod.exceptions.FlightNotFoundException;
import ar.edu.itba.pod.flight.Flight;
import ar.edu.itba.pod.flight.FlightStatus;
import ar.edu.itba.pod.flight.PlaneModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Data {
    private Map<String, PlaneModel> planeModels;
    private Map<String, Flight> flights;

    public Data() {
        this.flights = new HashMap<>();
        this.planeModels = new HashMap<>();
    }

    public Flight getFlightByCode(String flightCode){
        Flight flight = flights.get(flightCode);
        if(flight == null){
            throw new FlightNotFoundException();
        }
        return flight;
    }

    public List<Flight> getAlternatives(String destination){
        return flights.values().stream().filter(f ->
                f.getDestination().equals(destination) && f.getStatus() == FlightStatus.PENDING)
                .collect(Collectors.toList());
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
}
