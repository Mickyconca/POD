package ar.edu.itba.pod.client;


import ar.edu.itba.pod.exceptions.*;
import ar.edu.itba.pod.services.NotificationsServiceClient;
import ar.edu.itba.pod.services.SeatsQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.List;

public class NotificationsClient {

    private static final Logger logger = LoggerFactory.getLogger(NotificationsServiceClient.class);

    private List<List<String>> getFlightSeats(NotificationsServiceClient notificationsServiceClient , String flightCode) {
    try {
            seatsQueryService.flightSeats(flightCode);
        } catch (RemoteException | FlightNotFoundException | PassengerNotFoundException exception) {
            logger.error(exception.getMessage());
        }

    }
}
