package ar.edu.itba.pod.exceptions;

public class FlightNotFoundException extends RuntimeException{
    private final static String EXC_MSG = "Flight not found exception";

    public FlightNotFoundException(){
        super(EXC_MSG);
    }
}
