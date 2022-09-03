package g6.server;
import g6.server.Interface.FlightAdminRemoteInterface;
import g6.server.Servant.FlightAdminServant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {

        logger.info("tpe1 Server Starting ...");

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            FlightAdminRemoteInterface stub = new FlightAdminServant();
            registry.rebind("pod", stub);
            System.out.println("Service bound");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
