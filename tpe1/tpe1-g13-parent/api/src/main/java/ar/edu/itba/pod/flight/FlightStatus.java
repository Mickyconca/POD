package ar.edu.itba.pod.flight;

public enum FlightStatus {
    PENDING("PENDING", 0),
    CONFIRMED("CONFIRMED", 1),
    CANCELLED("CANCELLED", 2);
    
    private final String status;
    private final int code;

    FlightStatus(String status, int code) {
        this.status = status;
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }
}
