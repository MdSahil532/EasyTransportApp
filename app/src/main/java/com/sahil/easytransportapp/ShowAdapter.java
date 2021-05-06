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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.ParseGeoPoint;

import java.util.List;
import java.util.zip.Inflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CALL_PHONE;

public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.ViewHolder> {


    private Context context;
    List<Requester> requesters;
    LayoutInflater inflater;

    public ShowAdapter(Context ctx , List<Requester> requesters ){
        this.requesters = requesters;
        this.context = ctx;
        this.inflater = LayoutInflater.from(ctx);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_show,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Requester req = requesters.get(position);
        final String name = req.getName();
        String address_p = req.getPickAddress();
        String address_d = req.getDeliVerAddress();
        final String url = req.getImageUrl();
        String distance1 = "PickUp distance - "+req.getPickDistance()+" miles away";
        String distance2 = "Delivery distance - "+req.getDeliveryDistance()+" miles away";
        String  weightInKg = "Content weight - "+req.getWeight()+" kg";
        holder.distance1.setText(distance1);
        holder.distance2.setText(distance2);
        holder.weight.setText(weightInKg);
        holder.name.setText(name);
        holder.pAddress.setText(address_p);
        holder.dAddress.setText(address_d);
        Glide.with(context).load(url).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog settingsDialog = new Dialog(context);
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(inflater.inflate(R.layout.popimage
                        , null));
                CircleImageView imageView = settingsDialog.findViewById(R.id.image_popup);
                Drawable drawable = holder.imageView.getDrawable();
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
                inflater.inflate(R.menu.popupcall_menu,builder);
                @SuppressLint("RestrictedApi") MenuPopupHelper helper = new MenuPopupHelper(context,builder,view);
                helper.setForceShowIcon(true);
                builder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                        if (item.getItemId() == R.id.one) {
                            Toast.makeText(context, "calling requester", Toast.LENGTH_SHORT).show();
                            if (PermissionChecker.checkSelfPermission(context, CALL_PHONE)
                                    == PermissionChecker.PERMISSION_GRANTED) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + req.getRequesterNo()));
                                context.startActivity(intent);
                            } else {
                                ((Activity) context).requestPermissions(new String[]{CALL_PHONE}, 401);
                            }
                        } else if (item.getItemId() == R.id.two) {
                            Toast.makeText(context, "calling pickUp", Toast.LENGTH_SHORT).show();
                            if (PermissionChecker.checkSelfPermission(context, CALL_PHONE)
                                    == PermissionChecker.PERMISSION_GRANTED) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + req.getPickUpNo()));
                                context.startActivity(intent);
                            } else {
                                ((Activity) context).requestPermissions(new String[]{CALL_PHONE}, 401);
                            }
                        } else  {
                            Toast.makeText(context, "calling delivery", Toast.LENGTH_SHORT).show();
                            if (PermissionChecker.checkSelfPermission(context, CALL_PHONE)
                                    == PermissionChecker.PERMISSION_GRANTED) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + req.getDeliveryNo()));
                                context.startActivity(intent);
                            } else {
                                ((Activity) context).requestPermissions(new String[]{CALL_PHONE}, 401);
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
                inflater.inflate(R.menu.popuploc_menu,builder);
                @SuppressLint("RestrictedApi") MenuPopupHelper helper = new MenuPopupHelper(context,builder,view);
                helper.setForceShowIcon(true);
                builder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                        if (item.getItemId() == R.id.location1){
                            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
                                Toast.makeText(context, "Showing  pickUp location", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, NavActivity.class);
                                ParseGeoPoint geoPoint = req.getLocation();
                                double lat = geoPoint.getLatitude();
                                double lng = geoPoint.getLongitude();
                                Bundle b = new Bundle();
                                b.putDouble("Latitude", lat);
                                b.putDouble("Longitude", lng);
                                b.putString("value", "pickup");
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
                        }else {
                            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
                                Toast.makeText(context, "Showing  delivery location", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, NavActivity.class);
                                String dlat = req.getdLat();
                                String dlng = req.getdLng();
                                double lat = Double.parseDouble(dlat);
                                double lng = Double.parseDouble(dlng);
                                Bundle b = new Bundle();
                                b.putDouble("Latitude", lat);
                                b.putDouble("Longitude", lng);
                                b.putString("value", "delivery");
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
        return requesters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, distance1, distance2, pAddress, dAddress;
        TextView weight;
        CircleImageView imageView;
        ImageView call,chat, location;
        View line ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.r_name);
            distance1 = itemView.findViewById(R.id.distance1);
            pAddress = itemView.findViewById(R.id.address1);
            dAddress = itemView.findViewById(R.id.address2);
            line = itemView.findViewById(R.id.line);
            distance2 = itemView.findViewById(R.id.distance2);
            imageView = itemView.findViewById(R.id.posterImage);
            call = itemView.findViewById(R.id.call);
            location = itemView.findViewById(R.id.location);
            chat = imageView.findViewById(R.id.chat);
            weight = itemView.findViewById(R.id.weightTextView);
        }
    }
}