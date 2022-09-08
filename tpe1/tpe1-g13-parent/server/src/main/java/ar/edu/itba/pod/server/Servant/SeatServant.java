package ar.edu.itba.pod.server.Servant;

import ar.edu.itba.pod.services.SeatService;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;

public class SeatServant implements SeatService, Serializable {


    @Override
    public boolean status(String flightCode, int rowNumber, char colLetter, String passenger) throws RemoteException {
        return false;
    }

    @Override
    public void assign(String flightCode, int rowNumber, char colLetter, String passenger) throws RemoteException {

    }

    @Override
    public void move(String flightCode, String passenger, int rowNumber, char colLetter) throws RemoteException {

    }

    @Override
    public List<List<String>> alternatives(String flightCode, String passenger) throws RemoteException {
        return null;
    }

    @Override
    public void changeTicket(String originalFlightCode, String alternativeFlightCode, String passenger) throws RemoteException {

    }
}