package ar.edu.itba.pod.client;


import ar.edu.itba.pod.exceptions.*;
import ar.edu.itba.pod.services.NotificationsServiceClient;
import ar.edu.itba.pod.services.NotificationsServiceServer;
import ar.edu.itba.pod.services.SeatsQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static ar.edu.itba.pod.client.Utils.serverAddressParser;

public class NotificationsClient{

    private static final Logger logger = LoggerFactory.getLogger(NotificationsClient.class);

        public static void main(String[] args) throws IOException, NotBoundException {
            logger.info("Notification Client starting..");
            final Properties properties = System.getProperties();

            final Utils.ServerAddress serverAddress;
            try {
                serverAddress = serverAddressParser(Optional.ofNullable(properties.getProperty("serverAddress")).orElseThrow(IllegalArgumentException::new));
            } catch (NumberFormatException e) {
                logger.error("Invalid port number");
                return;
            }

            final String flight;
            try {
                flight = Optional.ofNullable(properties.getProperty("flight")).orElseThrow(IllegalArgumentException::new);
            } catch (IllegalArgumentException e) {
                logger.error("Missing flight.");
                return;
            }

            final String passenger;
            try {
                passenger = Optional.ofNullable(properties.getProperty("passenger")).orElseThrow(IllegalArgumentException::new);
            } catch (IllegalArgumentException e) {
                logger.error("Missing passenger.");
                return;
            }

            final Registry registry = LocateRegistry.getRegistry(serverAddress.getIp(), serverAddress.getPort());
            NotificationsServiceClient notificationsHandler = new NotificationsHandler();
            NotificationsServiceServer notificationsService = (NotificationsServiceServer) registry.lookup("NotificationsService");
            notificationsService.registerPassengerForNotifications(passenger, flight, notificationsHandler);
            
    }

}
