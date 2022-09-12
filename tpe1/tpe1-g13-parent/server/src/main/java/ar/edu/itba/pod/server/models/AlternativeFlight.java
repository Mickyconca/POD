package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.flight.Category;

public class AlternativeFlight implements Comparable<AlternativeFlight> {
    private final Flight flight;
    private final Category category;
    private final int capacity;

    public AlternativeFlight(Flight flight, Category category, int capacity) {
        this.flight = flight;
        this.category = category;
        this.capacity = capacity;
    }

    @Override
    public int compareTo(AlternativeFlight o) {
        if(category.getCategoryId() > o.category.getCategoryId()){
            return 1;
        } else if (category.getCategoryId() == o.category.getCategoryId()) {
            if(capacity == o.capacity){
                return flight.getFlightCode().compareTo(o.flight.getFlightCode());
            }else if(capacity > o.capacity){
                return 1;
            }
            return -1;
        }
        return -1;
    }

    public Flight getFlight() {
        return flight;
    }

    public Category getCategory(){
        return category;
    }

    @Override
    public String toString() {
        return flight.getDestination() + "\t|\t" + flight.getFlightCode() + "\t|\t" + category + " " + category.getCategory();
    }
}
