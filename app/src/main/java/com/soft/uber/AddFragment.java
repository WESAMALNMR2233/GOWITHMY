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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AddFragment extends Fragment {

    EditText name,password,address,carNumber,tel;
    Button btn;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public AddFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_add, container, false);
        name=(EditText) v.findViewById(R.id.dname);
        password=(EditText)v.findViewById(R.id.dpassword);
        address=(EditText)v.findViewById(R.id.daddress);
        carNumber=(EditText)v.findViewById(R.id.carnumber);
        tel=(EditText)v.findViewById(R.id.dtelephone);

        btn=(Button)v.findViewById(R.id.driveraddbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String driverName = name.getText().toString();
                String driverPassword = password.getText().toString();
                String driverAddress = address.getText().toString();
                String carn = carNumber.getText().toString();
                String tele = tel.getText().toString();
                int telephone = Integer.valueOf(tele);
                Driver driver = new Driver(driverName, driverPassword, driverAddress, carn, telephone,null);
                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
                Network activeNetwork = connectivityManager.getActiveNetwork();
                NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
                if (caps != null) {
                    boolean vpnInUse = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
                    if (vpnInUse) {
                        database = FirebaseDatabase.getInstance("https://taxi-5235a-default-rtdb.firebaseio.com/");
                        myRef = database.getReference("Drivers");

                        myRef.child(String.valueOf(telephone)).setValue(
                                driver).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(v.getContext(), "تم اضافة السائق للتطبيق", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(v.getContext(), "يرجى المحاولة مرة أخرى", Toast.LENGTH_SHORT).show();
                                System.out.println("error add" + e.getMessage());
                            }
                        });


                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "لا يمكن اكمال العملية، لايوجد vpn", Toast.LENGTH_LONG).show();

                    }
                } else
                    Toast.makeText(getActivity().getApplicationContext(), "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();

            }
        });
        // Inflate the layout for this fragment
        return v;


    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
