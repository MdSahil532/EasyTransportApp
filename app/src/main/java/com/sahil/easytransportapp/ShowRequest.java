package com.sahil.easytransportapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShowRequest extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    TextInputLayout layout;
    Toolbar showToolbar;
    LinearLayout lLayout;
    static ProgressDialog dialog;
    @SuppressLint("StaticFieldLeak")
    static EditText chooseLocation;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_request);
        chooseLocation = findViewById(R.id.chooseLocation);
        layout = findViewById(R.id.input_layout);
        lLayout = findViewById(R.id.layout_linear);
        button = findViewById(R.id.showDetails);
        showToolbar = findViewById(R.id.toolbar_show);
        setSupportActionBar(showToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Show User Request");
        showToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        showToolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
               String tag3 =  chooseLocation.getTag().toString();
               Intent intent = new Intent(ShowRequest.this,MapsActivity.class);
               intent.putExtra("tag" ,tag3);
               startActivity(intent);
                }else {
                    Toast.makeText(ShowRequest.this, "Internet is required for using google map Turn on the internet ", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder adb = new AlertDialog.Builder(ShowRequest.this);
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
                            Toast.makeText(ShowRequest.this, "Internet is required for using google map Turn on the internet ", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }
                    });
                    adb.setIcon(android.R.drawable.ic_dialog_info);
                    adb.create().show();
                }
            }
        });
        lLayout.setOnClickListener(this);
        showToolbar.setOnClickListener(this);
        chooseLocation.setOnKeyListener(this);

        if (ActivityCompat.checkSelfPermission(
                ShowRequest.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                ShowRequest.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ShowRequest.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }


    }

    public void requestShow(View view){
        String location = chooseLocation.getText().toString();
        if (location.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Address ");
            builder.setMessage("You did not enter any Address ");
            builder.setIcon(android.R.drawable.stat_notify_error);
            builder.setPositiveButton("close",null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }else {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
                dialog = new ProgressDialog(ShowRequest.this);
                dialog.setMessage("Searching for request...");
                dialog.show();
                LatLng latLngO = getLatLong(location);
                if (latLngO != null) {
                    Double lat = (double) (latLngO.latitude);
                    Double lng = (double) (latLngO.longitude);
                    Intent intent = new Intent(ShowRequest.this,ResultRequestShow.class);
                    Bundle b = new Bundle();
                    b.putDouble("latitude",lat);
                    b.putDouble("longitude",lng);
                    intent.putExtras(b);
                    startActivity(intent);

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Error");
                    builder.setMessage("pls enter a valid address or proper place name ");
                    builder.setIcon(android.R.drawable.stat_notify_error);
                    builder.setPositiveButton("close", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    dialog.dismiss();
                }
            }else {
                Toast.makeText(ShowRequest.this, " Internet is required for showing user request Turn on the internet  ", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder adb = new AlertDialog.Builder(ShowRequest.this);
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
                        dialogInterface.dismiss();
                        Toast.makeText(ShowRequest.this, " Internet is required for showing user request Turn on the internet  ", Toast.LENGTH_SHORT).show();
                    }
                });
                adb.setIcon(android.R.drawable.ic_dialog_info);
                adb.create().show();
            }
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
                Toast.makeText(this, "invalid address or place name ", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            if (e.getMessage().equals("grpc failed")){
                Toast.makeText(this, "try again someThing went wrong make sure yoy internet connection and location service is on  ", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "try again someThing went wrong " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return latLng;
    }

    public void  keyHide(){
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            requestShow(view);
            keyHide();
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.layout_linear || view.getId() == R.id.toolbar_show){
            keyHide();
        }
    }
}