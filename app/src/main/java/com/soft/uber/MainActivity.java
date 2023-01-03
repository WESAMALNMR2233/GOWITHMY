package com.soft.uber;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    ImageView driver;
    ImageView user;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        driver=(ImageView) findViewById(R.id.mainDriver);
        user=(ImageView)findViewById(R.id.imageView2);
        tv=(TextView) findViewById(R.id.tx2);
        tv.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in=new Intent(MainActivity.this,AdminLoginActivity.class);
                        startActivity(in);
                    }
                }

        );
        driver.setOnClickListener(new View.OnClickListener() {

                                      public void onClick(View v) {
                                          Intent in = new Intent(MainActivity.this, DriverLoginActivity.class);
                                          in.putExtra("parent","Main");
                                          startActivity(in);
                                          finish();

                                      }
                                  }
        );
        user.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, UserLoginActivity.class);
                startActivity(in);
                finish();

            }        }
        );

    }
}
