package ar.edu.itba.pod.exceptions;

public class InvalidSeatCategoryException extends RuntimeException{
    private final static String EXC_MSG = "Invalid seat category";

    public InvalidSeatCategoryException(){
            super(EXC_MSG);
        }
}
