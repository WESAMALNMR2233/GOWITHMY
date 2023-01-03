package com.soft.uber;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Marker mCurrLocationMarkerUser;
    Marker EndmCurrLocationMarker;
    Marker EndmCurrLocationMarkerUser;
    String usertoken ="";
    LocationRequest mLocationRequest;
    MarkerOptions markerOptionsUser;
    MarkerOptions EndmarkerOptionsUser;

    private GoogleMap mMap;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FloatingActionButton df;
    Float latt=0.0f,lngg=0.0f;
    Float endLat=0.0f,endLng=0.0f;

    static String driverTelephone,name,pass,address,carNumber,t;
    static int evalute;
    Button logOut;
    Intent in;
    static boolean getNotification=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);
         in=getIntent();
        driverTelephone=in.getStringExtra("driverTelephone");
        name=in.getStringExtra("name");
        pass=in.getStringExtra("pass");
        address=in.getStringExtra("address");
        carNumber=in.getStringExtra("carNumber");
        t=in.getStringExtra("t");
        evalute=in.getIntExtra("evalute",0);
        System.out.println("in drive map:"+name+","+pass+","+address+","+carNumber+","+evalute+","+t);


        df=(FloatingActionButton) findViewById(R.id.floatingActionButton4);
        df.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(in.hasExtra("body"))
                {
                    // "عنوان الزبون :" + start.latitude + "," + start.longitude + "رقم الهاتف:" + userTelephone,
                    String body=in.getStringExtra("body");
                    System.out.println("body in driver= "+body);
                    StringTokenizer st=new StringTokenizer(body,",");
                    String h=st.nextToken();
                     System.out.println("h1="+h);
                    h=st.nextToken();
                    System.out.println("h2="+h);

                    String tel=h.substring(21,30);
                    System.out.println("tel="+tel);
                     usertoken=getUserToken(tel);
                    System.out.println("send to= "+usertoken);
                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(usertoken,
                            "خدني معك ",
                            "تم الاستجابة لطلبك سيصل التكسي قريبا",
                            getApplicationContext(),
                            DriverMapActivity.this);
                    notificationsSender.SendNotifications();
                    setOrder(tel);
                    Toast.makeText(v.getContext(), "تم الاستجابة للطلب",
                            Toast.LENGTH_LONG).show();
                }
                else{

                    Toast.makeText(v.getContext(), " لا يوجد أي طلب ",
                            Toast.LENGTH_LONG).show();
                }

            }});
        logOut=(Button) findViewById(R.id.logOut);
        logOut.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //  FirebaseMessaging.getInstance().unsubscribeFromTopic("onlineDriver");
            database = FirebaseDatabase.getInstance("https://taxi-5235a-default-rtdb.firebaseio.com/");
            myRef = database.getReference("Tokens");

            myRef.child(driverTelephone)
                    .child("online")
                    .setValue(false);
            Toast.makeText(v.getContext(), " تم تسجيل خروجك، لن تتلقى أي اشعارات",
                    Toast.LENGTH_LONG).show();
        }}
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.drmap);

        mapFragment.getMapAsync(this);
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


    }

    private void setOrder(String tel) {
        database = FirebaseDatabase.getInstance("https://taxi-5235a-default-rtdb.firebaseio.com/");
        myRef = database.getReference("Orders");
        Query q = myRef.orderByChild("userTelephone").equalTo(tel);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long i=snapshot.getChildrenCount();
                System.out.println("children=="+snapshot.getChildrenCount());
                Calendar lastDate=null;
                String orderKey="";
                for(DataSnapshot s:snapshot.getChildren())
                {
                    Long tele=s.child("userTelephone").getValue(Long.class);
                    System.out.println("in child "+s.getKey()+"tele="+tele.toString()+", tel="+tel+", equal= "+(Long.valueOf(tel).equals(tele)));

                    if(tele.equals(Long.valueOf(tel))) {
                        String d = s.child("date").getValue(String.class);
                        Calendar date = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy",Locale.ENGLISH);
                        try {
                            date.setTime(sdf.parse(d));

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (lastDate == null) {
                            lastDate = date;
                            orderKey = s.getKey();
                        } else if (date.after(lastDate)) {

                            lastDate = date;
                            orderKey = s.getKey();
                        }

                    }
                }

                System.out.println("last order key= "+orderKey+", lastdate="+lastDate.getTime().toString());
                myRef.child(orderKey).child("status").setValue("complete");
                myRef.child(orderKey).child("driverTelephone").setValue(Long.valueOf(driverTelephone));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private String getUserToken(String tel) {
        database = FirebaseDatabase.getInstance("https://taxi-5235a-default-rtdb.firebaseio.com/");
        myRef = database.getReference("Tokens");

        myRef.child(tel).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    usertoken = snapshot.child("token").getValue(String.class);
                    System.out.println("usertoken " + usertoken);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        return usertoken;
                }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if(in.hasExtra("body"))
                {
                    // "عنوان الزبون :" + start.latitude + "," + start.longitude + "رقم الهاتف:" + userTelephone,
                    String body=in.getStringExtra("body");
                    System.out.println("body in driver= "+body);
                    StringTokenizer st=new StringTokenizer(body,",");
                    String h=st.nextToken();
                    System.out.println("h= "+h);

                    String lat=h.substring(14);
                     latt=Float.valueOf(lat);
                    System.out.println("lat="+lat);
                    h=st.nextToken();
                    System.out.println("h2="+h);
                    String lng=h.substring(0,10);
                     lngg=Float.valueOf(lng);

                    System.out.println("lng="+lng);
                  h=st.nextToken();
                  System.out.println("new h="+h);
                  String elatt=h.substring(7);
                  endLat=Float.valueOf(elatt);
                    h=st.nextToken();
                    System.out.println("long end h="+h);
                    endLng=Float.valueOf(h);

                }

                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);

            }
        } else {
            if(in.hasExtra("body"))
            {
                // "عنوان الزبون :" + start.latitude + "," + start.longitude + "رقم الهاتف:" + userTelephone,
                String body=in.getStringExtra("body");
                System.out.println("body in driver 2= "+body);
                StringTokenizer st=new StringTokenizer(body,",");
                String h=st.nextToken();
                System.out.println("h= "+h);

                String lat=h.substring(14);
                 latt=Float.valueOf(lat);
                System.out.println("lat="+lat);
                h=st.nextToken();
                System.out.println("h2="+h);
                String lng=h.substring(0,10);
                 lngg=Float.valueOf(lng);

                System.out.println("lng="+lng);

                System.out.println("new h="+h);
                String elatt=h.substring(6);
                endLat=Float.valueOf(elatt);
                h=st.nextToken();
                System.out.println("long end h="+h);
                endLng=Float.valueOf(h);


            }

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);


        }
    }
    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onLocationChanged(Location location) {
        System.out.println("on changed");
        mLastLocation = location;

        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
//Showing Current Location Marker on Map
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
      //  Toast.makeText(getApplicationContext(),"location="+latLng.latitude+","+latLng.longitude,Toast.LENGTH_LONG).show();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            double longitude = locations.getLongitude();
            double latitude = locations.getLatitude();
            Geocoder geocoder = new Geocoder(getApplicationContext(),
                    Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude,
                        longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    String state = listAddresses.get(0).getAdminArea();
                    String country = listAddresses.get(0).getCountryName();
                    String subLocality = listAddresses.get(0).getSubLocality();
                    markerOptions.title("" + latLng + "," + subLocality + "," + state
                            + "," + country);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
                 if(latt!=0 && lngg!=0) {
                     LatLng latLngUser = new LatLng(latt, lngg);

                     markerOptionsUser = new MarkerOptions();
                     markerOptionsUser.position(latLngUser);
                     markerOptionsUser.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                     mCurrLocationMarkerUser = mMap.addMarker(markerOptionsUser);


                     LatLng EndlatLngUser = new LatLng(endLat, endLng);

                     EndmarkerOptionsUser = new MarkerOptions();
                     EndmarkerOptionsUser.position(EndlatLngUser);
                     EndmarkerOptionsUser.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                     EndmCurrLocationMarkerUser = mMap.addMarker(EndmarkerOptionsUser);
                 }
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                    this);
        }
        database = FirebaseDatabase.getInstance("https://taxi-5235a-default-rtdb.firebaseio.com/");
        myRef = database.getReference("Drivers");
        GeoFire geoFire = new GeoFire(myRef);
        geoFire.setLocation(driverTelephone, new GeoLocation(location.getLatitude(), location.getLongitude()));
        myRef.child(driverTelephone)
                .child("name")
                .setValue(name);
        myRef.child(driverTelephone)
                .child("pass")
                .setValue(pass);
        myRef.child(driverTelephone)
                .child("address")
                .setValue(address);
        myRef.child(driverTelephone)
                .child("carNumber")
                .setValue(carNumber);
        myRef.child(driverTelephone)
                .child("evalute")
                .setValue(evalute);
        myRef.child(driverTelephone)
                .child("t")
                .setValue(t);
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}