package com.sahil.easytransportapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;

import com.google.android.libraries.places.api.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    MarkerOptions marker = new MarkerOptions();
    TextInputLayout inputLayout;
    TextInputEditText searchEditText;
    LocationManager locationManager;
    LocationListener locationListener;

    public void setAddress(View v) {
         AlertDialog.Builder adb = new AlertDialog.Builder(MapsActivity.this);
        if (marker != null) {
            LatLng position = marker.getPosition();
            if (position != null) {
                List<Address> positionsList = new ArrayList<Address>();
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    positionsList.clear();
                    positionsList = geocoder.getFromLocation(position.latitude, position.longitude, 1);
                    if (positionsList != null && positionsList.size() > 0){
                        String string = "";
                        if (positionsList.get(0).getLocality() != null) {
                            string += positionsList.get(0).getLocality() + " ";
                        }
                        if (positionsList.get(0).getAdminArea() != null){
                            string  += positionsList.get(0).getAdminArea() + " ";
                        }
                        if (positionsList.get(0).getAddressLine(0 ) != null){
                            string += positionsList.get(0).getAddressLine(0) ;
                        }
                        adb.setTitle(" Set Address ");
                        adb.setMessage(string);
                        final String finalString = string;
                        adb.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Bundle bundle = getIntent().getExtras();
                                String tag = bundle.getString("tag");
                                if (tag.equals("1")){
                                    RequestActivity.pickUp_address.setText(finalString);
                                    onBackPressed();
                                }else if (tag.equals("2")){
                                    RequestActivity.delivery_address.setText(finalString);
                                    onBackPressed();
                                }else if (tag.equals("3")){
                                    ShowRequest.chooseLocation.setText(finalString);
                                    onBackPressed();
                                }else if (tag.equals("4")){
                                    DriverRegister.address.setText(finalString);
                                    onBackPressed();
                                }else if (tag.equals("5")){
                                    SearchDriver.enterAddress.setText(finalString);
                                    onBackPressed();
                                } else {
                                    Toast.makeText(getApplicationContext(),"Something went wrong could not set the address ",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        adb.setNegativeButton("Close",null);
                        adb.setIcon(android.R.drawable.ic_menu_myplaces);
                        adb.show();
                    }else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);
                        alert.setTitle("Error");
                        alert.setMessage("can not find any place");
                        alert.setPositiveButton("Close",null);
                        alert.show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Error :- "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }else {
                AlertDialog.Builder a = new AlertDialog.Builder(MapsActivity.this);
                a.setTitle("Error");
                a.setMessage("The address is empty sry could not set the address ");
                a.setPositiveButton("Close",null);
                a.show();
            }
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            builder.setTitle("Error");
            builder.setMessage("the Current markerOptions is  null");
            builder.setPositiveButton("Close",null);
            builder.show();
        }

    }

    public void showMyLocation(View view) {
        if (ActivityCompat.checkSelfPermission(
                MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);
            Location lastKnownLocation  = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                List<Address> addressList = new ArrayList<Address>();
                Geocoder geocoder = new Geocoder(getApplicationContext(),Locale.getDefault());
                try {
                    addressList.clear();
                    addressList = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(),1);
                    if (addressList != null && addressList.size() > 0) {
                        String showAddress = "";

                        if (addressList.get(0).getAdminArea() != null) {

                            showAddress += addressList.get(0).getAdminArea() + " ";
                        }
                        if (addressList.get(0).getLocality() != null) {
                            showAddress += addressList.get(0).getLocality() + " ";
                        }
                        if (addressList.get(0).getAddressLine(0 ) != null){
                            showAddress += addressList.get(0).getAddressLine(0) +" ";
                        }
                        if (addressList.get(0).getPostalCode() != null) {
                            showAddress += addressList.get(0).getPostalCode();
                        }
                        locationShow(lastKnownLocation,showAddress);
                    }
                }catch (Exception e){
                    if (e.getMessage().equals("grpc failed")){
                        Toast.makeText(this, "try again someThing went wrong make sure yoy internet connection and location service is on  ", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, "someThing went wrong " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }


    public  void  locationShow(Location location, String title) {
        if (location != null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(marker.position(userLocation).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.checkSelfPermission(
                        MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);
                    Location lastKnownLocation  = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    locationShow(lastKnownLocation,"Your Location");
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        inputLayout = findViewById(R.id.til_location);
        searchEditText = findViewById(R.id.et_address);
        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    searchPlace(view);
                    keyBoardHide();
                }
                return false;
            }
        });
        inputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPlace(view);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(
                MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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

    public void searchPlace(View view){
        String location =  searchEditText.getText().toString();
        if (location.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            builder.setTitle("Google Map");
            builder.setIcon(android.R.drawable.ic_menu_mylocation);
            builder.setMessage("Please Enter a place to search");
            builder.setPositiveButton("Close",null);
            builder.show();
        }else {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
                Geocoder geoCoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geoCoder.getFromLocationName(location, 5);
                    if (addresses.size() > 0) {
                        Double lat = (double) (addresses.get(0).getLatitude());
                        Double lon = (double) (addresses.get(0).getLongitude());
                        String placeAddress = "";
                        String s = "";
                        if (addresses.get(0).getAdminArea() != null) {
                            placeAddress += addresses.get(0).getAdminArea() + " ";
                        }
                        if (addresses.get(0).getLocality() != null) {
                            placeAddress += addresses.get(0).getLocality() + " ";
                        }
                        if (addresses.get(0).getFeatureName() != null) {
                            placeAddress += addresses.get(0).getFeatureName() + " ";
                        }
                        if (addresses.get(0).getThoroughfare() != null) {
                            placeAddress += addresses.get(0).getThoroughfare() + "";
                        }
                        if (addresses.get(0).getPostalCode() != null) {
                            placeAddress += addresses.get(0).getPostalCode();
                        }
                        if (addresses.get(0).getAddressLine(0) != null) {
                            s = addresses.get(0).getAddressLine(0);
                        }
                        final LatLng user = new LatLng(lat, lon);
                        searchEditText.setText(s + ", " + user.toString());
                        mMap.clear();
                        Marker hamburg = mMap.addMarker(marker
                                .position(user)
                                .title(placeAddress)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(user, 13));
                    } else {
                        AlertDialog.Builder adb = new AlertDialog.Builder(MapsActivity.this);
                        adb.setTitle("Google Map");
                        adb.setMessage("Please Provide the Proper Place");
                        adb.setPositiveButton("Close", null);
                        adb.setIcon(android.R.drawable.ic_menu_mylocation);
                        adb.create().show();
                    }
                } catch (Exception e) {
                    if (e.getMessage().equals("grpc failed")) {
                        Toast.makeText(this, "try again someThing went wrong make sure yoy internet connection and location service is on  ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "someThing went wrong " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }else {
                Toast.makeText(MapsActivity.this, "Internet is required for searching places Turn on the internet ", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder adb = new AlertDialog.Builder(MapsActivity.this);
                adb.setTitle("Turn on internet");
                adb.setMessage(" ops :( looks like Your internet connection is off");
                adb.setPositiveButton("Go To Setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                        dialogInterface.dismiss();
                    }
                });
                adb.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MapsActivity.this, "Internet is required for searching places Turn on the internet ", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                });
                adb.setIcon(android.R.drawable.ic_dialog_info);
                adb.create().show();
            }
        }
    }

    public void keyBoardHide(){
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean b =locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean c =  locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!b && !c) {
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
                    Toast.makeText(MapsActivity.this, "Location service is required for using google map pls turn on Location service", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.create().show();

        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                //locationShow(location,"Your location");

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
                MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);
            Location lastKnownLocation  = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                locationShow(lastKnownLocation, "Your current Location");
            }

        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                keyBoardHide();
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                List<Address> addressList = new ArrayList<Address>();
                Geocoder geocoder = new Geocoder(getApplicationContext(),Locale.getDefault());
                try {
                    addressList.clear();
                    addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude,1);
                    if (addressList != null && addressList.size() > 0) {
                        String address = "";

                        if (addressList.get(0).getAdminArea() != null) {

                            address += addressList.get(0).getAdminArea() + " ";
                        }
                        if (addressList.get(0).getLocality() != null) {
                            address += addressList.get(0).getLocality() + " ";
                        }
                        if (addressList.get(0).getAddressLine(0 ) != null){
                                address += addressList.get(0).getAddressLine(0) +" ";
                        }
                        if (addressList.get(0).getPostalCode() != null) {
                            address += addressList.get(0).getPostalCode();
                        }
                        mMap.clear();
                        mMap.addMarker(marker.position(latLng).title(address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                    }
                }catch (Exception e){
                    if (e.getMessage().equals("grpc failed")){
                        Toast.makeText(MapsActivity.this, "try again someThing went wrong make sure yoy internet connection and location service is on  ", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MapsActivity.this, "someThing went wrong " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}

