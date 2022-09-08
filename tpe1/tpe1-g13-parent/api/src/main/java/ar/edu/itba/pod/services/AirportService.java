package ar.edu.itba.pod.services;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AirportService extends Remote {

    String print() throws RemoteException;
    void registerFlight();
}
