package ar.edu.itba.pod.client;

import ar.edu.itba.pod.exceptions.FlightNotFoundException;
import ar.edu.itba.pod.flight.Category;
import ar.edu.itba.pod.services.SeatsQueryService;
import com.opencsv.CSVWriter;

import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.stream.Collectors;

import static ar.edu.itba.pod.client.Utils.serverAddressParser;


public class SeatsQueryClient {

    public static void main(String[] args) throws IOException, NotBoundException {
        System.out.println("Seats query client starting...");
        final Properties properties = System.getProperties();

        final Utils.ServerAddress serverAddress;
        try {
            serverAddress = serverAddressParser(Optional.ofNullable(properties.getProperty("serverAddress")).orElseThrow(IllegalArgumentException::new));
        }catch (NumberFormatException e){
            System.out.println("Invalid port number");
            return;
        }

        final String flightCode;
        try{
            flightCode = Optional.ofNullable(properties.getProperty("action")).orElseThrow(IllegalArgumentException::new);
        }catch (IllegalArgumentException e){
            System.out.println("Missing action.");
            return;
        }

        final String category = properties.getProperty("action");

        final int rowNumber = Integer.parseInt(properties.getProperty("action"));

        final String outPath;
        try{
            outPath = Optional.ofNullable(properties.getProperty("action")).orElseThrow(IllegalArgumentException::new);
        }catch (IllegalArgumentException e){
            System.out.println("Missing action.");
            return;
        }

        final Registry registry = LocateRegistry.getRegistry(serverAddress.getIp(), serverAddress.getPort());
        SeatsQueryService seatsQueryService =  (SeatsQueryService) registry.lookup("SeatsQueryService");
        runAction(seatsQueryService, flightCode, category, rowNumber, outPath);

    }

    private static void runAction(SeatsQueryService seatsQueryService, String flightCode, String category, Integer rowNumber, String outPath){
        List<List<String>> results = new ArrayList<>();

        if(category == null && rowNumber == null){
            try {
                results = seatsQueryService.flightSeats(flightCode);
            }catch(RemoteException | FlightNotFoundException exception){
                System.out.println(exception.getMessage());
            }
        } else if (category != null && rowNumber == null) {

            Category cat = null;
            try{
                cat = Category.valueOf(category);
            }catch (IllegalArgumentException ex){
                System.out.println("Invalid category");
            }

            try{
                results = seatsQueryService.flightSeatsByCategory(flightCode, cat);
            }catch(RemoteException | FlightNotFoundException ex ){
                System.out.println(ex.getMessage());
            }
        }else if (category == null){
            try{
                results.add(seatsQueryService.flightSeatsByRow(flightCode, rowNumber));
            }catch(RemoteException | FlightNotFoundException ex){
                System.out.println(ex.getMessage());
            }
        }else{
            System.out.println("Invalid amount if arguments.");
            return;
        }
        exportToCSV(results, outPath);
    }

    private static void exportToCSV(List<List<String>> results, String outPath){
        File file = new File(outPath);
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);
            List<String[]> lines = results.stream().map(line -> line.toArray(new String[0])).collect(Collectors.toCollection(LinkedList::new));
            writer.writeAll(lines);
            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
