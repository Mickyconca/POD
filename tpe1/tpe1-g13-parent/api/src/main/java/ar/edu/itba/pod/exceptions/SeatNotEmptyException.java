package ar.edu.itba.pod.exceptions;

public class SeatNotEmptyException extends RuntimeException{
    private final static String EXC_MSG = "Seat is already assigned";

    public SeatNotEmptyException() {
        super(EXC_MSG);
    }
}
