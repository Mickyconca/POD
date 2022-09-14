package ar.edu.itba.pod.services;

import ar.edu.itba.pod.flight.Category;
import ar.edu.itba.pod.flight.FlightStatus;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FlightAdminService extends Remote {

    void registerPlaneModel(String name, int[] businessSeats, int[] premiumSeats, int[] economySeats) throws RemoteException;
    void registerFlight(String modelName, String flightCode, String destinyAirport, Map<Category, Set<String>> passengers) throws RemoteException;
    FlightStatus flightStatus(String flightCode) throws RemoteException;
    void confirmFlight(String flightCode) throws RemoteException;
    void cancelFlight(String flightCode) throws RemoteException;
    List<String> changeFlightTickets(String flightCode) throws RemoteException;
}
