package com.sahil.easytransportapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.ParseGeoPoint;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CALL_PHONE;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context context;
    List<Driver> drivers;
    LayoutInflater inflater;

    public SearchAdapter(Context context, List<Driver> drivers){
        this.context = context;
        this.drivers = drivers;
        this.inflater = LayoutInflater.from(context);

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_detail_show,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Driver driver = drivers.get(position);
        holder.driver_Name.setText(driver.getDriverName());
        holder.driver_PhoneNo.setText(driver.getDriverPhoneNo());
        String oName = "OwnerName - "+driver.getOwnerName();
        String oNo = "OwnerPhoneNo - "+driver.getOwnerPhoneNo();
        String vName = "VehicleName - "+driver.getVehicleName();
        String vType = "VehicleType - "+driver.getVehicleType();
        String vNo = "VehicleNo - "+driver.getVehicleNo();
        String dis = "Distance - "+driver.getDistance()+" miles away";
        holder.owner_name.setText(oName);
        holder.owner_phoneNo.setText(oNo);
        holder.vehicle_name.setText(vName);
        holder.vehicle_No.setText(vNo);
        holder.vehicle_type.setText(vType);
        holder.distance.setText(dis);
        Glide.with(context).load(driver.getPosterUrl()).into(holder.posterImage);
        holder.posterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog settingsDialog = new Dialog(context);
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(inflater.inflate(R.layout.popimage
                        , null));
                CircleImageView imageView = settingsDialog.findViewById(R.id.image_popup);
                Drawable drawable = holder.posterImage.getDrawable();
                if (drawable != null){
                    imageView.setImageDrawable(drawable);
                }
                settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                settingsDialog.show();
            }
        });

        holder.call.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                @SuppressLint("RestrictedApi") MenuBuilder builder = new MenuBuilder(context);
                MenuInflater inflater = new MenuInflater(context);
                inflater.inflate(R.menu.call_menu,builder);
                @SuppressLint("RestrictedApi") MenuPopupHelper helper = new MenuPopupHelper(context,builder,view);
                helper.setForceShowIcon(true);
                builder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                        if (item.getItemId() == R.id.call1){
                            Toast.makeText(context, "calling owner...", Toast.LENGTH_SHORT).show();
                            if (PermissionChecker.checkSelfPermission(context, CALL_PHONE)
                                    == PermissionChecker.PERMISSION_GRANTED) {
                                String o_no = driver.getOwnerPhoneNo();
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:"+o_no));
                                context.startActivity(intent);
                            }else  {
                                ((Activity) context).requestPermissions(new String[]{CALL_PHONE},401);
                            }
                        }else {
                            Toast.makeText(context, "calling driver...", Toast.LENGTH_SHORT).show();
                            if (PermissionChecker.checkSelfPermission(context, CALL_PHONE)
                                    == PermissionChecker.PERMISSION_GRANTED) {
                                String d_no = driver.getDriverPhoneNo();
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:"+d_no));
                                context.startActivity(callIntent);
                            }else  {
                                ((Activity) context).requestPermissions(new String[]{CALL_PHONE},401);
                            }
                        }
                        return false;
                    }
                    @Override
                    public void onMenuModeChange(@NonNull MenuBuilder menu) {}
                });
                helper.show();
            }
        });
        holder.location.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                @SuppressLint("RestrictedApi") MenuBuilder builder = new MenuBuilder(context);
                MenuInflater inflater = new MenuInflater(context);
                inflater.inflate(R.menu.location_menu,builder);
                @SuppressLint("RestrictedApi") MenuPopupHelper helper = new MenuPopupHelper(context,builder,view);
                helper.setForceShowIcon(true);
                builder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                        if (item.getItemId() == R.id.location3) {
                            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
                                Toast.makeText(context, "showing driver location", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, NavActivity.class);
                                ParseGeoPoint geoPoint = driver.getDriverLocation();
                                double lat = geoPoint.getLatitude();
                                double lng = geoPoint.getLongitude();
                                Bundle b = new Bundle();
                                b.putDouble("Latitude", lat);
                                b.putDouble("Longitude", lng);
                                b.putString("value", "driver");
                                intent.putExtras(b);
                                context.startActivity(intent);
                            }else {
                                Toast.makeText(context, " Internet is required for using google map Turn on the internet  ", Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder adb = new AlertDialog.Builder(context);
                                adb.setTitle("Turn on internet");
                                adb.setMessage(" ops :( looks like Your internet connection is off");
                                adb.setPositiveButton("Go To Setting", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                        context.startActivity(intent);
                                        dialogInterface.dismiss();
                                    }
                                });
                                adb.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(context, " Internet is required for using google map Turn on the internet  ", Toast.LENGTH_SHORT).show();
                                        dialogInterface.dismiss();
                                    }
                                });
                                adb.setIcon(android.R.drawable.ic_dialog_info);
                                adb.create().show();
                            }
                        }
                        return false;
                    }
                    @Override
                    public void onMenuModeChange(@NonNull MenuBuilder menu) {}
                });
                helper.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView posterImage;
        ImageView call, chat, location;
        TextView driver_Name, driver_PhoneNo,owner_name,owner_phoneNo,vehicle_name,vehicle_No,vehicle_type;
        TextView distance;
        View black_line ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.driver_poster);
            driver_Name = itemView.findViewById(R.id.name_driver);
            driver_PhoneNo = itemView.findViewById(R.id.driverNo);
            owner_name = itemView.findViewById(R.id.OwnweName);
            black_line = itemView.findViewById(R.id.base_line);
            owner_phoneNo = itemView.findViewById(R.id.ownerPhoneNo);
            vehicle_name = itemView.findViewById(R.id.car_name);
            vehicle_No = itemView.findViewById(R.id.car_no);
            vehicle_type = itemView.findViewById(R.id.car_type);
            distance = itemView.findViewById(R.id.distance);
            call = itemView.findViewById(R.id.call_driver);
            chat = itemView.findViewById(R.id.chat_driver);
            location = itemView.findViewById(R.id.driver_Location);

        }
    }

}
