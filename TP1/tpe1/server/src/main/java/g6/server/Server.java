package g6.server;
import g6.server.Interface.FlightAdminRemoteInterface;
import g6.server.Servant.FlightAdminServant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {

        logger.info("tpe1 Server Starting ...");

        try {
            FlightAdminRemoteInterface stub = new FlightAdminServant();
            final Remote remote = UnicastRemoteObject.exportObject(stub,0);
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            registry.rebind("pod", remote);
            System.out.println("Service bound");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
