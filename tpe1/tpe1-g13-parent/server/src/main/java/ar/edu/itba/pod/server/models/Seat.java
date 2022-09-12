package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.flight.Category;

public class Seat {

    private int rowNumber;
    private char colLetter;
    private boolean isEmpty;
    private Category category;

    public Seat(int rowNumber, char colLetter, Category category) {
        this.rowNumber = rowNumber;
        this.colLetter = colLetter;
        this.isEmpty = true;
        this.category = category;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
