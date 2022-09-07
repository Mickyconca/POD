package ar.edu.itba.pod.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FlightAdminRemoteInterface extends Remote {

    String print() throws RemoteException;
}
