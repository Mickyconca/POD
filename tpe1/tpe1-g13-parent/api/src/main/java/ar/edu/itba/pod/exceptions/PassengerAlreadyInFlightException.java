package ar.edu.itba.pod.exceptions;

public class PassengerAlreadyInFlightException extends RuntimeException{
    private final static String EXC_MSG = "Passenger already in flight";

    public PassengerAlreadyInFlightException(){
        super(EXC_MSG);
    }
}
