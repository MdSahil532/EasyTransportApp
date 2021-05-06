package com.sahil.easytransportapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class ResultRequestShow extends AppCompatActivity {
    Toolbar toolbar3;
    TextView view;
    List<Requester> requesterList;
    private RecyclerView newRecyclerView;
    private ShowAdapter showAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_request_show);
        toolbar3 = findViewById(R.id.toolbar3);
        requesterList = new ArrayList<Requester>();
        newRecyclerView = findViewById(R.id.RequestShowResult);
        newRecyclerView.setHasFixedSize(true);
        newRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        view = findViewById(R.id.result_showing);
        setSupportActionBar(toolbar3);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Showing request");
        toolbar3.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar3.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getDataResult();

    }

    public void getDataResult(){
        Bundle b1 = getIntent().getExtras();
        double latitude = b1.getDouble("latitude");
        double longitude = b1.getDouble("longitude");
        LatLng locationLatLng = new LatLng(latitude,longitude);
        final ParseGeoPoint resultGeoPoint = new ParseGeoPoint(locationLatLng.latitude,locationLatLng.longitude);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
        query.whereNear("pickUPLocation",resultGeoPoint);
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects != null && objects.size() > 0){
                        int size = objects.size();
                        String s = size + " request near  you";
                        view.setText(s);
                        for (ParseObject ob : objects){
                            String r_name = ob.getString("requesterName");
                            String r_contact = ob.getString("requesterContactNo");
                            String r_url = ob.getString("imageUrl");
                            ParseGeoPoint geoPointR = ob.getParseGeoPoint("pickUPLocation");
                            Double distanceInMiles = resultGeoPoint.distanceInMilesTo(geoPointR);
                            Double round = (double) Math.round(distanceInMiles * 10) / 10;
                            String pDistance = String.valueOf(round);
                            String dlat = ob.getString("deliveryLat");
                            String dlon = ob.getString("deliverLon");
                            LatLng l = new LatLng(Double.parseDouble(dlat),Double.parseDouble(dlon));
                            ParseGeoPoint g = new ParseGeoPoint(l.latitude, l.longitude);
                            Double distanceI = resultGeoPoint.distanceInMilesTo(g);
                            Double round2 = (double) Math.round(distanceI * 10) / 10;
                            String dDistance = String.valueOf(round2);
                            String pAddress = ob.getString("pickupAddress");
                            String dAddress = ob.getString("deliveryAddress");
                            String weight = ob.getString("contentWeightInKg");
                            String pNo = ob.getString("pickUpContactNo");
                            String dNo = ob.getString("deliveryContactNo");
                            Requester r = new Requester();
                            r.setName(r_name);
                            r.setRequesterNo(r_contact);
                            r.setImageUrl(r_url);
                            r.setDeliVerAddress(dAddress);
                            r.setPickAddress(pAddress);
                            r.setPickUpNo(pNo);
                            r.setDeliveryNo(dNo);
                            r.setLocation(geoPointR);
                            r.setdLat(dlat);
                            r.setdLng(dlon);
                            r.setWeight(weight);
                            r.setPickDistance(pDistance);
                            r.setDeliveryDistance(dDistance);
                            requesterList.add(r);
                        }
                        showAdapter = new ShowAdapter(ResultRequestShow.this,requesterList);
                        newRecyclerView.setAdapter(showAdapter);

                    }else {
                        view.setText("No request near you");
                        Toast.makeText(ResultRequestShow.this, "data not found", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ResultRequestShow.this, "SomeThing went wrong check your internet"+ e.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (ShowRequest.dialog.isShowing()){
            ShowRequest.dialog.dismiss();
        }
    }
}