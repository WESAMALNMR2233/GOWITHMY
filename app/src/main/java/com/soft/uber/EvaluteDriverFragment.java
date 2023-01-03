package com.soft.uber;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class EvaluteDriverFragment extends Fragment {

    private String mParam1;
    private String mParam2;
    Button save;
    EditText editEva;
    FirebaseDatabase database;
    DatabaseReference myRef;
    static ArrayList<String> names = new ArrayList<String>();
    static ArrayList<String> telephones = new ArrayList<String>();

    Spinner spin;
    String selected = "";
    int selectedIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_evalute_driver, container, false);
        editEva=(EditText) v.findViewById(R.id.editTextEva);
        save=(Button)v.findViewById(R.id.buttonEva);
        getDriversName();
        getDriversTelephones();
        spin = v.findViewById(R.id.spinnerEva);
        ArrayAdapter<String> ad
                = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                names);
        spin.setAdapter(ad);


        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("driver name= " + (String) parent.getItemAtPosition(position));
                selected = (String) parent.getItemAtPosition(position);
                selectedIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String evalute=editEva.getText().toString();
                ConnectivityManager connectivityManager = (ConnectivityManager)
                        getActivity().getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
                Network activeNetwork = connectivityManager.getActiveNetwork();
                NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
                if (caps != null) {
                    boolean vpnInUse = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
                    if (vpnInUse) {

                        database = FirebaseDatabase.getInstance("https://taxi-5235a-default-rtdb.firebaseio.com/");
                        myRef = database.getReference("Drivers");
                        if (selectedIndex == 0) {
                            Toast.makeText(getContext(), "يجب اختيار سائق", Toast.LENGTH_LONG).show();

                        } else {
                            int e=Integer.valueOf(evalute);
                            System.out.println("eva= "+e);
                            if(!(e>=0 && e<=5))
                                Toast.makeText(getContext(), "  يرجى التقييم من 5", Toast.LENGTH_LONG).show();

                            else {
                                myRef.child(telephones.get(selectedIndex)).child("evalute").setValue(e);
                                Toast.makeText(getContext(), "شكرا لك لتقييمك", Toast.LENGTH_LONG).show();
                            }

                        }


                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "لا يمكن اكمال العملية، لايوجد vpn", Toast.LENGTH_LONG).show();

                    }
                } else
                    Toast.makeText(getActivity().getApplicationContext(), "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();

            }

        });
        return v;
    }

    void getDriversName() {
        names.clear();
        names.add("اسم السائق");
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);

        Network activeNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
        if (caps != null) {
            boolean vpnInUse = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
            if (vpnInUse) {

                database = FirebaseDatabase.getInstance("https://taxi-5235a-default-rtdb.firebaseio.com/");
                myRef = database.getReference("Drivers");

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot sn : snapshot.getChildren()) {

                            String drivername = sn.child("name").getValue().toString();


                            System.out.println("f name= " + drivername);
                            names.add(drivername);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "لا يمكن اكمال العملية، لايوجد vpn", Toast.LENGTH_LONG).show();

            }
        } else
            Toast.makeText(getActivity().getApplicationContext(), "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();

    }
    void getDriversTelephones() {
        telephones.clear();
        telephones.add("0");
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);

        Network activeNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
        if (caps != null) {
            boolean vpnInUse = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
            if (vpnInUse) {

                database = FirebaseDatabase.getInstance("https://taxi-5235a-default-rtdb.firebaseio.com/");
                myRef = database.getReference("Drivers");

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            for (DataSnapshot d : snapshot.getChildren()) {
                                telephones.add(d.getKey());
                                System.out.println(d.getKey());
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "لا يمكن اكمال العملية، لايوجد vpn", Toast.LENGTH_LONG).show();

            }
        } else
            Toast.makeText(getActivity().getApplicationContext(), "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();

    }

}
