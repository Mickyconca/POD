package ar.edu.itba.pod.server.Servant;

import ar.edu.itba.pod.exceptions.PassengerNotFoundException;
import ar.edu.itba.pod.flight.Category;
import ar.edu.itba.pod.flight.Flight;
import ar.edu.itba.pod.flight.Passenger;
import ar.edu.itba.pod.flight.Seat;
import ar.edu.itba.pod.services.SeatsQueryService;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
        for(Map.Entry<Integer, Map<Character, Seat>> rows : flight.getSeats().entrySet()){
            results.add(new LinkedList<>());
            for(Seat seat : rows.getValue().values()){
                results.get(rows.getKey()).add(seatInfoToString(flight, seat));
            }
            Category rowCategory = rows.getValue().get('A').getCategory();
            results.get(rows.getKey()).add(rowCategory.getCategory());
        }
        return results;
    }

    @Override
    public List<List<String>> flightSeatsByCategory(String flightCode, Category category) throws RemoteException {
        Flight flight = data.getFlightByCode(flightCode);
        int[] categoryRowIndexes = flight.getPlaneModel().getCategoryRowIndexes(category);
        List<List<String>> results = new LinkedList<>();
        for (int i = categoryRowIndexes[0] ; i <= categoryRowIndexes[1] ; i++){
            results.add(new LinkedList<>());
            for(Seat seat : flight.getSeats().get(i).values()){
                results.get(i).add(seatInfoToString(flight, seat));
            }
            Category rowCategory = flight.getSeat(i,'A').getCategory();
            results.get(i).add(rowCategory.getCategory());
        }
        return results;
    }

    @Override
    public List<String> flightSeatsByRow(String flightCode, int rowNumber) throws RemoteException {
        Flight flight = data.getFlightByCode(flightCode);
        List<String> results = new LinkedList<>();
        for(Seat seat : flight.getSeats().get(rowNumber).values()){
            results.add(seatInfoToString(flight, seat));
        }
        results.add(flight.getSeats().get(rowNumber).get('A').getCategory().getCategory());
        return results;
    }

    private String seatInfoToString(Flight flight, Seat seat) {
        StringBuilder data = new StringBuilder(seat.getRowNumber() + " " + seat.getColLetter() + " ");
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
        return data.toString();
    }

}
