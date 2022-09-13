package ar.edu.itba.pod.client;

import ar.edu.itba.pod.services.NotificationsServiceClient;
import ar.edu.itba.pod.services.NotificationsServiceServer;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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

        final Registry registry = LocateRegistry.getRegistry(serverAddress.getIp(), serverAddress.getPort());
        NotificationsServiceClient notificationsHandler = new NotificationsHandler();
        NotificationsServiceServer notificationsService = (NotificationsServiceServer) registry.lookup("NotificationsServiceServer");
        notificationsService.registerPassengerForNotifications(passenger, flight, notificationsHandler);

    }

}
