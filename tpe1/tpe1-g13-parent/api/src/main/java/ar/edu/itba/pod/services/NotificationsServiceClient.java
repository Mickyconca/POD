package ar.edu.itba.pod.services;


import java.rmi.Remote;

public interface NotificationsServiceClient extends Remote {
    void onPassengerRegistered(String flightCode, String destination);
    void onSeatAssigned(String category, int row, char col, String flightCode, String destination);
    void onSeatChanged(String oldCategory, int oldRow, char oldCol, String category, int row, char col, String flightCode, String destination);
    void onStatusChange(String flightCode, String destination, String flightStatus, String category, Integer row, Character col);
    void onTicketChange(String oldFlightCode, String oldDestination, String flightCode, String destination);

}
