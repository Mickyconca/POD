package ar.edu.itba.pod.client;

import ar.edu.itba.pod.interfaces.FlightAdminRemoteInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException, InterruptedException {

        System.out.println("Clientt");
        logger.info("tpe1 Client Starting ...");

        FlightAdminRemoteInterface handle = (FlightAdminRemoteInterface) Naming.lookup("//localhost:1099/pod");
        System.out.println(handle.print());
    }
}
