package ar.edu.itba.pod.exceptions;

public class DuplicateModelException extends RuntimeException{
    private final static String EXC_MSG = "Duplicate model exception";

    public DuplicateModelException(){
        super(EXC_MSG);
    }
}
