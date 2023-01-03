package com.soft.uber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.FirebaseMessaging;

public class DriverLoginActivity extends AppCompatActivity {

    EditText password,telephone;
    Button btn;
    TextView tv;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String name,address,carNumber;
    int evalute;
    String t;
    public static String currentDriverTelephone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        password=(EditText) findViewById(R.id.drpassword);
        telephone=(EditText) findViewById(R.id.drtelephone);

        btn=(Button) findViewById(R.id.drbtn);


        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        System.out.println(password.getText().toString());
                        isDriver(password.getText().toString(),telephone.getText().toString());




                    }
                }
        );

    }

    public void isDriver(String pass,String currentDriverTelephone) {



        ConnectivityManager connectivityManager = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);

        Network activeNetwork = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            activeNetwork = connectivityManager.getActiveNetwork();
        }
        NetworkCapabilities caps = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            caps = connectivityManager.getNetworkCapabilities(activeNetwork);
        }
        if(caps!=null) {
            boolean vpnInUse = false;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                vpnInUse = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
            }
            if (vpnInUse) {


                    database = FirebaseDatabase.getInstance("https://taxi-5235a-default-rtdb.firebaseio.com/");
                    myRef = database.getReference("Drivers");

                    myRef.child(currentDriverTelephone).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {

                                String passwordd = snapshot.child("pass").getValue(String.class);
                                System.out.println("hererr "+passwordd);

                                if (passwordd.equals(pass)) {
                                     name = snapshot.child("name").getValue(String.class);
                                    address = snapshot.child("address").getValue(String.class);
                                    carNumber = snapshot.child("carNumber").getValue(String.class);
                                    evalute = snapshot.child("evalute").getValue(Integer.class);
                                    Toast.makeText(getApplicationContext(), name + "أهلا بك ", Toast.LENGTH_LONG).show();

                                    FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                                        if (!TextUtils.isEmpty(token)) {
                                            System.out.println("token success =" + token);
                                            t=token;
                                            database = FirebaseDatabase.getInstance("https://taxi-5235a-default-rtdb.firebaseio.com/");

                                            myRef = database.getReference("Tokens");

                                          myRef.child(currentDriverTelephone)
                                                    .child("token")
                                                    .setValue(token);
                                            myRef.child(currentDriverTelephone)
                                                    .child("online")
                                                    .setValue(true);

                                        } else {
                                            System.out.println("token error");

                                        }

                                    }).addOnFailureListener(e -> {
                                            }
                                    ).addOnCanceledListener(() -> {
                                    }).addOnCompleteListener(task -> System.out.println("token is:" + task.getResult())

                                    );
                                    Intent in = new Intent(getApplicationContext(), DriverMapActivity.class);
                                            Intent from=getIntent();
                                            String parent=from.getStringExtra("parent");
                                            if(!parent.equalsIgnoreCase("Main")){
                                                String body=from.getStringExtra("body");

                                                in.putExtra("body",body);
                                            }

                                    in.putExtra("driverTelephone", currentDriverTelephone);
                                    in.putExtra("name",name);
                                    in.putExtra("pass",pass);
                                    in.putExtra("address",address);
                                    in.putExtra("carNumber",carNumber);
                                    in.putExtra("evalute", evalute);
                                    in.putExtra("t",t);
                                    System.out.println("in login:"+name+","+pass+","+address+","+carNumber+","+evalute+","+t);
                                    startActivity(in);


                                } else {
                                    Toast.makeText(getApplicationContext(), "يرجى التأكد من كلمة السر", Toast.LENGTH_LONG).show();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                Toast.makeText(getApplicationContext(), "لا يمكن اكمال العملية، لايوجد vpn", Toast.LENGTH_LONG).show();

            }
        }else {
            Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();

        }
    }
}
