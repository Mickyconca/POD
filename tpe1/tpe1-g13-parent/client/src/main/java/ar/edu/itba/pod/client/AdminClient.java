package ar.edu.itba.pod.client;

import ar.edu.itba.pod.exceptions.DuplicateFlightCodeException;
import ar.edu.itba.pod.exceptions.DuplicateModelException;
import ar.edu.itba.pod.exceptions.InvalidModelException;
import ar.edu.itba.pod.exceptions.ModelNotFoundException;
import ar.edu.itba.pod.flight.Category;
import ar.edu.itba.pod.services.FlightAdminService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.stream.Collectors;

import static ar.edu.itba.pod.client.Utils.serverAddressParser;

//./run-admin -DserverAddress=10.6.0.1:1099 -Daction=models -DinPath=../modelos.csv
//./run-admin -DserverAddress=10.6.0.1:1099 -Daction=flights-DinPath=../vuelos.csv

public class AdminClient {

    public static void main(String[] args) throws IOException, NotBoundException{
        System.out.println("Flight Admin Client starting..");
        final Properties properties = System.getProperties();
        //get server address & port
        final Utils.ServerAddress serverAddress;
        try {
            serverAddress = serverAddressParser(Optional.ofNullable(properties.getProperty("serverAddress")).orElseThrow(IllegalArgumentException::new));
        }catch (NumberFormatException e){
            System.out.println("Invalid port number");
            System.out.println("Invalid port number");
            return;
        }
        //get action
        final String action;
        try{
            action = Optional.ofNullable(properties.getProperty("action")).orElseThrow(IllegalArgumentException::new);
        }catch (IllegalArgumentException e){
            System.out.println("Missing action.");
            System.out.println("Missing action.");
            return;
        }

        final String inPath;
        try{
            inPath=Optional.ofNullable(properties.getProperty("inPath"))
                    .orElseThrow(IllegalArgumentException::new);
        }catch (IllegalArgumentException ex){
            System.out.println("Missing input file");
            System.out.println("Missing input file");
            return;
        }
        final Registry registry = LocateRegistry.getRegistry(serverAddress.getIp(), serverAddress.getPort());
        FlightAdminService flightAdminService = (FlightAdminService) registry.lookup(FlightAdminService.class.getName());
        runAction(flightAdminService,action, inPath);


    }

    private static void runAction(FlightAdminService flightAdminService, String action, String inPath){
        //todo check if path is null or action is null depending on action

        switch (action) {
            case "models":
                addPlaneModels(flightAdminService, inPath);
                break;
            case "flights":
                addFlight(flightAdminService, inPath);
                break;
            case "status":
                //status fn
                break;
            case "confirm":
                //confirm fn
                break;
            case "cancel":
                //cancel fn
                break;
            case "reticketing":
                //reticketing fn
                break;
            default:
                System.out.println("Invalid action");
                break;
        }
    }

    private static void addPlaneModels(FlightAdminService flightAdminService, String inPath){
//        Model;Seats
//        Boeing 787;BUSINESS#2#3,PREMIUM_ECONOMY#3#3,ECONOMY#20#10
//        Airbus A321;ECONOMY#15#9,PREMIUM_ECONOMY#3#6
        List<List<String>> planeModelsLines = new LinkedList<>();
        try{
            planeModelsLines = Files.readAllLines(Paths.get(inPath)).stream().skip(1)
                    .map(line->Arrays.asList(line.split(";")))
                    .collect(Collectors.toList());
        }catch (IOException e){
            System.out.printf("Error parsing file %s\n", inPath);
        }
        for (List<String> line : planeModelsLines){
            //parse list of seats
            String[] seatsInfo = line.get(1).split(",");
            int[] businessSeats = new int[2];
            int[] premiumSeats = new int[2];
            int[] economySeats = new int[2];
            for(String info : seatsInfo){
                String[] tokens = info.split("#");
                if(tokens.length > 3){
                    System.out.println("Error in file"); //todo complete error
                }
                switch (tokens[0]) {
                    case "BUSINESS":
                        businessSeats[0] = Integer.parseInt(tokens[1]);
                        businessSeats[1] = Integer.parseInt(tokens[2]);
                        break;
                    case "PREMIUM_ECONOMY":
                        premiumSeats[0] = Integer.parseInt(tokens[1]);
                        premiumSeats[1] = Integer.parseInt(tokens[2]);
                        break;
                    case "ECONOMY":
                        economySeats[0] = Integer.parseInt(tokens[1]);
                        economySeats[1] = Integer.parseInt(tokens[2]);
                        break;
                }
            }
            try {
                flightAdminService.registerPlaneModel(line.get(0),businessSeats, premiumSeats, economySeats);
            } catch (RemoteException | DuplicateModelException | InvalidModelException exception) {
                System.out.println(exception.getMessage());
            }
        }

    }

    private static void addFlight(FlightAdminService flightAdminService, String inPath){
        // flightModel[0] = Boeing 787; AA100; JFK; BUSINESS#John,ECONOMY#Juliet,BUSINESS#Elizabeth
        // flightModel[0][0] = Boeing 787

        List<List<String>> flightsLines = new LinkedList<>();
        try{
            flightsLines = Files.readAllLines(Paths.get(inPath)).stream().skip(1)
                    .map(line->Arrays.asList(line.split(";")))
                    .collect(Collectors.toList());
        }catch(IOException e) {
            System.out.printf("Error parsing file %s\n", inPath);
        }

        for (List<String> flightsLine : flightsLines) {
            Map<Category, Set<String>> tickets = parseTickets(flightsLine.get(3));
            // TODO fix catch
            try {
                flightAdminService.registerFlight(flightsLine.get(0), flightsLine.get(1), flightsLine.get(2), tickets);
            } catch (RemoteException | DuplicateFlightCodeException | ModelNotFoundException exception) {
                System.out.println(exception.getMessage());
            }
        }

    }

    static private Map<Category, Set<String>> parseTickets(String passLine){
        String[] pass = passLine.split(",");
        Map<Category, Set<String>> passengers = new HashMap<>();
        Set<String> businessPass = new HashSet<>();
        Set<String> premiumPass = new HashSet<>();
        Set<String> economyPass = new HashSet<>();

        for(String aux : pass){
            String[] token = aux.split("#");
            if(token[0].equals("BUSINESS")){
                businessPass.add(token[1]);
            }else if(token[0].equals("PREMIUM_ECONOMY")){
                premiumPass.add(token[1]);
            }else{
                economyPass.add(token[1]);
            }
        }
        passengers.put(Category.BUSINESS, businessPass);
        passengers.put(Category.PREMIUM, premiumPass);
        passengers.put(Category.ECONOMY, economyPass);

        return passengers;

    }

}
