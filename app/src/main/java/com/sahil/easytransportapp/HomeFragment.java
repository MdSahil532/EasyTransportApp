package com.sahil.easytransportapp;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.ParseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class HomeFragment extends Fragment  {

    SwitchCompat driverMode ;
    LinearLayout  linearLayout;
    CardView search, request, show, register;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home,container,false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Home");

        search  = v.findViewById(R.id.card_view);
        request = v.findViewById(R.id.card_view2);
        register = v.findViewById(R.id.card_view3);
        show  = v.findViewById(R.id.card_view4);
        driverMode = v.findViewById(R.id.driverMode);
        linearLayout = v.findViewById(R.id.linear);
        String onOff = ParseUser.getCurrentUser().getString("driverMode");
       // Toast.makeText(getActivity(), "Driver mode now  " + onOff, Toast.LENGTH_LONG).show();
        if (onOff != null && !onOff.isEmpty()) {
            if (onOff.equals("on")) {
                driverMode.setChecked(true);
                Toast.makeText(getActivity(), "Driver Mode is On", Toast.LENGTH_SHORT).show();

            } else if (onOff.equals("off")) {
                driverMode.setChecked(false);
                Toast.makeText(getActivity(), "Driver Mode is Off", Toast.LENGTH_SHORT).show();
            }
        }else {
            driverMode.setChecked(false);
        }
        if (driverMode.isChecked()){
            linearLayout.setVisibility(View.VISIBLE);
            linearLayout.setClickable(true);
            register.setClickable(true);
            show.setClickable(true);
            /*
            ParseUser user = ParseUser.getCurrentUser();
            user.put("driverMode","on");
            user.saveInBackground();

             */
        }else {
            linearLayout.setVisibility(View.INVISIBLE);
            linearLayout.setClickable(false);
            register.setClickable(false);
            show.setClickable(false);
            /*
            Toast.makeText(getActivity(), "Driver Mode is Off", Toast.LENGTH_SHORT).show();
            ParseUser user = ParseUser.getCurrentUser();
            user.put("driverMode","off");
            user.saveInBackground();

             */
        }
        driverMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
                    if (b) {
                        linearLayout.setVisibility(View.VISIBLE);
                        linearLayout.setClickable(true);
                        register.setClickable(true);
                        show.setClickable(true);
                        ParseUser user = ParseUser.getCurrentUser();
                        user.put("driverMode", "on");
                        user.saveInBackground();

                    } else {
                        linearLayout.setVisibility(View.INVISIBLE);
                        linearLayout.setClickable(false);
                        register.setClickable(false);
                        show.setClickable(false);
                        ParseUser user = ParseUser.getCurrentUser();
                        user.put("driverMode", "off");
                        user.saveInBackground();
                    }
                }else {
                    Toast.makeText(getActivity(), "Need internet connection for driver mode ", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
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
                            Toast.makeText(getActivity(), "Need internet connection for driver mode turn on internet ", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }
                    });
                    adb.setIcon(android.R.drawable.ic_dialog_info);
                    adb.create().show();
                }

            }
        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),RequestActivity.class));
            }
        });
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),ShowRequest.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),DriverRegister.class));

            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),SearchDriver.class));
            }
        });
        return v;
    }
}
