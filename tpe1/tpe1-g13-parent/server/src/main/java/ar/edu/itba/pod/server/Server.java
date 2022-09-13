package ar.edu.itba.pod.server;

import ar.edu.itba.pod.services.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {

    public static void main(String[] args) throws RemoteException {
        System.out.println("Server Starting ...");

        final FlightService servant = new Servant();
        final Remote remote = UnicastRemoteObject.exportObject(servant, 0);
        final Registry registry = LocateRegistry.getRegistry();
        registry.rebind(FlightAdminService.class.getName(), remote);
        registry.rebind(NotificationsServiceServer.class.getName(), remote);
        registry.rebind(SeatsQueryService.class.getName(), remote);
        registry.rebind(SeatService.class.getName(), remote);

    }
}