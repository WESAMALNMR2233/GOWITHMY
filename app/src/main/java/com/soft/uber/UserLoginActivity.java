package com.soft.uber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.messaging.FirebaseMessaging;

public class UserLoginActivity extends AppCompatActivity {

    EditText password,telephone;
    Button btn;
    TextView tv;
    FirebaseDatabase database;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        password=(EditText) findViewById(R.id.password);
        telephone=(EditText) findViewById(R.id.telephone);
        tv=(TextView) findViewById(R.id.tv);
        tv.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent in = new Intent(UserLoginActivity.this, UserActivity.class);
                                startActivity(in);
                                finish();
                            }
                        }

                );
        btn=(Button) findViewById(R.id.btn);


        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       isUser();




                    }
                }
        );

    }

   public void isUser() {

       String userEnterPassword = password.getText().toString();
       int tel = Integer.valueOf(telephone.getText().toString());
       ConnectivityManager connectivityManager = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);

       Network activeNetwork = connectivityManager.getActiveNetwork();
       NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
       if(caps!=null) {
           boolean vpnInUse = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
           if (vpnInUse) {
               database = FirebaseDatabase.getInstance("https://taxi-5235a-default-rtdb.firebaseio.com/");
               myRef = database.getReference("Users");

               myRef.child(telephone.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {

                       if (snapshot.exists()) {

                           String password = snapshot.child("pass").getValue(String.class);
                           System.out.println("pass=" + password);
                           if (password.equals(userEnterPassword)) {
                               String name = snapshot.child("name").getValue(String.class);
                               FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                                   if (!TextUtils.isEmpty(token)) {
                                       System.out.println("token success =" + token);

                                       database = FirebaseDatabase.getInstance("https://taxi-5235a-default-rtdb.firebaseio.com/");

                                       myRef = database.getReference("Tokens");

                                       myRef.child(String.valueOf(tel))
                                               .child("token")
                                               .setValue(token);

                                   } else {
                                       System.out.println("token error");

                                   }

                               }).addOnFailureListener(e -> {
                                       }
                               ).addOnCanceledListener(() -> {
                               }).addOnCompleteListener(task -> System.out.println("token is:" + task.getResult())

                               );
                               Toast.makeText(getApplicationContext(), name + "أهلا بك ", Toast.LENGTH_LONG).show();

                               Intent in=new Intent(getApplicationContext(),UserDrawerActivity.class);
                                 in.putExtra("userTelephone",tel);
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
