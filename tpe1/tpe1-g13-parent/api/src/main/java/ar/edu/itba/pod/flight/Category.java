package ar.edu.itba.pod.flight;

public enum Category {
    BUSINESS("BUSINESS", 0),
    PREMIUM("PREMIUM", 1),
    ECONOMY("ECONOMY", 2);

    private final String category;
    private final int id;

    Category(String category, int id) {
        this.category = category;
        this.id = id;
    }

    public int getCategoryId(){
        return id;
    }

    public String getCategory(){
        return category;
    }
}
