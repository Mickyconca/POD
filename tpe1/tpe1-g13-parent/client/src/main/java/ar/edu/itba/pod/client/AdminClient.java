package ar.edu.itba.pod.client;

import ar.edu.itba.pod.exceptions.*;
import ar.edu.itba.pod.flight.Category;
import ar.edu.itba.pod.flight.FlightStatus;
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
            return;
        }
        //get action
        final String action;
        try{
            action = Optional.ofNullable(properties.getProperty("action")).orElseThrow(IllegalArgumentException::new);
        }catch (IllegalArgumentException e){
            System.out.println("Missing action.");
            return;
        }

        final String inPath = properties.getProperty("inPath");
        final String flight = properties.getProperty("flight");
        final Registry registry = LocateRegistry.getRegistry(serverAddress.getIp(), serverAddress.getPort());
        FlightAdminService flightAdminService = (FlightAdminService) registry.lookup(FlightAdminService.class.getName());
        runAction(flightAdminService,action, inPath, flight);


    }

    private static void runAction(FlightAdminService flightAdminService, String action, String inPath, String flight){
        switch (action) {
            case "models":
                if(inPath != null){
                    addPlaneModels(flightAdminService, inPath);
                }else{
                    System.out.println("Missing path.");
                }
                break;
            case "flights":
                if(inPath != null){
                    addFlight(flightAdminService, inPath);
                }else{
                    System.out.println("Missing path.");
                }
                break;
            case "status":
                if(flight != null){
                    flightStatus(flightAdminService, flight);
                }else{
                    System.out.println("Missing flight code.");
                }
                break;
            case "confirm":
                if(flight != null){
                    confirmFlight(flightAdminService, flight);
                }else{
                    System.out.println("Missing flight code.");
                }
                break;
            case "cancel":
                if (flight != null){
                    cancelFlight(flightAdminService, flight);
                }else{
                    System.out.println("Missing flight code.");
                }
                break;
            case "reticketing":
                if (flight != null){
                    reticketing(flightAdminService, flight);
                }else{
                    System.out.println("Missing flight code.");
                }
                break;
            default:
                System.out.println("Invalid action");
                break;
        }
    }

    //        Model;Seats
    //        Boeing 787;BUSINESS#2#3,PREMIUM_ECONOMY#3#3,ECONOMY#20#10
    //        Airbus A321;ECONOMY#15#9,PREMIUM_ECONOMY#3#6
    private static void addPlaneModels(FlightAdminService flightAdminService, String inPath){
        List<List<String>> planeModelsLines = new LinkedList<>();
        int modelsAdded = 0;
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
                    System.out.println("Error in file format.");
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
                modelsAdded++;
            } catch (RemoteException exception) {
                System.out.println(exception.getMessage());
            }catch(DuplicateModelException | InvalidModelException exception){
                System.out.println("Cannot add model " + line.get(0) + ".");
            }
        }
        System.out.println(modelsAdded + " models added.");

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
        int flightsAdded = 0;
        for (List<String> flightsLine : flightsLines) {
            Map<Category, Set<String>> tickets = parseTickets(flightsLine.get(3));
            try {
                flightAdminService.registerFlight(flightsLine.get(0), flightsLine.get(1), flightsLine.get(2), tickets);
                flightsAdded++;
            } catch (RemoteException exception) {
                System.out.println(exception.getMessage());
            } catch(DuplicateFlightCodeException | ModelNotFoundException exception){
                System.out.println("Cannot add flight " + flightsLine.get(1));
            }
        }

        System.out.println(flightsAdded + " flights added.");
    }

    private static void flightStatus(FlightAdminService flightAdminService, String flight){
        try{
            FlightStatus status = flightAdminService.flightStatus(flight);
            System.out.println("Flight " + flight + " is " + status.getStatus());
        }catch (RemoteException | FlightNotFoundException exception){
            System.out.println(exception.getMessage());
        }
    }

    private static void confirmFlight(FlightAdminService flightAdminService, String flight){
        try{
            flightAdminService.confirmFlight(flight);
            flightStatus(flightAdminService,flight);
        }catch (RemoteException | FlightNotFoundException exception){
            System.out.println(exception.getMessage());
        }
    }

    private static void cancelFlight(FlightAdminService flightAdminService, String flight){
        try {
            flightAdminService.cancelFlight(flight);
            flightStatus(flightAdminService, flight);
        }catch(RemoteException | FlightNotFoundException ex){
            System.out.println(ex.getMessage());
        }
    }

    private static void reticketing(FlightAdminService flightAdminService, String flight){
        try{
            List<String> ticketsChanged = flightAdminService.changeFlightTickets(flight);
            System.out.println(ticketsChanged.get(0) + " tickets were changed.");
            if(ticketsChanged.size()>1){
                for (String s : ticketsChanged.subList(1, ticketsChanged.size())){
                    System.out.println(s);
                }
            }
        }catch (RemoteException | FlightNotFoundException e ){
            System.out.println(e.getMessage());
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
