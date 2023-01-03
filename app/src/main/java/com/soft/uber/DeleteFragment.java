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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DeleteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeleteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeleteFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button dbtn;
    FirebaseDatabase database;
    DatabaseReference myRef;
    static ArrayList<String> names = new ArrayList<String>();
    static ArrayList<String> telephones = new ArrayList<String>();

    Spinner spin;
    String selected = "";
    int selectedIndex;
    private OnFragmentInteractionListener mListener;

    public DeleteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeleteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeleteFragment newInstance(String param1, String param2) {
        DeleteFragment fragment = new DeleteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_delete, container, false);
        getDriversName();
        getDriversTelephones();
        spin = v.findViewById(R.id.driverspin);
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


        dbtn = (Button) v.findViewById(R.id.deletebtn);
        dbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager)
                        getActivity().getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
                Network activeNetwork = connectivityManager.getActiveNetwork();
                NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
                if (caps != null) {
                    boolean vpnInUse = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
                    if (vpnInUse) {
                        database = FirebaseDatabase.getInstance
                                ("https://taxi-5235a-default-rtdb.firebaseio.com/");
                        myRef = database.getReference("Drivers");
                        if (selectedIndex == 0) {
                            Toast.makeText(getContext(), "يجب اختيار سائق", Toast.LENGTH_LONG).show();

                        } else {
                            myRef.child(telephones.get(selectedIndex)).removeValue();
                            Toast.makeText(getContext(), "تم حذف السائق بنجاح", Toast.LENGTH_LONG).show();
                            names.remove(selectedIndex);
                            telephones.remove(selectedIndex);
                        }
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "لا يمكن اكمال العملية، لايوجد vpn", Toast.LENGTH_LONG).show();

                    }
                } else
                    Toast.makeText(getActivity().getApplicationContext(),
                            "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();

            }
        });
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

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

