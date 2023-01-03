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
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImportFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        AdapterView.OnItemSelectedListener,
        LocationListener {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    float distence=0;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    FloatingActionButton fb;
    private GoogleMap mMap;
    MarkerOptions marker;
    Marker dest;
    EditText et;
    Spinner spin;
    Switch aSwitch;
    static String spinData="";
    static String kindData;
    String[] cars = { "Normal", "VIP"};
    FirebaseDatabase database;
    DatabaseReference myRef;
    static ConnectivityManager connectivityManager ;
    static  ArrayList<String> tokens;
    Button requestbtn;
    static LatLng start,end;
    static int price;
    static Date currentTime;
    static int userTelephone;
    static boolean running;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.activity_user_map,container,false);

        running=true;

        Intent in=this.getActivity().getIntent();
        userTelephone=in.getIntExtra("userTelephone",0);
        connectivityManager = (ConnectivityManager) this.getActivity().getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        tokens=new ArrayList<String>();
        fb=(FloatingActionButton) v.findViewById(R.id.floatingActionButton3);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager()
                        .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        // Take the instance of Spinner and
        // apply OnItemSelectedListener on it which
        // tells which item of spinner is clicked
        spin = v.findViewById(R.id.coursesspinner);
        spin.setOnItemSelectedListener(this);

        // Create the instance of ArrayAdapter
        // having the list of courses
        ArrayAdapter<String> ad
                = new ArrayAdapter<>(
                this.getActivity(),
                android.R.layout.simple_spinner_item,
                cars);

        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        spin.setAdapter(ad);
        et=(EditText)v.findViewById(R.id.ee);
        et.setText("0 ل.س");
        aSwitch=(Switch) v.findViewById(R.id.switch1);
        boolean d=aSwitch.isChecked();
        if(!d) kindData="عامة";
        else
            kindData="خاصة";
        requestbtn=(Button) v.findViewById(R.id.request);
        Context context=this.getActivity().getApplicationContext();
        requestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Order o = new Order(Integer.valueOf(price), kindData, spinData, start.latitude, start.longitude, end.latitude, end.longitude, currentTime.toString(), userTelephone);

                Order.InsertOrder(o, connectivityManager, database, myRef, context);

                getAllOnlineDriversToken(context);

            }
        });
        return v;
    }


    void getAllOnlineDriversToken(Context context) {


        ConnectivityManager connectivityManager = (ConnectivityManager) this.getActivity().getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);

        Network activeNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
        if (caps != null) {
            boolean vpnInUse = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
            if (vpnInUse) {
                database = FirebaseDatabase.getInstance("https://taxi-5235a-default-rtdb.firebaseio.com/");
                myRef = database.getReference("Tokens");
                Query q = myRef.orderByChild("online").equalTo(true);
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long i=snapshot.getChildrenCount();

                        for(DataSnapshot s:snapshot.getChildren())
                        {
                            String t=s.child("token").getValue(String.class);
                            tokens.add(t);

                            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(t,
                                    "خدني معك ",
                                    "عنوان الزبون :" + start.latitude + "," + start.longitude + "رقم الهاتف:" + userTelephone+",target:"+end.latitude+","+end.longitude+","+"كلفة الرحلة=" +price,
                                    context,
                                    getActivity());
                            notificationsSender.SendNotifications();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




            }
        }

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        et=(EditText)this.getActivity().findViewById(R.id.ee);
        et.setText("0 ل.س");
        fb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(dest!=null) dest.remove();
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if(marker==null){
                            marker = new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).title("الوجهة");
                            dest= mMap.addMarker(marker);
                            System.out.println("desti:" + latLng.latitude + "," + latLng.longitude);
                            end=latLng;
                            start=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                            System.out.println("location="+start.latitude+','+start.longitude);
                            System.out.println("destince="+CalculateDistance(start,end));
                            if(spinData.equalsIgnoreCase("Normal"))
                                price=(int)( CalculateDistance(start,end)*1500);
                            else
                                price=(int)( CalculateDistance(start,end)*3000);
                            currentTime = Calendar.getInstance().getTime();

                            System.out.println(currentTime.toString());
                            et.setText(String.valueOf(price));
                            mMap.setOnMapClickListener(null);
                        }
                        else
                        {
                            marker = new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).title("الوجهة");
                            dest= mMap.addMarker(marker);
                            System.out.println("desti:" + latLng.latitude + "," + latLng.longitude);
                            end=latLng;
                            start=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                            System.out.println("location="+start.latitude+','+start.longitude);
                            System.out.println("destince="+CalculateDistance(start,end));
                            if(spinData.equalsIgnoreCase("Normal"))
                                price=(int)( CalculateDistance(start,end)*1500);
                            else
                                price=(int)( CalculateDistance(start,end)*3000);
                            currentTime = Calendar.getInstance().getTime();

                            System.out.println(currentTime.toString());
                            et.setText(String.valueOf(price));
                            mMap.setOnMapClickListener(null);
                        }




                    }
                });
            }
        });
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this.getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                boolean destination=false;
                mMap.setMyLocationEnabled(true);
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if(dest!=null)dest.remove();
                        MarkerOptions marker = new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).title("الوجهة");
                        dest= mMap.addMarker(marker);
                        System.out.println("desti:" + latLng.latitude + "," + latLng.longitude);
                        end=latLng;
                        start=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                        System.out.println("location="+start.latitude+','+start.longitude);
                        System.out.println("destince="+CalculateDistance(start,end));
                        if(spinData.equalsIgnoreCase("Normal"))
                            price=(int)( CalculateDistance(start,end)*1500);
                        else
                            price=(int)( CalculateDistance(start,end)*3000);
                        currentTime = Calendar.getInstance().getTime();

                        System.out.println(currentTime.toString());
                        et.setText(String.valueOf(price));
                        mMap.setOnMapClickListener(null);



                    }
                });
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if(dest!=null) dest.remove();
                    MarkerOptions marker=new MarkerOptions().position(new LatLng(latLng.latitude,latLng.longitude)).title("الوجهة");
                    dest= mMap.addMarker(marker);
                    System.out.println("desti:"+latLng.latitude+","+latLng.longitude);
                    end=latLng;
                    start=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                    System.out.println("location="+start.latitude+','+start.longitude);
                    System.out.println("destince="+CalculateDistance(start,end));
                    if(spinData.equalsIgnoreCase("Normal"))
                        price=(int)( CalculateDistance(start,end)*1500);
                    else
                        price=(int)( CalculateDistance(start,end)*3000);
                    currentTime = Calendar.getInstance().getTime();

                    System.out.println(currentTime.toString());
                    et.setText(String.valueOf(price));
                    mMap.setOnMapClickListener(null);

                }
            });
        }
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
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
        if (ContextCompat.checkSelfPermission(this.getContext(),
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
                this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            double longitude = locations.getLongitude();
            double latitude = locations.getLatitude();
            Geocoder geocoder = new Geocoder(this.getActivity().getApplicationContext(),
                    Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude,
                        longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    String state = listAddresses.get(0).getAdminArea();
                    String country = listAddresses.get(0).getCountryName();
                    // String subLocality = listAddresses.get(0).getSubLocality();
                    markerOptions.title("" + latLng  + "," + state
                            + "," + country);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                    this);
        }

    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this.getActivity(),
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
                    if (ContextCompat.checkSelfPermission(this.getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this.getActivity(), "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public double CalculateDistance(LatLng start,LatLng end){
        int r=6371;
        double lat1=start.latitude;
        double lat2=end.latitude;
        double lon1=start.longitude;
        double lon2=end.longitude;
        double dlat=Math.toRadians(lat2-lat1);
        double dlon=Math.toRadians(lon2-lon1);
        double a=Math.sin(dlat/2)*Math.sin(dlat/2)
                +Math.cos(Math.toRadians(lat1))
                *Math.cos(Math.toRadians(lat2))*Math.sin(dlon/2)
                *Math.sin(dlon/2);
        double c=2* Math.asin(Math.sqrt(a));
        double valueResult=r*c;
        double km=valueResult/1;
        DecimalFormat newFormat=new DecimalFormat("####");
        int kmInDec=Integer.valueOf(newFormat.format(km));
        double meter=valueResult%1000;
        int meterInDec=Integer.valueOf(newFormat.format(meter));
        return r*c;



    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


        spinData=cars[i];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}