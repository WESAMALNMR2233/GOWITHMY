package com.soft.uber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AdminLoginActivity extends AppCompatActivity {
    EditText password,username;
    Button btn;
    TextView tv;
    FirebaseDatabase database;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        password=(EditText) findViewById(R.id.password);
        username=(EditText) findViewById(R.id.username);
        btn=(Button) findViewById(R.id.btn);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConnectivityManager connectivityManager =
                                (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
                        Network activeNetwork = connectivityManager.getActiveNetwork();
                        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
                        if (caps != null) {
                            boolean vpnInUse = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
                            if (vpnInUse) {
                                isAdmin(username.getText().toString(), password.getText().toString());
                            } else {

                                Toast.makeText(getApplicationContext(),
                                        "لا يمكن اكمال العملية، لايوجد vpn", Toast.LENGTH_LONG).show();

                            }

                        } else
                            Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();
                    }
                    });

    }

    public void isAdmin(String username,String Enteredpassword) {
               database = FirebaseDatabase.getInstance("https://taxi-5235a-default-rtdb.firebaseio.com/");
                myRef = database.getReference("Admin");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            String name = snapshot.child("username").getValue(String.class);
                            Long password = snapshot.child("password").getValue(Long.class);
                            System.out.println(name+",,,"+username);
                            System.out.println(password+",,"+Enteredpassword);
                            if (password == Long.parseLong(Enteredpassword) && name.equals(username)) {

                                Intent in = new Intent(getApplicationContext(), AdminActivity.class);

                                startActivity(in);


                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "يرجى التأكد من بيانات الحساب اسم المستخدم /كلمة السر", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }
    }

