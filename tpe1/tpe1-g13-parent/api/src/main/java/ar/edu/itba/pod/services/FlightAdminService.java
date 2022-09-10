package ar.edu.itba.pod.services;

import ar.edu.itba.pod.flight.Category;
import ar.edu.itba.pod.flight.FlightStatus;
import ar.edu.itba.pod.flight.Passenger;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

public interface FlightAdminService extends Remote {

    void registerPlaneModel(String name, int[] businessSeats, int[] premiumSeats, int[] economySeats) throws RemoteException;
    void registerFlight(String modelName, String flightCode, String destinyAirport, Map<String, Passenger> passengers) throws RemoteException;
    FlightStatus flightStatus(String flightCode) throws RemoteException;
    void confirmFlight(String flightCode) throws RemoteException;
    void cancelFlight(String flightCode) throws RemoteException;
    void changeFlightTickets(String flightCode) throws RemoteException;
}
