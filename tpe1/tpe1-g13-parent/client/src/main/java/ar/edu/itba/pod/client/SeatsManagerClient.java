package ar.edu.itba.pod.client;

import ar.edu.itba.pod.exceptions.*;
import ar.edu.itba.pod.services.SeatService;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

import static ar.edu.itba.pod.client.Utils.serverAddressParser;

public class SeatsManagerClient {

    public static void main(String[] args) throws IOException, NotBoundException {
        System.out.println("Seats Manager Client starting..");
        final Properties properties = System.getProperties();

        final Utils.ServerAddress serverAddress;
        try {
            serverAddress = serverAddressParser(Optional.ofNullable(properties.getProperty("serverAddress")).orElseThrow(IllegalArgumentException::new));
        }catch (NumberFormatException e){
            System.out.println("Invalid port number");
            return;
        }

        final String action;
        try{
            action = Optional.ofNullable(properties.getProperty("action")).orElseThrow(IllegalArgumentException::new);
        }catch (IllegalArgumentException e){
            System.out.println("Missing action.");
            return;
        }

        final String flight;
        try{
            flight = Optional.ofNullable(properties.getProperty("flight")).orElseThrow(IllegalArgumentException::new);
        }catch (IllegalArgumentException e){
            System.out.println("Missing flight.");
            return;
        }

        final String passenger = properties.getProperty("passenger");
        final Integer row = Integer.parseInt(properties.getProperty("row"));
        final Character col = properties.getProperty("col").charAt(0);
        final String originalFlight = properties.getProperty("originalFlight");

        final Registry registry = LocateRegistry.getRegistry(serverAddress.getIp(), serverAddress.getPort());
        final SeatService seatService = (SeatService) registry.lookup(SeatService.class.getName());

        runAction(seatService,action, flight, row, col, passenger, originalFlight);
    }

    private static void runAction(SeatService seatService, String action, String flight, Integer row, Character col, String passenger, String originalFlight){
        if(flight == null || action == null) {
           throw new IllegalArgumentException();
        }
        switch (action) {
            case "status":
                if(col == null){
                    System.out.println("Invalid column arguments");
                    break;
                }
                if(Character.isLetter(col)){
                    System.out.println("Invalid column arguments");
                    break;
                }
                if (row == null){
                    System.out.println("Invalid row argument.");
                    break;
                }
                if(row < 0 || row > 25){
                    System.out.println("Invalid row argument.");
                    break;
                }
                getSeatStatus(seatService, flight, row, col, passenger);
                break;
            case "assign":
                if(col == null){
                    System.out.println("Missing column arguments");
                    break;
                }
                if(Character.isLetter(col)){
                    System.out.println("Invalid column arguments");
                    break;
                }
                if (row == null){
                    System.out.println("Missing row argument.");
                    break;
                }
                if(row < 0 || row > 25){
                    System.out.println("Invalid row argument.");
                    break;
                }
                if(passenger == null){
                    System.out.println("Missing passenger name.");
                    break;
                }
                assignSeat(seatService, flight, row, col, passenger);
                break;
            case "move":
                if(col == null){
                    System.out.println("Missing column argument.");
                    break;
                }
                if(Character.isLetter(col)){
                    System.out.println("Invalid column argument.");
                    break;
                }
                if (row == null){
                    System.out.println("Missing row argument.");
                    break;
                }
                if(row < 0 || row > 25){
                    System.out.println("Invalid row argument.");
                    break;
                }
                if(passenger == null){
                    System.out.println("Missing passenger name.");
                    break;
                }
                movePassengerSeat(seatService, flight, row, col, passenger);
                break;
            case "alternatives":
                if(passenger == null){
                    System.out.println("Missing passenger name.");
                    break;
                }else{
                    getAlternatives(seatService, flight, passenger);
                }
                break;
            case "changeTicket":
                if(passenger == null){
                    System.out.println("Missing passenger name.");
                    break;
                }
                if(originalFlight == null){
                    System.out.println("Missing original flight code argument.");
                }
                changeTicket(seatService, originalFlight, flight, passenger);
                break;
            default:
                System.out.println("Invalid action");
                break;
        }
    }

    private static void getSeatStatus(SeatService seatService, String flightCode, int row, char col, String passenger) {
        boolean status;
        try {
             status = seatService.status(flightCode, row, col, passenger);
            System.out.printf("Seat %d%c is " + (status ? "FREE" : "OCCUPIED") + ".%n", row, col);
        } catch(SeatNotFoundException | FlightNotFoundException | RemoteException exception){
            System.out.println(exception.getMessage());
        }
    }

    private static void assignSeat(SeatService seatService, String flightCode, int row, char col, String passenger) {
        try {
            seatService.assign(flightCode, row, col, passenger);
            System.out.printf("Seat %d%c is assigned to %s", row, col, passenger);
        } catch (RemoteException | FlightNotFoundException | PassengerNotFoundException | PassengerWithSeatAlreadyAssignedException | InvalidSeatCategoryException | SeatNotEmptyException exception){
            System.out.println(exception.getMessage());
        }
    }

    private static void movePassengerSeat(SeatService seatService, String flightCode, int row, char col, String passenger) {
        try {
            seatService.move(flightCode, passenger, row, col);
            System.out.printf("Seat %d%c is assigned to %s", row, col, passenger);
        } catch (RemoteException | FlightNotFoundException | PassengerNotFoundException | PassengerWithSeatAlreadyAssignedException | InvalidSeatCategoryException | SeatNotEmptyException exception) {
            System.out.print("Exception");
        }
    }

    private static void getAlternatives(SeatService seatService, String flightCode, String passenger) {
        List<String> alternatives = new LinkedList<>();
        try {
           alternatives = seatService.alternatives(flightCode, passenger);
        } catch (RemoteException | FlightNotFoundException | PassengerNotFoundException exception) {
            System.out.print("Exception");
        }
        for(String alternative: alternatives){
            System.out.println(alternative);
        }
    }

    private static void changeTicket(SeatService seatService, String originalFlightCode, String alternativeFlightCode, String passenger) {
        try {
            seatService.changeTicket(originalFlightCode, alternativeFlightCode, passenger);
            System.out.printf("Your ticket changed to Flight %s from Flight %s.%n.",alternativeFlightCode,originalFlightCode);
        } catch (RemoteException | FlightNotFoundException | PassengerNotFoundException | FlightAlreadyConfirmedException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
