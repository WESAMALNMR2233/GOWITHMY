package com.soft.uber;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Date;

public class GalleryFragment extends Fragment {

    TableLayout table;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.gallery_fragment,container,false);
        table=(TableLayout) v.findViewById(R.id.listRequest);
       table.setStretchAllColumns(true);
       ArrayList<TableRow> rows=new ArrayList<>();

       /** **/
       ArrayList<String> keys=new ArrayList<String>();
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);

        Network activeNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
        if(caps!=null){
            boolean vpnInUse = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
            if (vpnInUse) {
                database = FirebaseDatabase.getInstance("https://taxi-5235a-default-rtdb.firebaseio.com/");
                myRef = database.getReference("Orders");

                 myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot) {

                         for(DataSnapshot datas:snapshot.getChildren()){
                             String key=datas.getKey();
                             System.out.println("key= "+key);

                             myRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot snapshot) {

                                     if (snapshot.exists()) {

                                         Long userTelephone = snapshot.child("userTelephone").getValue(Long.class);
                                         System.out.println("userTelephone=" + userTelephone);
                                         System.out.println("userTelephone2222=" + UserDrawerActivity.userTelephone);

                                         if (userTelephone==UserDrawerActivity.userTelephone) {
                                             System.out.println("yeeeees");
                                             Long price = snapshot.child("price").getValue(Long.class);
                                             String date = snapshot.child("date").getValue(String.class);
                                             Long driverTel = snapshot.child("driverTelephone").getValue(Long.class);
                                             String status = snapshot.child("status").getValue(String.class);
                                             System.out.println(price+","+date+","+driverTel+","+status);
                                             TableRow row=new TableRow(getContext());
                                             TextView tdate=new TextView(getContext());
                                             tdate.setText(date.substring(0,19));
                                             TextView tprice=new TextView(getContext());
                                             tprice.setText(price.toString());
                                             TextView ttelephone=new TextView(getContext());
                                             ttelephone.setPadding(3,0,0,0);
                                             if(driverTel!=0)
                                             ttelephone.setText(driverTel.toString());
                                             else ttelephone.setText(R.string.not_found);
                                             TextView tstatus=new TextView(getContext());
                                             tstatus.setPadding(3,0,0,0);
                                             tstatus.setText(status);
                                             row.addView(tdate);
                                             row.addView(tprice);
                                             row.addView(ttelephone);
                                             row.addView(tstatus);
                                             table.addView(row);

                                         }}

                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError error) {

                                 }
                             });

                         }

                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError error) {

                     }
                 });

            }
            else
            {

                Toast.makeText(getActivity().getApplicationContext(), "لا يمكن اكمال العملية، لايوجد vpn", Toast.LENGTH_LONG).show();

            }
        }
        else
            Toast.makeText(getActivity().getApplicationContext(), "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();



        return v;
    }
}