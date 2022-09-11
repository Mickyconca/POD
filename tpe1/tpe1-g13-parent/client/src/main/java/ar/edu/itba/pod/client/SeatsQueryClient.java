package ar.edu.itba.pod.client;

import ar.edu.itba.pod.exceptions.FlightNotFoundException;
import ar.edu.itba.pod.exceptions.PassengerNotFoundException;
import ar.edu.itba.pod.services.SeatsQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static ar.edu.itba.pod.client.Utils.serverAddressParser;


public class SeatsQueryClient {
    private static final Logger logger = LoggerFactory.getLogger(SeatsQueryClient.class);

    public static void main(String[] args) throws IOException {
        logger.info("Seats query client starting...");
        final Properties properties = System.getProperties();

        final Utils.ServerAddress serverAddress;
        try {
            serverAddress = serverAddressParser(Optional.ofNullable(properties.getProperty("serverAddress")).orElseThrow(IllegalArgumentException::new));
        }catch (NumberFormatException e){
            logger.error("Invalid port number");
            return;
        }

        final String flightCode;
        try{
            flightCode = Optional.ofNullable(properties.getProperty("action")).orElseThrow(IllegalArgumentException::new);
        }catch (IllegalArgumentException e){
            logger.error("Missing action.");
            return;
        }

        final String passengerName;
        try{
            passengerName = Optional.ofNullable(properties.getProperty("action")).orElseThrow(IllegalArgumentException::new);
        }catch (IllegalArgumentException e){
            logger.error("Missing action.");
            return;
        }

        final String category;
        try{
            category = Optional.ofNullable(properties.getProperty("action")).orElseThrow(IllegalArgumentException::new);
        }catch (IllegalArgumentException e){
            logger.error("Missing action.");
            return;
        }

        final Integer rowNumber;
        try{
            rowNumber = Integer.parseInt(Optional.ofNullable(properties.getProperty("action")).orElseThrow(IllegalArgumentException::new));
        }catch (IllegalArgumentException e){
            logger.error("Missing action.");
            return;
        }

        final String outPath;
        try{
            outPath = Optional.ofNullable(properties.getProperty("action")).orElseThrow(IllegalArgumentException::new);
        }catch (IllegalArgumentException e){
            logger.error("Missing action.");
            return;
        }

        runAction(flightCode, passengerName, category, rowNumber, outPath);



    }

    private static void runAction(String flightCode, String passengerName, String category, Integer rowNumber, String outPath){
        if(category == null && )
    }

}
