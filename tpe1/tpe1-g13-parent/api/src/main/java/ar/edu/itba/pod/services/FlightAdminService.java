package ar.edu.itba.pod.services;

import ar.edu.itba.pod.flight.Category;
import ar.edu.itba.pod.flight.FlightStatus;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

public interface FlightAdminService extends Remote {

    String print() throws RemoteException;
    void registerPlaneModel(String name, int[] businessSeats, int[] premiumSeats, int[] economySeats);
    void registerFlight(String modelName, String flightCode, String destinyAirport, Map<Category, Set<String>> passengers);
    FlightStatus flightStatus(String flightCode);
    void confirmFlight(String flightCode);
    void cancelFlight(String flightCode);
}
