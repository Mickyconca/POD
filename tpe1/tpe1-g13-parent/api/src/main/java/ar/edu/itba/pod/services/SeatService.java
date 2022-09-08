package ar.edu.itba.pod.services;

public interface SeatService {
    boolean isFree(String flightCode, int rowNumber, char colLetter, String passenger); //TODO borrar esto porque se que no va
    void assignSeat(String flightCode, int rowNumber, char colLetter, String passenger);
    void changePassengerSeat(String )

    void changeFlightTicket(String flightCode, String passenger);
}
