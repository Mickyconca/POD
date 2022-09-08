package ar.edu.itba.pod.exceptions;

public class ModelNotFoundException extends RuntimeException{
        private final static String EXC_MSG = "Model not found exception";

    public ModelNotFoundException(){
        super(EXC_MSG);
    }
}
