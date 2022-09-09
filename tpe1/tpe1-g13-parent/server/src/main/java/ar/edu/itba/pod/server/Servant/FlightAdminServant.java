package ar.edu.itba.pod.server.Servant;

import ar.edu.itba.pod.exceptions.*;
import ar.edu.itba.pod.flight.*;
import ar.edu.itba.pod.services.FlightAdminService;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FlightAdminServant implements FlightAdminService, Serializable {

    private final Data data;
    private final Object planeModelsLock = new Object();
    private final Object flightsLock = new Object();

    public FlightAdminServant(Data data) {
        this.data = data;
    }


    @Override
    public void registerPlaneModel(String name, int[] businessSeats, int[] premiumSeats, int[] economySeats) throws DuplicateModelException, RemoteException{
        synchronized (planeModelsLock){
            if(data.getPlaneModels().containsKey(name)){
                throw new DuplicateModelException();
            }
            if(Arrays.stream(businessSeats).anyMatch(i -> i < 0) || Arrays.stream(premiumSeats).anyMatch(i -> i < 0) || Arrays.stream(economySeats).anyMatch(i -> i < 0)){
                throw new InvalidModelException();
            }
            this.data.getPlaneModels().put(name, new PlaneModel(name, businessSeats, premiumSeats, economySeats));
        }
    }

    //Boeing 787;AA100;JFK;BUSINESS#John,ECONOMY#Juliet,BUSINESS#Elizabeth
    @Override
    public void registerFlight(String modelName, String flightCode, String destinyAirport, Map<String, Passenger> passengers) throws RemoteException{
        synchronized (flightsLock) {
            if (data.getFlights().containsKey(flightCode)) {
                throw new DuplicateFlightCodeException();
            }
            if (!data.getPlaneModels().containsKey(modelName)) {
                throw new ModelNotFoundException();
            }
            PlaneModel planeModel = data.getPlaneModels().get(modelName);
            int capacity = planeModel.getTotalCapacity();
            if(passengers.size() > capacity){ //todo check this
                throw new InvalidAmountOfPassengersException();
            }
            this.data.getFlights().put(flightCode, new Flight(planeModel, flightCode, destinyAirport, passengers));
        }
    }

    @Override
    public FlightStatus flightStatus(String flightCode) throws RemoteException{
        synchronized(flightsLock){
            if(!data.getFlights().containsKey(flightCode)){
                throw new FlightNotFoundException();
            }
            return data.getFlights().get(flightCode).getStatus();
        }
    }

    @Override
    public void confirmFlight(String flightCode) throws RemoteException{
        synchronized(flightsLock){
            if(!data.getFlights().containsKey(flightCode)){
                throw new FlightNotFoundException();
            }
            data.getFlights().get(flightCode).setStatus(FlightStatus.CONFIRMED);
        }
    }

    @Override
    public void cancelFlight(String flightCode) throws RemoteException{
        synchronized(flightsLock){
            if(!data.getFlights().containsKey(flightCode)){
                throw new FlightNotFoundException();
            }
            data.getFlights().get(flightCode).setStatus(FlightStatus.CANCELLED);
        }
    }




}