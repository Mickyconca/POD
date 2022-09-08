package ar.edu.itba.pod.exceptions;

public class InvalidAmountOfPassengersException extends RuntimeException{
        private final static String EXC_MSG = "Invalid amount of passengers exception";

    public InvalidAmountOfPassengersException(){
        super(EXC_MSG);
    }
}
