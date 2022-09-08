package ar.edu.itba.pod.exceptions;

public class InvalidModelException extends RuntimeException{
    private final static String EXC_MSG = "Invalid model exception";

    public InvalidModelException(){
        super(EXC_MSG);
    }
}
