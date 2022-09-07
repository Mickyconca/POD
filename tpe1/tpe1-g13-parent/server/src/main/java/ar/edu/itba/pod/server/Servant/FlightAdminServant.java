package ar.edu.itba.pod.server.Servant;

import ar.edu.itba.pod.interfaces.FlightAdminRemoteInterface;

import java.io.Serializable;
import java.rmi.RemoteException;

public class FlightAdminServant implements FlightAdminRemoteInterface, Serializable {

    public FlightAdminServant() {
    }

    @Override
    public String print() throws RemoteException {
        return "Flight admin";
    }
}