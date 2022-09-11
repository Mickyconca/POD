package ar.edu.itba.pod.services;

import ar.edu.itba.pod.flight.Category;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SeatsQueryService extends Remote {
    List<List<String>> flightSeats(String flightCode) throws RemoteException;
    List<List<String>> flightSeatsByCategory(String flightCode, Category category) throws RemoteException;
    List<String> flightSeatsByRow(String flightCode, int rowNumber) throws RemoteException;

}
