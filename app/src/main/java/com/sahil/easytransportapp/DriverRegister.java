package com.sahil.easytransportapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DriverRegister extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {
    Toolbar toolbar;
    TextInputLayout inputLayout;
    @SuppressLint("StaticFieldLeak")
    static EditText address;
    EditText ownerName,owner_No,vehicleName,vehicleNo;
    RadioGroup radioGroup;
    ScrollView scrollView;
    LinearLayout linearLayout;
    Button button;
    RadioButton radioButton;


    public void registerDriver(View view){
        String owner_name = ownerName.getText().toString();
        String owner_no = owner_No.getText().toString();
        String vehicle_name = vehicleName.getText().toString();
        String vehicle_no = vehicleNo.getText().toString();
        String address_place = address.getText().toString();
        Pattern ps = Pattern.compile("^[a-zA-Z ]+$");
        Matcher ms = ps.matcher(owner_name);
        boolean bs = ms.matches();
        if (owner_name.isEmpty()){
            ownerName.setError("Enter Owner Name");
            shoMessage("you did not enter owner name");
        }else if (!bs) {
            ownerName.setError("Invalid name type");
            shoMessage("name should be character type ");
        }else if (owner_no.isEmpty()){
            shoMessage("you did not enter owner number");
            owner_No.setError("Enter owner contact number");
        } else if (owner_no.length() != 10) {
            owner_No.setError("pls Enter a valid contact number");
            shoMessage("contact number should be 10 digit");
        }else if (vehicle_name.isEmpty()){
            shoMessage("you did not enter vehicle name");
            vehicleName.setError("Enter vehicle name");
        }else if (address_place.isEmpty()){
            shoMessage("you did not enter address");
            address.setError("Enter address");
        }else if (vehicle_no.isEmpty()){
            vehicleNo.setError("Enter vehicle No");
            shoMessage("you did not enter vehicle no");
        }else if(vehicle_no.length() > 12){
            vehicleNo.setError("Invalid vehicle No");
            shoMessage("pls  enter valid vehicle no");
        }
        else if (radioGroup.getCheckedRadioButtonId() == -1){
           shoMessage("pls Enter Vehicle type");
        }else {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
                final ProgressDialog showDialog = new ProgressDialog(DriverRegister.this);
                showDialog.setMessage("Registering details...");
                showDialog.show();
                LatLng latLng = getLatLong(address_place);
                if (latLng != null) {
                    ParseObject driverDetails = new ParseObject("DriverDetails");
                    if (ParseUser.getCurrentUser() != null){
                        driverDetails.put("driverName", ParseUser.getCurrentUser().getUsername());
                        driverDetails.put("driverPhoneNo", ParseUser.getCurrentUser().getString("phoneNo"));
                        ParseFile file = ParseUser.getCurrentUser().getParseFile("profileImage");
                        if (file != null){
                            String url  = file.getUrl();
                            String imageUrl = url.replace("127.0.0.1:1337","3.16.42.187");
                            driverDetails.put("driverPic",imageUrl);
                        }else {
                            String s = "https://images.unsplash.com/photo-1523482365499-ae61b2980dd0?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=667&q=80";
                            driverDetails.put("driverPic",s);
                        }
                    }
                    driverDetails.put("ownerName",owner_name);
                    driverDetails.put("OwnerPhoneNo", owner_no);
                    ParseGeoPoint addressGeoPoint = new ParseGeoPoint(latLng.latitude,latLng.longitude);
                    driverDetails.put("driverLocation",addressGeoPoint);
                    driverDetails.put("vehicleName",vehicle_name);
                    driverDetails.put("vehicleNo",vehicle_no);
                    int id = radioGroup.getCheckedRadioButtonId();
                    radioButton = (RadioButton) findViewById(id);
                    String vehicle_type = radioButton.getText().toString();
                    if (vehicle_type.equals("Goods")){
                        driverDetails.put("vehicleType","Goods");
                    }else  {
                        driverDetails.put("vehicleType","Luxury");
                    }
                    driverDetails.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DriverRegister.this);
                                builder.setTitle("Successful");
                                builder.setMessage("Your Driver details have  uploaded");
                                builder.setPositiveButton("Return Home", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        onBackPressed();
                                    }
                                });
                                android.app.AlertDialog pdialog = builder.create();
                                pdialog.show();
                            }else {
                                shoMessage("Error" + e.getMessage());
                            }
                            showDialog.dismiss();
                        }
                    });
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Error");
                    builder.setMessage("pls enter a valid address or proper place name ");
                    builder.setIcon(android.R.drawable.stat_notify_error);
                    builder.setPositiveButton("close", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    showDialog.dismiss();
                }
            }else {
                Toast.makeText(DriverRegister.this, " Internet is required for registering driver details Turn on the internet  ", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder adb = new AlertDialog.Builder(DriverRegister.this);
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
                        Toast.makeText(DriverRegister.this, " Internet is required for registering driver details Turn on the internet  ", Toast.LENGTH_SHORT).show();
                    }
                });
                adb.setIcon(android.R.drawable.ic_dialog_info);
                adb.show();
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_register);
        toolbar = findViewById(R.id.toolbar_r);
        inputLayout = findViewById(R.id.input_layout2);
        button = findViewById(R.id.register_button);
        radioGroup =(RadioGroup)findViewById(R.id.radio_group);
        ownerName = findViewById(R.id.ownerName);
        scrollView = findViewById(R.id.sc1);
        vehicleName = findViewById(R.id.vehicleName);
        linearLayout = findViewById(R.id.linear_layoutSc);
        address = findViewById(R.id.address_l);
        owner_No = findViewById(R.id.ownerNo);
        vehicleNo = findViewById(R.id.vehicleNo);
        vehicleNo.setText(vehicleNo.getText().toString().replaceAll(" ",""));
        vehicleNo.setSelection(vehicleNo.getText().length());
        owner_No.setText(owner_No.getText().toString().replaceAll(" ",""));
        owner_No.setSelection(owner_No.getText().length());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Register Driver Details");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        inputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
                    String tag4 = address.getTag().toString();
                    Intent in = new Intent(DriverRegister.this, MapsActivity.class);
                    in.putExtra("tag", tag4);
                    startActivity(in);
                }else {
                    Toast.makeText(DriverRegister.this, "Internet is required for using google map Turn on the internet ", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder adb = new AlertDialog.Builder(DriverRegister.this);
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
                            Toast.makeText(DriverRegister.this, "Internet is required for using google map Turn on the internet ", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }
                    });
                    adb.setIcon(android.R.drawable.ic_dialog_info);
                    adb.create().show();
                }

            }
        });
        vehicleNo.setOnKeyListener(this);
        scrollView.setOnClickListener(this);
        toolbar.setOnClickListener(this);
        linearLayout.setOnClickListener(this);

        if (ActivityCompat.checkSelfPermission(
                DriverRegister.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                DriverRegister.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DriverRegister.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            registerDriver(view);
            keyHide();
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.toolbar_r || view.getId() == R.id.sc1 || view.getId() == R.id.linear_layoutSc){
            keyHide();
        }

    }
    public void  keyHide(){
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}