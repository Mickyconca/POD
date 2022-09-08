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

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public char getColLetter() {
        return colLetter;
    }

    public void setColLetter(char colLetter) {
        this.colLetter = colLetter;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }
}
