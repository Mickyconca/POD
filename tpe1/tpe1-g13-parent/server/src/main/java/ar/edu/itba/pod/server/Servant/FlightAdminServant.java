package ar.edu.itba.pod.server.Servant;

import ar.edu.itba.pod.exceptions.*;
import ar.edu.itba.pod.flight.Category;
import ar.edu.itba.pod.flight.Flight;
import ar.edu.itba.pod.flight.FlightStatus;
import ar.edu.itba.pod.flight.PlaneModel;
import ar.edu.itba.pod.services.FlightAdminService;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FlightAdminServant implements FlightAdminService, Serializable {

    private final Map<String, PlaneModel> planeModels = new HashMap<>();
    private final Map<String, Flight> flights = new HashMap<>();

    private final Object planeModelsLock = new Object();
    private final Object flightsLock = new Object();

    public FlightAdminServant() {
    }

    @Override
    public String print() throws RemoteException {
        return "Flight admin";
    }

    @Override
    public void registerPlaneModel(String name, int[] businessSeats, int[] premiumSeats, int[] economySeats) throws DuplicateModelException{
        synchronized (planeModelsLock){
            if(planeModels.containsKey(name)){
                throw new DuplicateModelException();
            }
            if(Arrays.stream(businessSeats).anyMatch(i -> i < 0) || Arrays.stream(premiumSeats).anyMatch(i -> i < 0) || Arrays.stream(economySeats).anyMatch(i -> i < 0)){
                throw new InvalidModelException();
            }
            this.planeModels.put(name, new PlaneModel(name, businessSeats, premiumSeats, economySeats));
        }
    }

    //Boeing 787;AA100;JFK;BUSINESS#John,ECONOMY#Juliet,BUSINESS#Elizabeth
    @Override
    public void registerFlight(String modelName, String flightCode, String destinyAirport, Map<Category, Set<String>> passengers) {
        synchronized (flightsLock) {
            if (flights.containsKey(flightCode)) {
                throw new DuplicateFlightCodeException();
            }
            if (!planeModels.containsKey(modelName)) {
                throw new ModelNotFoundException();
            }
            PlaneModel planeModel = planeModels.get(modelName);
            for (Category category : Category.values()) {
                if (passengers.get(category).size() > planeModel.getCategoryCapacity(category)) {
                    throw new InvalidAmountOfPassengersException();
                }
            }
            this.flights.put(flightCode, new Flight(planeModel, flightCode, destinyAirport, passengers));
        }
    }

    @Override
    public FlightStatus flightStatus(String flightCode) {
        synchronized(flightsLock){
            if(!flights.containsKey(flightCode)){
                throw new FlightNotFoundException();
            }
            return flights.get(flightCode).getStatus();
        }
    }

    @Override
    public void confirmFlight(String flightCode) {
        synchronized(flightsLock){
            if(!flights.containsKey(flightCode)){
                throw new FlightNotFoundException();
            }
            flights.get(flightCode).setStatus(FlightStatus.CONFIRMED);
        }
    }

    @Override
    public void cancelFlight(String flightCode) {
        synchronized(flightsLock){
            if(!flights.containsKey(flightCode)){
                throw new FlightNotFoundException();
            }
            flights.get(flightCode).setStatus(FlightStatus.CANCELLED);
        }
    }


}