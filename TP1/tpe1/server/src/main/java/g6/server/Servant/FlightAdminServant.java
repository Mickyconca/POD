package g6.server.Servant;

import g6.server.Interface.FlightAdminRemoteInterface;

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
