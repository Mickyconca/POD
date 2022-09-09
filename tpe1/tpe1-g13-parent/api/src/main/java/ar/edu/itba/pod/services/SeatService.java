package ar.edu.itba.pod.services;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SeatService extends Remote {
    boolean status(String flightCode, int rowNumber, char colLetter, String passenger) throws RemoteException; //TODO borrar esto porque se que no va
    void assign(String flightCode, int rowNumber, char colLetter, String passenger) throws RemoteException;
    void move(String flightCode, String passenger, int rowNumber, char colLetter) throws RemoteException; //todo es boolean esto?
    List<String> alternatives(String flightCode, String passenger) throws RemoteException;
    void changeTicket(String originalFlightCode, String alternativeFlightCode, String passenger) throws RemoteException;
}
