package ar.edu.itba.pod.server;

import ar.edu.itba.pod.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws RemoteException {
        logger.info("tpe1 Server Starting ...");

        final FlightService servant = new Servant();
        final Remote remote = UnicastRemoteObject.exportObject(servant, 0);
        final Registry registry = LocateRegistry.getRegistry();
        registry.rebind(FlightAdminService.class.getName(), remote);
        registry.rebind(NotificationsServiceServer.class.getName(), remote);
        registry.rebind(SeatsQueryService.class.getName(), remote);
        registry.rebind(SeatService.class.getName(), remote);

    }
}