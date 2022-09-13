package ar.edu.itba.pod.client;

import ar.edu.itba.pod.exceptions.*;
import ar.edu.itba.pod.services.SeatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

import static ar.edu.itba.pod.client.Utils.serverAddressParser;

public class SeatsManagerClient {
    private static final Logger logger = LoggerFactory.getLogger(SeatsManagerClient.class);

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

        final String passenger;
        try{
            passenger = Optional.ofNullable(properties.getProperty("passenger")).orElseThrow(IllegalArgumentException::new);
        }catch (IllegalArgumentException e){
            System.out.println("Missing passenger.");
            return;
        }

        final int row;
        try{
            row = Integer.parseInt(Optional.ofNullable(properties.getProperty("row")).orElseThrow(IllegalArgumentException::new));
        }catch (IllegalArgumentException e){
            System.out.println("Missing row.");
            return;
        }

        final char col;
        try{
            col = Optional.ofNullable(properties.getProperty("col")).orElseThrow(IllegalArgumentException::new).toCharArray()[0];
        }catch (IllegalArgumentException e){
            System.out.println("Missing col.");
            return;
        }

        final String originalFlight;
        try{
            originalFlight = Optional.ofNullable(properties.getProperty("originalFlight")).orElseThrow(IllegalArgumentException::new);
        }catch (IllegalArgumentException e){
            System.out.println("Missing originalFlight.");
            return;
        }

        final Registry registry = LocateRegistry.getRegistry(serverAddress.getIp(), serverAddress.getPort());
        final SeatService seatService = (SeatService) registry.lookup("SeatService");

        runAction(seatService,action, flight, row, col, passenger, originalFlight);
    }

    private static void runAction(SeatService seatService, String action, String flight, int row, char col, String passenger, String originalFlight){
        if(flight == null || action == null) {
           throw new IllegalArgumentException();
        }
        switch (action) {
            case "status":
                getSeatStatus(seatService, flight, row, col, passenger);
                break;
            case "assign":
                assignSeat(seatService, flight, row, col, passenger);
                break;
            case "move":
                movePassengerSeat(seatService, flight, row, col, passenger);
                break;
            case "alternatives":
                getAlternatives(seatService, flight, passenger);
                break;
            case "changeTicket":
                changeTicket(seatService, originalFlight, flight, passenger);
                break;
            default:
                System.out.println("Invalid action");
                break;
        }
    }

    private static void getSeatStatus(SeatService seatService, String flightCode, int row, char col, String passenger) {
        boolean status = false;
        try {
             status = seatService.status(flightCode, row, col, passenger);
        } catch(SeatNotFoundException | FlightNotFoundException | RemoteException exception){
            System.out.println(exception.getMessage());
        }
        System.out.printf("Seat %d%c is " + (status ? "FREE" : "OCCUPIED") + ".%n", row, col);
    }

    private static void assignSeat(SeatService seatService, String flightCode, int row, char col, String passenger) {
        try {
            seatService.assign(flightCode, row, col, passenger);
        } catch (RemoteException | FlightNotFoundException | PassengerNotFoundException | PassengerWithSeatAlreadyAssignedException | InvalidSeatCategoryException | SeatNotEmptyException exception){
            System.out.println(exception.getMessage());
        }
        System.out.printf("Seat %d%c is assigned to %s", row, col, passenger);
    }

    private static void movePassengerSeat(SeatService seatService, String flightCode, int row, char col, String passenger) {
        try {
            seatService.move(flightCode, passenger, row, col);
        } catch (RemoteException | FlightNotFoundException | PassengerNotFoundException | PassengerWithSeatAlreadyAssignedException | InvalidSeatCategoryException | SeatNotEmptyException exception) {
            System.out.print("Exception");
        }
        System.out.printf("Seat %d%c is assigned to %s", row, col, passenger);
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
        } catch (RemoteException | FlightNotFoundException | PassengerNotFoundException | FlightAlreadyConfirmedException exception) {
            System.out.println(exception.getMessage());
        }
        System.out.printf("Your ticket changed to Flight %s from Flight %s.%n.",alternativeFlightCode,originalFlightCode);
    }
}
