package ar.edu.itba.pod.client;

import ar.edu.itba.pod.exceptions.FlightAlreadyConfirmedException;
import ar.edu.itba.pod.exceptions.FlightNotFoundException;
import ar.edu.itba.pod.exceptions.PassengerNotFoundException;
import ar.edu.itba.pod.services.NotificationsServiceClient;
import ar.edu.itba.pod.services.NotificationsServiceServer;
import ar.edu.itba.pod.services.SeatService;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.Properties;

import static ar.edu.itba.pod.client.Utils.serverAddressParser;

public class NotificationsClient{

    public static void main(String[] args) throws IOException, NotBoundException {
        System.out.println("Notification Client starting..");
        final Properties properties = System.getProperties();

        final Utils.ServerAddress serverAddress;
        try {
            serverAddress = serverAddressParser(Optional.ofNullable(properties.getProperty("serverAddress")).orElseThrow(IllegalArgumentException::new));
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number");
            return;
        }

        final String flight;
        try {
            flight = Optional.ofNullable(properties.getProperty("flight")).orElseThrow(IllegalArgumentException::new);
        } catch (IllegalArgumentException e) {
            System.out.println("Missing flight.");
            return;
        }

        final String passenger;
        try {
            passenger = Optional.ofNullable(properties.getProperty("passenger")).orElseThrow(IllegalArgumentException::new);
        } catch (IllegalArgumentException e) {
            System.out.println("Missing passenger.");
            return;
        }

        final NotificationsServiceServer notificationsServiceServer = (NotificationsServiceServer) Naming.lookup("//" + serverAddress.getServerAddress() + "/" + NotificationsServiceServer.class.getName());
        NotificationsServiceClient notificationsServiceClient = new NotificationsHandler();
        UnicastRemoteObject.exportObject(notificationsServiceClient, 0);

        try{
            notificationsServiceServer.registerPassengerForNotifications(passenger, flight, notificationsServiceClient);
        }catch(RemoteException e){
            UnicastRemoteObject.unexportObject(notificationsServiceClient, true);
            System.out.println("Remote Exception. Error registering client's notifications.");
        }catch(FlightNotFoundException e){
            UnicastRemoteObject.unexportObject(notificationsServiceClient, true);
            System.out.println("Flight not found.");
        }catch (PassengerNotFoundException e){
            UnicastRemoteObject.unexportObject(notificationsServiceClient, true);
            System.out.println("Passenger not found.");
        }catch (FlightAlreadyConfirmedException e){
            UnicastRemoteObject.unexportObject(notificationsServiceClient, true);
            System.out.println("Flight is already confirmed. Cannot sign up for notifications.");
        }

    }


}
