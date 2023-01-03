package com.soft.uber;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Order {
    private int price;
    private String kind; //vip & public
    private String car; //kia & serato & skoda
    private double srclat,srclng;
    private double tarlat,tarlng;
    private String Date;
    private int userTelephone;
    private int driverTelephone;
    private String status;

    public Order(int price, String kind, String car, double srclat, double srclng, double tarlat, double tarlng, String date, int user) {
        this.price = price;
        this.kind = kind;
        this.car = car;
        this.srclat = srclat;
        this.srclng = srclng;
        this.tarlat = tarlat;
        this.tarlng = tarlng;
        this.Date=date;
        this.userTelephone=user;
        this.status="sending";
        this.driverTelephone=0;
    }
    public int getUserTelephone() {
        return userTelephone;
    }

    public void setUserTelephone(int userTelephone) {
        this.userTelephone = userTelephone;
    }

    public int getDriverTelephone() {
        return driverTelephone;
    }

    public void setDriverTelephone(int driverTelephone) {
        this.driverTelephone = driverTelephone;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUser(int user) {
        this.userTelephone = user;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public double getSrclat() {
        return srclat;
    }

    public void setSrclat(double srclat) {
        this.srclat = srclat;
    }

    public double getSrclng() {
        return srclng;
    }

    public void setSrclng(double srclng) {
        this.srclng = srclng;
    }

    public double getTarlat() {
        return tarlat;
    }

    public void setTarlat(double tarlat) {
        this.tarlat = tarlat;
    }

    public double getTarlng() {
        return tarlng;
    }

    public void setTarlng(double tarlng) {
        this.tarlng = tarlng;
    }

   public static void InsertOrder(Order order, ConnectivityManager connectivityManager,
                                  FirebaseDatabase database, DatabaseReference myRef,Context context){


        Network activeNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
        if(caps!=null){
            boolean vpnInUse = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
            if (vpnInUse) {
                database = FirebaseDatabase.getInstance("https://taxi-5235a-default-rtdb.firebaseio.com/");
                myRef = database.getReference("Orders");
                String key = myRef.push().getKey();
                myRef.child(key).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "تم اضافة طلبك", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(context, "يرجى المحاولة مرة أخرى", Toast.LENGTH_SHORT).show();
                        System.out.println("error add" + e.getMessage());
                    }
                });


            }
            else
            {

                Toast.makeText(context, "لا يمكن اكمال العملية، لايوجد vpn", Toast.LENGTH_LONG).show();

            }
        }
        else
            Toast.makeText(context, "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();

    }
}
