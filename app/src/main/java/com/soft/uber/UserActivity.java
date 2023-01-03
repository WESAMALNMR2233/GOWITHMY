package com.soft.uber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserActivity extends AppCompatActivity {

    EditText name,phone,address,password;
    Button btn;
    FirebaseDatabase database;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);



        name=(EditText) findViewById(R.id.name);
        password=(EditText) findViewById(R.id.password);

        phone=(EditText)findViewById(R.id.telephone);
        address=(EditText)findViewById(R.id.address);
        btn=(Button)findViewById(R.id.floatingActionButton);

        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConnectivityManager connectivityManager = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);

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
                                    Toast.makeText(v.getContext(), "شكرا لاشتراكك في تطبيق خدني معك", Toast.LENGTH_SHORT).show();

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

                            Toast.makeText(getApplicationContext(), "لا يمكن اكمال العملية، لايوجد vpn", Toast.LENGTH_LONG).show();

                        }
                    }
                        else
                            Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();

                    }
                }
        );
    }
}
