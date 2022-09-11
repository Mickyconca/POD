package ar.edu.itba.pod.server.Servant;

import ar.edu.itba.pod.flight.Category;

public class Passenger {
    private String name;
    private Category category;
    private Seat seat;

    public Passenger(String name, Category category) {
        this.name = name;
        this.category = category;
        this.seat = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public boolean hasSeatAssigned() {
        return seat != null;
    }
}
