package com.upem.proxyloc.models;

public class MyLocations {


    private int id;
    private double alt;
    private double longi;
    private int statuts;

    public MyLocations() {
    }

    public MyLocations(int id, double alt, double longi, int statuts) {
        this.id = id;
        this.alt = alt;
        this.longi = longi;
        this.statuts = statuts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAlt() {
        return alt;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }

    public int getStatuts() {
        return statuts;
    }

    public void setStatuts(int statuts) {
        this.statuts = statuts;
    }


}
