package ar.edu.itba.pod.exceptions;

public class DuplicateFlightCodeException extends RuntimeException{
    private final static String EXC_MSG = "Duplicate flight code exception";

    public DuplicateFlightCodeException(){
        super(EXC_MSG);
    }
}