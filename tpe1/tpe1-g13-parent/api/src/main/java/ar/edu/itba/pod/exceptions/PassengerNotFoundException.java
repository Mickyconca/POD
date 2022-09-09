package ar.edu.itba.pod.exceptions;

public class PassengerNotFoundException extends RuntimeException{
    private final static String EXC_MSG = "Passenger not found";

    public PassengerNotFoundException() {
        super(EXC_MSG);
    }
}
