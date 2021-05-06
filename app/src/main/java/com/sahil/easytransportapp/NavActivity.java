package com.sahil.easytransportapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;

import java.util.ArrayList;

public class NavActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<Marker> markers;
    LocationManager locationManager;
    LocationListener locationListener;
    ParseGeoPoint geoPoint, geoPoint2;


    public void  navigate(View view){
        try {
            if (ActivityCompat.checkSelfPermission(
                    NavActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    NavActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(NavActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (locationGPS != null) {
                    String g1 = String.valueOf(locationGPS.getLatitude());
                    String g2 = String.valueOf(locationGPS.getLongitude());
                    Bundle b = getIntent().getExtras();
                    double lati = b.getDouble("Latitude");
                    double longi =  b.getDouble("Longitude");
                    String d1 = String.valueOf(lati);
                    String d2 = String.valueOf(longi);
                    String location1 =  g1 +","+g2;
                    String location2 =  d1 +","+d2;
                    String s = "http://maps.google.com/maps?saddr="+location1+"&daddr="+location2;
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(s));
                    intent.setPackage("com.google.android.apps.maps");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(NavActivity.this, "navigation failed unable to find your location", Toast.LENGTH_SHORT).show();
                }
            }

        }catch (ActivityNotFoundException e){
            Toast.makeText(NavActivity.this, "some thing went wrong" +e.getMessage(), Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent i = new Intent(Intent.ACTION_VIEW,uri);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        markers = new ArrayList<>();
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean s = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!gps_enable && !s) {
            final  AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enable GPS Location");
            builder.setMessage("Pls Enable GPS Location to use map otherwise you wont able to use map");
            builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Toast.makeText(NavActivity.this, "Location service is required for navigation pls turn on Location service", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.create().show();

            if (ActivityCompat.checkSelfPermission(
                    NavActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    NavActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(NavActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        String s = "";
        mMap = googleMap;
        markers.clear();
        mMap.clear();
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };
        if (ActivityCompat.checkSelfPermission(
                NavActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                NavActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NavActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                geoPoint = new ParseGeoPoint(locationGPS.getLatitude(),locationGPS.getLongitude());
                LatLng myLatLng = new LatLng(locationGPS.getLatitude(), locationGPS.getLongitude());
                markers.add(mMap.addMarker(new MarkerOptions().position(myLatLng).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
            } else {
                Toast.makeText(NavActivity.this, "Unable to find  your location try again make sure that your Location service  is on.", Toast.LENGTH_SHORT).show();
            }
        }
        Bundle b = getIntent().getExtras();
        String value = b.getString("value");
        double l1 = b.getDouble("Latitude");
        double l2 =  b.getDouble("Longitude");
        geoPoint2 = new ParseGeoPoint(l1,l2);
        Double distanceInKm = geoPoint.distanceInKilometersTo(geoPoint2);
        Double round = (double) Math.round(distanceInKm * 10) / 10;
         s += String.valueOf(round) + " km away";
        if (value.equals("driver")){
            LatLng driver = new LatLng(l1,l2);
            markers.add(mMap.addMarker(new MarkerOptions().position(driver).snippet(s).title("driver Location")));
        }else if (value.equals("pickup")){
            LatLng pickup = new LatLng(l1,l2);
            markers.add(mMap.addMarker(new MarkerOptions().position(pickup).snippet(s).title("pickUp location")));
        }else {
            LatLng delivery = new LatLng(l1,l2);
            markers.add(mMap.addMarker(new MarkerOptions().position(delivery).snippet(s).title("Delivery location")));
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 300;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,padding);
        mMap.animateCamera(cu);
    }
}