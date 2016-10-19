package com.example.dilrajsingh.groceryitems;

/**
 * Created by Dilraj Singh on 12/10/2016.
 */
public class ItemAdded {

    private String name, quant;

    public ItemAdded()
    {}
    public ItemAdded(String name, String quant) {
        this.name = name;
        this.quant = quant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuant() {
        return quant;
    }

    public void setQuant(String quant) {
        this.quant = quant;
    }
}
