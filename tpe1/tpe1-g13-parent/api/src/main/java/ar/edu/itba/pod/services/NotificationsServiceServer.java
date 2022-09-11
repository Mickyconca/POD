package ar.edu.itba.pod.services;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotificationsServiceServer extends Remote {
    void registerPassengerForNotifications(String passengerName, String flightCode, NotificationsServiceClient handler) throws RemoteException;
}
