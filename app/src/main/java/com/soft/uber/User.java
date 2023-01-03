package com.soft.uber;

public class User {


   private String name,pass,address;
    private int telephone;
    private int lat,lng;

    public User( String name, String pass, String address, int telephone, int lat, int lng) {

        this.name = name;
        this.pass = pass;
        this.address = address;
        this.telephone = telephone;
        this.lat = lat;
        this.lng = lng;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    public int getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public int getLng() {
        return lng;
    }

    public void setLng(int lng) {
        this.lng = lng;
    }
}
