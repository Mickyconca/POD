package ar.edu.itba.pod.exceptions;

public class FlightCancelledException extends RuntimeException{
    private final static String EXC_MSG = "Flight is cancelled.";

    public FlightCancelledException(){
        super(EXC_MSG);
    }
}
