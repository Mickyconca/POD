package ar.edu.itba.pod.exceptions;

public class NoAlternativesException extends RuntimeException{
    public NoAlternativesException(String flight, String passenger) {
        super("Cannot find alternative flight for " + passenger +" with Ticket " + flight);
    }
}
