package ar.edu.itba.pod.exceptions;

public class SeatNotFoundException extends RuntimeException {
    private final static String EXC_MSG = "Seat not found exception";

    public SeatNotFoundException() {
        super(EXC_MSG);
    }
}
