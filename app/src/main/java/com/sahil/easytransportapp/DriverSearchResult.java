package com.sahil.easytransportapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class DriverSearchResult extends AppCompatActivity {
    Toolbar toolbar_result;
    TextView resultShow;
    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    List<Driver> driverList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_search_result);
        driverList = new ArrayList<Driver>();
        recyclerView = findViewById(R.id.showResult);
        recyclerView.setLayoutManager(new LinearLayoutManager(DriverSearchResult.this));
        recyclerView.setHasFixedSize(true);
        toolbar_result = findViewById(R.id.toolbar_result);
        resultShow = findViewById(R.id.result_show);
        setSupportActionBar(toolbar_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Search result for driver");
        toolbar_result.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar_result.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
               if (SearchDriver.d.isShowing()){
                   SearchDriver.d.dismiss();
               }
            }
        });

        searchResultData();


    }

    public void searchResultData(){
        Bundle bundle1 = getIntent().getExtras();
        double latitude = bundle1.getDouble("lat");
        double longitude = bundle1.getDouble("lon");
        String v = bundle1.getString("vehicleType");
        LatLng latLng = new LatLng(latitude,longitude);
        final ParseGeoPoint locationGeoPoint = new ParseGeoPoint(latLng.latitude, latLng.longitude);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("DriverDetails");
        query.whereEqualTo("vehicleType",v);
        query.whereNear("driverLocation",locationGeoPoint);
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects != null && objects.size() > 0){
                        int size = objects.size();
                        String result = size + " Driver found near you";
                        resultShow.setText(result);
                        for (ParseObject obj : objects){
                            String dName = obj.getString("driverName");
                            String dNo = obj.getString("driverPhoneNo");
                            String url = obj.getString("driverPic");
                            String o_name = obj.getString("ownerName");
                            String o_no = obj.getString("OwnerPhoneNo");
                            String v_name = obj.getString("vehicleName");
                            String v_no = obj.getString("vehicleNo");
                            String v_type = obj.getString("vehicleType");
                            ParseGeoPoint geoPoint = obj.getParseGeoPoint("driverLocation");
                            Double distanceInMiles = locationGeoPoint.distanceInMilesTo(geoPoint);
                            Double round = (double) Math.round(distanceInMiles * 10) / 10;
                            String miles = String.valueOf(round);
                            Driver driverD = new Driver();
                            driverD.setDriverName(dName);
                            driverD.setDriverPhoneNo(dNo);
                            driverD.setDriverLocation(geoPoint);
                            driverD.setPosterUrl(url);
                            driverD.setOwnerName(o_name);
                            driverD.setOwnerPhoneNo(o_no);
                            driverD.setVehicleName(v_name);
                            driverD.setVehicleNo(v_no);
                            driverD.setVehicleType(v_type);
                            driverD.setDistance(miles);
                            driverList.add(driverD);
                        }
                        searchAdapter = new SearchAdapter(DriverSearchResult.this,driverList);
                        recyclerView.setAdapter(searchAdapter);
                    }else {
                        resultShow.setText("No driver found near  you");
                        Toast.makeText(DriverSearchResult.this, "data is null or empty ", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(DriverSearchResult.this, "Error : - "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (SearchDriver.d.isShowing()){
            SearchDriver.d.dismiss();
        }
    }
}