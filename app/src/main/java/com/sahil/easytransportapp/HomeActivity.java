package com.sahil.easytransportapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.view.GravityCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.parse.GetDataCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

import static android.Manifest.permission.CALL_PHONE;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    static ImageView profileImageView;
    ProgressDialog dialog;
    static TextView usernameTextView;
    static TextView emailAddressTextView;
    Toolbar toolbar;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.changePass) {
            startActivity(new Intent(getApplicationContext(),ChangePassword.class));

        }else if (item.getItemId() == R.id.logout){
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
                dialog = new ProgressDialog(HomeActivity.this);
                dialog.setMessage("LogOut....");
                dialog.show();
                ParseUser.logOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }else {
                Toast.makeText(HomeActivity.this, " Need internet connection to log out turn on internet ", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder adb = new AlertDialog.Builder(HomeActivity.this);
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
                        Toast.makeText(HomeActivity.this, " Need internet connection to log out turn on internet ", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                });
                adb.setIcon(android.R.drawable.ic_dialog_info);
                adb.create().show();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Easy Transport");
        drawerLayout = findViewById(R.id.drawer_Layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        usernameTextView =(TextView) headerView.findViewById(R.id.nameTextView);
        emailAddressTextView =(TextView) headerView.findViewById(R.id.emailTextView);
        profileImageView =(ImageView) headerView.findViewById(R.id.profile_imageView);
        if (ParseUser.getCurrentUser() != null){
            usernameTextView.setText(ParseUser.getCurrentUser().getUsername());
            emailAddressTextView.setText(ParseUser.getCurrentUser().getEmail());
            profilePic();
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.home_item);
        }

        if (PermissionChecker.checkSelfPermission(HomeActivity.this, CALL_PHONE)
                != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this,new String[]{CALL_PHONE},2);
        }


    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            finishAffinity();
        }

    }

    public void profilePic(){
        ParseFile file = ParseUser.getCurrentUser().getParseFile("profileImage");
        if (file != null) {
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null && data != null) {
                        Bitmap bitmap2 = BitmapFactory.decodeByteArray(data, 0, data.length);
                        profileImageView.setImageBitmap(bitmap2);
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.home_item:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , new HomeFragment()).commit();
                break;
            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , new ProfileFragment()).commit();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}


