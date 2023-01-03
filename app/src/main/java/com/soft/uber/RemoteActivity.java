package com.soft.uber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class RemoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            Intent in = getIntent();

            String body= in.getStringExtra("body");
            if(body.contains("الزبون")){
            Intent in2 = new Intent(getApplicationContext(), DriverLoginActivity.class);
            in2.putExtra("parent","Remote");
            in2.putExtra("body",body);
            System.out.println("in remote: "+body);

            startActivity(in2);

            }
        else
        {
            System.out.println("in remote2: "+body);
            Intent in2 = new Intent(getApplicationContext(), UserLoginActivity.class);
            startActivity(in2);
        }
    }
}
