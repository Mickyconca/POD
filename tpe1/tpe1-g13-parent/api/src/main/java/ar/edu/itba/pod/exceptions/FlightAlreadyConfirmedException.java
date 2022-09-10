package ar.edu.itba.pod.exceptions;

public class FlightAlreadyConfirmedException extends RuntimeException{
    private final static String EXC_MSG = "Flight already confirmed";

    public FlightAlreadyConfirmedException(){
        super(EXC_MSG);
    }
}
