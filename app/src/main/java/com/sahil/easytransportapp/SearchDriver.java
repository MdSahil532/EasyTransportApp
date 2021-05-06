package com.sahil.easytransportapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.media.session.MediaSession;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchDriver extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {
    Toolbar toolbar;
    String[] vehicleType = new String[]{"None", "Goods", "Luxury"};
    Button searchButton;
    TextInputLayout layout;
    static ProgressDialog d;
    RadioGroup radioGroup;
    RadioButton radioButton;
    LinearLayout l;
    @SuppressLint("StaticFieldLeak")
    static EditText enterAddress;


    public void searchForDriver(View view) {
        String searchAddress = enterAddress.getText().toString();
        if (searchAddress.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Address ");
            builder.setMessage("You did not enter any Address ");
            builder.setIcon(android.R.drawable.stat_notify_error);
            builder.setPositiveButton("close", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }else if (radioGroup.getCheckedRadioButtonId() == -1){
            shoMessage("pls select vehicle type");
        } else {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
                d = new ProgressDialog(SearchDriver.this);
                d.setMessage("Searching..");
                d.show();
                LatLng latLng = getLatLong(searchAddress);
                if (latLng != null) {
                    int id = radioGroup.getCheckedRadioButtonId();
                    Double pLat = (double) (latLng.latitude);
                    Double pLon = (double) (latLng.longitude);
                    radioButton = (RadioButton) findViewById(id);
                    String vehicle_type = radioButton.getText().toString();
                    if (vehicle_type.equals("Goods")){
                        Intent in = new Intent(SearchDriver.this,DriverSearchResult.class);
                        Bundle b = new Bundle();
                        b.putDouble("lat",pLat);
                        b.putDouble("lon",pLon);
                        b.putString("vehicleType","Goods");
                        in.putExtras(b);
                        startActivity(in);
                    }else {
                        Intent i = new Intent(SearchDriver.this,DriverSearchResult.class);
                        Bundle bundle = new Bundle();
                        bundle.putDouble("lat",pLat);
                        bundle.putDouble("lon",pLon);
                        bundle.putString("vehicleType","Luxury");
                        i.putExtras(bundle);
                        startActivity(i);
                    }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("pls enter a valid address or proper place name ");
                builder.setIcon(android.R.drawable.stat_notify_error);
                builder.setPositiveButton("close", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                d.dismiss();
            }
        }else{
                Toast.makeText(SearchDriver.this, " Internet is required for searching driver Turn on the internet  ", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder adb = new AlertDialog.Builder(SearchDriver.this);
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
                        Toast.makeText(SearchDriver.this, " Internet is required for searching driver Turn on the internet  ", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                });
                adb.setIcon(android.R.drawable.ic_dialog_info);
                adb.create().show();
        }
    }
}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_driver);
        toolbar = findViewById(R.id.toolbar_search);
        searchButton = findViewById(R.id.searchDriver);
        radioGroup = findViewById(R.id.radio_group1);
        l = findViewById(R.id.linear_layoutL);
        enterAddress = findViewById(R.id.enterAddress);
        layout = findViewById(R.id.input_l);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Search for driver");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        layout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
                    String tag5 = enterAddress.getTag().toString();
                    Intent intentI = new Intent(SearchDriver.this, MapsActivity.class);
                    intentI.putExtra("tag", tag5);
                    startActivity(intentI);
                }else{
                    Toast.makeText(SearchDriver.this, "Internet is required for using google map Turn on the internet ", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder adb = new AlertDialog.Builder(SearchDriver.this);
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
                            Toast.makeText(SearchDriver.this, "Internet is required for using google map Turn on the internet ", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }
                    });
                    adb.setIcon(android.R.drawable.ic_dialog_info);
                    adb.create().show();
                }
            }
        });
        enterAddress.setOnKeyListener(this);
        l.setOnClickListener(this);
        toolbar.setOnClickListener(this);

        if (ActivityCompat.checkSelfPermission(
                SearchDriver.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                SearchDriver.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SearchDriver.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    public LatLng getLatLong(String s){
        LatLng latLng = null;
        List<Address> addressList = new ArrayList<Address>();
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            addressList.clear();
            addressList = geocoder.getFromLocationName(s, 5);
            if (addressList != null && addressList.size() > 0){
                Address addressn  = addressList.get(0);
                latLng = new LatLng(addressn.getLatitude(),addressn.getLongitude());
            }else {
                Toast.makeText(this, "invalid place name or address", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            if (e.getMessage().equals("grpc failed")){
                Toast.makeText(this, "try again someThing went wrong make sure yoy internet connection and location service is on  ", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "someThing went wrong " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return latLng;
    }

    public void shoMessage(String msg){
        Toast toast = Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT);
        int backgroundColor = ResourcesCompat.getColor(toast.getView().getResources(), R.color.lightGrey, null);
        toast.getView().getBackground().setColorFilter(backgroundColor, PorterDuff.Mode.SRC_IN);
        toast.show();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.toolbar_search || view.getId() == R.id.linear_layoutL){
            keyHide();
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            searchForDriver(view);
            keyHide();
        }
        return false;
    }
    public void  keyHide(){
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}