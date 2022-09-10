package ar.edu.itba.pod.server.Servant;

import ar.edu.itba.pod.exceptions.PassengerNotFoundException;
import ar.edu.itba.pod.flight.Category;
import ar.edu.itba.pod.flight.Flight;
import ar.edu.itba.pod.flight.Passenger;
import ar.edu.itba.pod.flight.Seat;
import ar.edu.itba.pod.services.SeatsQueryService;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SeatsQueryServant implements SeatsQueryService{ //todo check if we need serializable
    private final Data data;

    public SeatsQueryServant(Data data){
        this.data = data;
    }

    @Override
    public List<List<String>> flightSeats(String flightCode) throws RemoteException {
        Flight flight = data.getFlightByCode(flightCode);
        List<List<String>> results = new LinkedList<>();
        int rowNumber = 0;
        results.add(new LinkedList<>());
        Seat prevSeat = null;
        for(Seat seat : flight.getSeats().values()){
            int seatRow = seat.getRowNumber();
            if(seatRow != rowNumber && prevSeat != null){
                results.get(seatRow - 1).add(prevSeat.getCategory().name());
                results.add(new LinkedList<>());
            }
            StringBuilder data = new StringBuilder(rowNumber + " " + seat.getColLetter() + " ");
            if(seat.isEmpty()){
                data.append("*").append(" ");
            }else{
                Optional<Passenger> passenger = flight.getPassengers().values().stream().filter((Passenger p) -> p.getSeat() == seat).findFirst();
                if(passenger.isPresent()){
                    data.append(passenger.get().getName().charAt(0)).append(" ");
                }else{
                    throw new PassengerNotFoundException();
                }
            }
            results.get(seatRow).add(data.toString());
            prevSeat = seat;
        }
        assert prevSeat != null;
        results.get(results.size()-1).add(prevSeat.getCategory().name());
        return results;
    }

    @Override
    public List<List<String>> flightSeatsBycategory(String flightCode, Category category) throws RemoteException {
        return null;
    }

    @Override
    public List<List<String>> flightSeatsByRow(String flightCode, int rowNumber) throws RemoteException {
        return null;
    }
}
