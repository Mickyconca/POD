package ar.edu.itba.pod.services;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotificationsServiceClient extends Remote {
    void onPassengerRegistered(String flightCode, String destination) throws RemoteException;
    void onSeatAssigned(String category, int row, char col, String flightCode, String destination) throws RemoteException;
    void onSeatChanged(String oldCategory, int oldRow, char oldCol, String category, int row, char col, String flightCode, String destination) throws RemoteException;
    void onStatusChange(String flightCode, String destination, String flightStatus, String category, Integer row, Character col) throws RemoteException;
    void onTicketChange(String oldFlightCode, String oldDestination, String flightCode, String destination) throws RemoteException;

}
