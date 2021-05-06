package com.sahil.easytransportapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class ResetActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    LinearLayout layout;
    Toolbar toolbarT;
    TextView  rTextView;
    EditText emailEditText;
    Button resetButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        toolbarT = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbarT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reset Your Password");
        toolbarT.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbarT.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        emailEditText = findViewById(R.id.editText3);
        resetButton = findViewById(R.id.button3);
        rTextView = findViewById(R.id.textView14);
        layout  = findViewById(R.id.constraintLayout);
        layout.setOnClickListener(this);
        rTextView.setOnClickListener(this);
        toolbarT.setOnClickListener(this);
        emailEditText.setOnKeyListener(this);
    }

    public  void  clickReset(View view){
        String emailAddress = emailEditText.getText().toString();
        if (emailAddress.isEmpty() || emailAddress.matches("")){
            emailEditText.setError("Email address is empty");
            showMessage("email Address is must required for password resetting");
        }else {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()){
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("Sending....");
                dialog.show();
                ParseUser.requestPasswordResetInBackground(emailAddress, new RequestPasswordResetCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            dialog.dismiss();
                            showMessage("check ur email. An email was successfully sent to email address with reset instructions.");
                        }else {
                            dialog.dismiss();
                            showMessage("Something went wrong looks like your emailId is invalid! Pls enter a valid emailId " + e.getMessage() );
                        }
                    }
                });
            }else {
                Toast.makeText(ResetActivity.this, " Internet is required for resetting password Turn on the internet  ", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder adb = new AlertDialog.Builder(ResetActivity.this);
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
                        Toast.makeText(ResetActivity.this, " Internet is required for resetting password Turn on the internet  ", Toast.LENGTH_SHORT).show();
                    }
                });
                adb.setIcon(android.R.drawable.ic_dialog_info);
                adb.create().show();
            }
        }
    }
    private  void  showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            clickReset(view);
            View v = getCurrentFocus();
            if (v != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.constraintLayout || view.getId() == R.id.toolbar5 || view.getId() == R.id.textView14){
            View v = getCurrentFocus();
            if (v != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }
}
