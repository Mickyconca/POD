package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.Servant.Data;
import ar.edu.itba.pod.server.Servant.NotificationsServant;
import ar.edu.itba.pod.server.Servant.SeatServant;
import ar.edu.itba.pod.services.FlightAdminService;
import ar.edu.itba.pod.server.Servant.FlightAdminServant;
import ar.edu.itba.pod.services.NotificationsServiceServer;
import ar.edu.itba.pod.services.SeatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        logger.info("tpe1 Server Starting ...");
        try {
            Data data = new Data();
            FlightAdminService stubFlightAdmin = new FlightAdminServant(data);
            SeatService stubSeat = new SeatServant(data);
            NotificationsServiceServer stubNotificationsService = new NotificationsServant(data);
            final Remote remoteFlightAdmin = UnicastRemoteObject.exportObject(stubFlightAdmin,0);
            final Remote remoteNotificationsService = UnicastRemoteObject.exportObject(stubNotificationsService,1);
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            registry.rebind("FlightAdminService", remoteFlightAdmin);
            registry.rebind("NotificationsService", remoteNotificationsService);
            //TODO agregar todos los services y ver que onda el port
            System.out.println("Service bound");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}