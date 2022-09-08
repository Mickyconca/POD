package ar.edu.itba.pod.flight;

public class Seat {

    private int rowNumber;
    private char colLetter;
    public boolean isEmpty;

    public Seat(int rowNumber, char colLetter) {
        this.rowNumber = rowNumber;
        this.colLetter = colLetter;
        this.isEmpty = true;
    }




}
