package com.sahil.easytransportapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.hardware.input.InputManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RequestActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    RelativeLayout relativeLayout;
    ScrollView scrollView ;
    LocationListener listener;
    LatLng userLocationLatLng;
    LocationManager locationManager;
    Toolbar toolbar;
    Button requestButton;
    EditText weightEditText, pickUpContact, deliveryContact;
    @SuppressLint("StaticFieldLeak")
    static EditText pickUp_address,delivery_address;
    CircleImageView pickUp_Location,delivery_location;

    public void request(View view){
        String picUpAddress = pickUp_address.getText().toString();
        String deliveryAddress = delivery_address.getText().toString();
        String pContact = pickUpContact.getText().toString();
        String dContact = deliveryContact.getText().toString();
        String contentWeight = weightEditText.getText().toString();
        if (picUpAddress.isEmpty()){
            pickUp_address.setError("Enter a pick up Address");
            showMsg("You did not  enter any pick up address");
        }else if (deliveryAddress.isEmpty()){
            delivery_address.setError("Enter a pick up Address");
            showMsg("You did not  enter any  delivery address");
        }else if (pContact.isEmpty()){
            pickUpContact.setError("Enter a pick up contact number");
            showMsg("You did not  enter a pick up contact number");
        }else if (pContact.length() != 10 ){
            pickUpContact.setError("Enter a valid pick up contact number");
            showMsg("pick up contact number should be 10 digit");
        }else if (dContact.isEmpty()){
            deliveryContact.setError("Enter a delivery contact number");
            showMsg("You did not a enter a  delivery contact number");
        }else if (dContact.length() != 10 ){
            deliveryContact.setError("Enter a valid  delivery contact number");
            showMsg("delivery contact number should be 10 digit");
        }else if (contentWeight.isEmpty()){
            weightEditText.setError("Enter content weight");
            showMsg("You did not a enter  content weight");
        }else if (Integer.parseInt(contentWeight) > 200 ){
            weightEditText.setError("Max weight 200 kg");
            showMsg("Content weight should be <= 200 kg ");
        } else {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()){
                LatLng pick_latLng = getLatLong(picUpAddress);
                LatLng delivery_latLng = getLatLong(deliveryAddress);
                final ProgressDialog pDialog = new ProgressDialog(RequestActivity.this);
                pDialog.setMessage("wait a moment Requesting....");
                pDialog.show();
                    if (pick_latLng != null && delivery_latLng != null) {
                        ParseObject request = new ParseObject("Request");
                        if (ParseUser.getCurrentUser() != null){
                            request.put("requesterName", ParseUser.getCurrentUser().getUsername());
                            request.put("requesterContactNo", ParseUser.getCurrentUser().getString("phoneNo"));
                            ParseFile file = ParseUser.getCurrentUser().getParseFile("profileImage");
                            if (file != null){
                                String url  = file.getUrl();
                                String imageUrl = url.replace("127.0.0.1:1337","3.16.42.187");
                                request.put("imageUrl",imageUrl);
                            }else {
                                String s = "https://komps.sa.edu.au/wp-content/uploads/2018/10/placeholder-profile-sq.jpg";
                                request.put("imageUrl",s);
                            }
                        }
                        ParseGeoPoint pGeoPoint = new ParseGeoPoint(pick_latLng.latitude,pick_latLng.longitude);
                        request.put("pickUPLocation",pGeoPoint);
                        Double dLat = (double) (delivery_latLng.latitude);
                        Double dLon = (double) (delivery_latLng.longitude);
                        request.put("deliveryLat",String.valueOf(dLat));
                        request.put("deliverLon",String.valueOf(dLon));
                        request.put("pickupAddress", picUpAddress);
                        request.put("deliveryAddress", deliveryAddress);
                        request.put("pickUpContactNo", pContact);
                        request.put("deliveryContactNo", dContact);
                        request.put("contentWeightInKg", contentWeight);
                        request.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RequestActivity.this);
                                    builder.setTitle("Request Successful");
                                    builder.setMessage("Your request have  uploaded");
                                    builder.setPositiveButton("Return Home", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            onBackPressed();
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                } else {
                                    showMsg("Something went wrong could not upload your request :( try again  " + e.getMessage());
                                }
                                pDialog.dismiss();
                            }
                        });
                    }else {
                        AlertDialog.Builder ad = new AlertDialog.Builder(RequestActivity.this);
                        ad.setTitle("Invalid  Address type");
                        ad.setMessage(" Pls enter correct valid  pickup address or delivery delivery ");
                        ad.setPositiveButton("Close",null);
                        ad.setIcon(android.R.drawable.ic_dialog_info);
                        ad.show();
                        pDialog.dismiss();
                    }
            }else {
                Toast.makeText(RequestActivity.this, "Internet is required for requesting Turn on the internet ", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder adb = new AlertDialog.Builder(RequestActivity.this);
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
                        Toast.makeText(RequestActivity.this, "Internet is required for requesting Turn on the internet ", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_request);
        scrollView = findViewById(R.id.sc1);
        relativeLayout = findViewById(R.id.r_layout);
        requestButton = findViewById(R.id.requestButton);
        pickUpContact = findViewById(R.id.pickUp_contact);
        pickUpContact.setText(pickUpContact.getText().toString().replaceAll(" ",""));
        pickUpContact.setSelection(pickUpContact.getText().length());
        deliveryContact = findViewById(R.id.delivery_contact);
        deliveryContact.setText(deliveryContact.getText().toString().replaceAll(" ",""));
        deliveryContact.setSelection(deliveryContact.getText().length());
        weightEditText = findViewById(R.id.weightEditText);
        weightEditText.setText(weightEditText.getText().toString().replaceAll(" ",""));
        weightEditText.setSelection( weightEditText.getText().length());
        pickUp_address = findViewById(R.id.pickUp_address);
        delivery_address = findViewById(R.id.delivery_address);
        toolbar = findViewById(R.id.toolbar_request);
        toolbar.setOnClickListener(this);
        scrollView.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);
        weightEditText.setOnKeyListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Request for Driver");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        pickUp_Location = findViewById(R.id.pick_location);
        delivery_location = findViewById(R.id.Delivery_location);
        delivery_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                String tag1 = delivery_location.getTag().toString();
                if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()){
                    Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("tag",tag1);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else {
                    Toast.makeText(RequestActivity.this, "Internet is required for using google map Turn on the internet ", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder adb = new AlertDialog.Builder(RequestActivity.this);
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
                            Toast.makeText(RequestActivity.this, "Internet is required for using google map Turn on the internet ", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }
                    });
                    adb.setIcon(android.R.drawable.ic_dialog_info);
                    adb.create().show();
                }
            }
        });
        pickUp_Location.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
              String tag2 = pickUp_Location.getTag().toString();
              if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()){
                  Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                  intent.putExtra("tag",tag2);
                  startActivity(intent);
              }else {
                  Toast.makeText(RequestActivity.this, "Internet is required for using google map Turn on the internet ", Toast.LENGTH_SHORT).show();
                  AlertDialog.Builder adb = new AlertDialog.Builder(RequestActivity.this);
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
                          Toast.makeText(RequestActivity.this, "Internet is required for using google map Turn on the internet ", Toast.LENGTH_SHORT).show();
                          dialogInterface.dismiss();
                      }
                  });
                  adb.setIcon(android.R.drawable.ic_dialog_info);
                  adb.create().show();
              }
          }
      });

        if (ActivityCompat.checkSelfPermission(
                RequestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                RequestActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RequestActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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

    public void showMsg(String msg){
        Toast toast = Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT);
        int backgroundColor = ResourcesCompat.getColor(toast.getView().getResources(), R.color.lightGrey, null);
        toast.getView().getBackground().setColorFilter(backgroundColor, PorterDuff.Mode.SRC_IN);
        toast.show();
    }


    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            request(view);
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sc1 || view.getId() == R.id.r_layout || view.getId() == R.id.toolbar_request){
            keyHide();
        }
    }
}
