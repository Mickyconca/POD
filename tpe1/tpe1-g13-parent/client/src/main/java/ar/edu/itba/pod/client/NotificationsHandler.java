package ar.edu.itba.pod.client;

import ar.edu.itba.pod.services.NotificationsServiceClient;


public class NotificationsHandler implements NotificationsServiceClient {
    @Override
    public void onPassengerRegistered(String flightCode, String destination) {
        String notification = String.format("You are following Flight %s with destination %s.", flightCode, destination);
        System.out.println(notification);
    }

    @Override
    public void onSeatAssigned(String category, int row, char col, String flightCode, String destination) {
        String notification = String.format("Your seat is %s %d%c for Flight %s with destination %s.", category, row, col, flightCode, destination);
        System.out.println(notification);
    }

    @Override
    public void onSeatChanged(String oldCategory, int oldRow, char oldCol, String category, int row, char col, String flightCode, String destination) {
        String notification = String.format("Your seat changed to %s %d%c from %s %d%c for Flight %s with destination %s.",
                category, row, col, oldCategory, oldRow, oldCol, flightCode, destination);
        System.out.println(notification);
    }

    @Override
    public void onStatusChange(String flightCode, String destination, String flightStatus, String category, Integer row, Character col) {
        String notification = String.format("Your Flight %s with destination %s was %s and your seat is %s %d%c.", flightCode, destination, flightStatus.toLowerCase(), category, row, col);
        System.out.println(notification);
    }

    @Override
    public void onTicketChange(String oldFlightCode, String oldDestination, String flightCode, String destination) {
        String notification = String.format("Your ticket changed to Flight %s with destination %s from Flight %s with destination %s.", flightCode, destination, oldFlightCode, oldDestination);
        System.out.println(notification);
    }
}
