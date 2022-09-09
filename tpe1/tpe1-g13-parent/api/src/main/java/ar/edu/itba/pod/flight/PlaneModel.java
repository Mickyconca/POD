package ar.edu.itba.pod.flight;

import java.util.ArrayList;
import java.util.List;

public class PlaneModel {

    private String name;
    List<int[]> categories = new ArrayList<>();
    private int[] businessSeats;
    private int[] premiumSeats;
    private int[] economySeats;

    public PlaneModel(String name, int[] businessSeats, int[] premiumSeats, int[] economySeats) {
        this.name = name;
        this.businessSeats = businessSeats;
        this.premiumSeats = premiumSeats;
        this.economySeats = economySeats;
        this.categories.add(businessSeats);
        this.categories.add(premiumSeats);
        this.categories.add(economySeats);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalCapacity(){
        return getCategoryCapacity(Category.BUSINESS) + getCategoryCapacity(Category.PREMIUM) + getCategoryCapacity(Category.ECONOMY);
    }

    public int getCategoryCapacity(Category category){
        if(category == Category.BUSINESS){
            return businessSeats[0] * businessSeats[1];
        }else if(category == Category.ECONOMY) {
            return economySeats[0] * economySeats[1];
        }else{
            return premiumSeats[0] * premiumSeats[1];
        }
    }

    public int[] getBusinessSeats() {
        return businessSeats;
    }

    public void setBusinessSeats(int[] businessSeats) {
        this.businessSeats = businessSeats;
    }

    public int[] getPremiumSeats() {
        return premiumSeats;
    }

    public void setPremiumSeats(int[] premiumSeats) {
        this.premiumSeats = premiumSeats;
    }

    public int[] getEconomySeats() {
        return economySeats;
    }

    public void setEconomySeats(int[] economySeats) {
        this.economySeats = economySeats;
    }
}
