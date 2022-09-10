package ar.edu.itba.pod.exceptions;

public class NoAlternativesException extends RuntimeException{
    private final static String EXC_MSG = "No alternatives exception";

    public NoAlternativesException(){
        super(EXC_MSG);
    }
}
