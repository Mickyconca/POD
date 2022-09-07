package ar.edu.itba.pod.server.Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FlightAdminRemoteInterface extends Remote {

    String print() throws RemoteException;
}
