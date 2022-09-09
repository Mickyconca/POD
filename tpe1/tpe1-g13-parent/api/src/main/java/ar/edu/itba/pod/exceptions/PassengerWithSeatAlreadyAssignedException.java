package ar.edu.itba.pod.exceptions;

public class PassengerWithSeatAlreadyAssignedException extends RuntimeException{
    private final static String EXC_MSG = "Passenger with seat already assigned";

    public PassengerWithSeatAlreadyAssignedException(){
        super(EXC_MSG);
    }
}
