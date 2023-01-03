package com.soft.uber;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;


public class UserProfileFragment extends Fragment {
    EditText name,phone,address,password;
    Button btn;
    FirebaseDatabase database;
    DatabaseReference myRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_user_profile, container, false);
        name=(EditText) v.findViewById(R.id.pname);
        password=(EditText) v.findViewById(R.id.ppassword);

        phone=(EditText)v.findViewById(R.id.ptelephone);
        address=(EditText)v.findViewById(R.id.paddress);
        phone.setText(String.valueOf(UserDrawerActivity.userTelephone));

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);

        Network activeNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
        if(caps!=null){
            boolean vpnInUse = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
            if (vpnInUse) {
                database = FirebaseDatabase.getInstance("https://taxi-5235a-default-rtdb.firebaseio.com/");
                myRef = database.getReference("Users");
                myRef.child(String.valueOf(UserDrawerActivity.userTelephone)).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            password.setText(snapshot.child("pass").getValue(String.class));
                            name.setText(snapshot.child("name").getValue(String.class));
                            address.setText(snapshot.child("address").getValue(String.class));

                        } }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }  );




            }
                   else
                {

                    Toast.makeText(getActivity().getApplicationContext(), "لا يمكن اكمال العملية، لايوجد vpn", Toast.LENGTH_LONG).show();

                }
            }
            else
                Toast.makeText(getActivity().getApplicationContext(), "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();


                //.....................................................................
                btn=(Button)v.findViewById(R.id.pfloatingActionButton);

        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);

                        Network activeNetwork = connectivityManager.getActiveNetwork();
                        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
                        if(caps!=null){
                            boolean vpnInUse = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
                            if (vpnInUse) {
                                database = FirebaseDatabase.getInstance("https://taxi-5235a-default-rtdb.firebaseio.com/");
                                myRef = database.getReference("Users");


                                String uname = name.getText().toString();
                                String upass = password.getText().toString();
                                int tel = Integer.valueOf(phone.getText().toString());
                                String uaddress = address.getText().toString();
                                int lat = 0;
                                int lng = 0;
                                User user = new User(uname, upass, uaddress, tel, lat, lng);
                                myRef.child(String.valueOf(tel)).setValue(
                                        user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(v.getContext(), "تم حفظ التعديلات", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Toast.makeText(v.getContext(), "يرجى المحاولة مرة أخرى", Toast.LENGTH_SHORT).show();
                                        System.out.println("error add" + e.getMessage());
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

                    }
                }
        );
        return v;
    }

}
