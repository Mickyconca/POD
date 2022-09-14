//package ar.edu.itba.pod.server.models;
//
//import ar.edu.itba.pod.exceptions.FlightAlreadyConfirmedException;
//import ar.edu.itba.pod.flight.FlightStatus;
//import ar.edu.itba.pod.services.NotificationsServiceClient;
//import ar.edu.itba.pod.services.NotificationsServiceServer;
//
//import java.util.HashMap;
//import java.util.LinkedList;
//
//public class NotificationsServant implements NotificationsServiceServer {
//    private final Data data;
//
//    public NotificationsServant(Data data) {
//        this.data = data;
//    }
//
//    @Override
//    public void registerPassengerForNotifications(String passengerName, String flightCode, NotificationsServiceClient handler) {
//        Flight flight = data.getFlightByCode(flightCode); //throws exception if doesnt exist
//        if (flight.getStatus() != FlightStatus.CONFIRMED) {
//            flight.getPassenger(passengerName); //throws exc if passenger not in flight
//            data.getPassengersNotifications().putIfAbsent(passengerName, new HashMap<>());
//            data.getPassengersNotifications().get(passengerName).putIfAbsent(flightCode, new LinkedList<>());
//            data.getPassengersNotifications().get(passengerName).get(flightCode).add(handler);
//            handler.onPassengerRegistered(flightCode, flight.getDestination());
//        }else{
//            throw new FlightAlreadyConfirmedException();
//        }
//    }
//
//}
