package com.soft.uber;

public class Driver {



    private String name,pass,address,carNumber;
    private int telephone;
    private int lat,lng,evalute;
    private String token;

    public Driver(String name, String pass, String address, String carNumber, int telephone,String token) {
        this.name = name;
        this.pass = pass;
        this.address = address;
        this.carNumber = carNumber;
        this.telephone = telephone;
        this.lat=0;
        this.lng=0;
        this.evalute=0;
        this.token=token;
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

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
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

    public int getEvalute() {
        return evalute;
    }

    public void setEvalute(int evalute) {
        this.evalute = evalute;
    }
}
