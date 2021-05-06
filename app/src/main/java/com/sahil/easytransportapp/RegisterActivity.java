package com.sahil.easytransportapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.TextUtilsCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    EditText nameEditText, emailEditTex, phoneEditText, passEditText,confirmEditText;
    LinearLayout layout;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nameEditText = findViewById(R.id.nameEditText);
        toolbar = findViewById(R.id.toolbar_T);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Create An Account ");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        emailEditTex = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        phoneEditText.setText(phoneEditText.getText().toString().replaceAll(" ",""));
        phoneEditText.setSelection(phoneEditText.getText().length());
        passEditText = findViewById(R.id.passEditText);
        confirmEditText = findViewById(R.id.confirmEditText);
        layout = findViewById(R.id.conLayOut);
        toolbar.setOnClickListener(this);
        layout.setOnClickListener(this);
        confirmEditText.setOnKeyListener(this);

    }

    public void onSignUp(View view){
        final String userName = nameEditText.getText().toString();
        String email = emailEditTex.getText().toString();
        String phNum = phoneEditText.getText().toString();
        final String pass = passEditText.getText().toString();
        String confirmPass = confirmEditText.getText().toString();
        Pattern ps = Pattern.compile("^[a-zA-Z ]+$");
        Matcher ms = ps.matcher(userName);
        boolean bs = ms.matches();
        if (userName.isEmpty()){
            nameEditText.setError("Enter a username");
            showMessage("name can not be blank");
        }else if (!bs) {
            nameEditText.setError(" Enter valid  a username");
            showMessage("username must be characters and alphabet's");
        } else if (email.isEmpty()){
            emailEditTex.setError("Enter a email Address");
            showMessage("Email can not be blank");
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditTex.setError("Not an email type");
            showMessage("pls enter a valid email address");
        } else if (phNum.isEmpty()) {
            phoneEditText.setError("Enter your phone number");
            showMessage("phone number can not be blank");
        } else if (phNum.length() != 10 ){
            showMessage("Invalid mobile number , number should be of 10 digits");
        }else if (pass.isEmpty()) {
            passEditText.setError("Password  is required");
            showMessage("Password must required for security purpose");
        }else if (confirmPass.isEmpty()) {
            confirmEditText.setError("Conformation of password is required");
            showMessage("Can not be blank for password conformation");
        }else if (!pass.equals(confirmPass)){
            showMessage("Password is not Matching ");
        }else {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()){
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("Creating...");
                dialog.show();
                final ParseUser user = new ParseUser();
                user.setUsername(userName);
                user.setEmail(email);
                user.setPassword(pass);
                user.put("phoneNo", phNum);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            dialog.dismiss();
                            AlertDialog.Builder ad= new AlertDialog.Builder(RegisterActivity.this);
                            ad.setTitle(" Creating Successful ");
                            ad.setMessage("You successfully  create your account");
                            ad.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ParseUser.logInInBackground(userName, pass, new LogInCallback() {
                                        @Override
                                        public void done(ParseUser user, ParseException e) {
                                            if (e == null && user != null){
                                                dialog.dismiss();
                                                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                            }else {
                                                dialog.dismiss();
                                                showMessage("Error :- " +  e.getMessage());
                                                Log.i("Error ",e.getMessage());
                                            }
                                            finish();
                                        }
                                    });
                                }
                            });
                            ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ParseUser.logOut();
                                    onBackPressed();
                                }
                            });
                            ad.setIcon(android.R.drawable.ic_dialog_info);
                            ad.show();

                        }else {
                            dialog.dismiss();
                            showMessage("Error :-  "+ e.getMessage());
                            Log.i("Error ",e.getMessage());
                        }
                    }
                });
            }else {
                Toast.makeText(RegisterActivity.this, " Internet is required for creating an account Turn on the internet  ", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder adb = new AlertDialog.Builder(RegisterActivity.this);
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
                        Toast.makeText(RegisterActivity.this, " Internet is required for creating an account Turn on the internet  ", Toast.LENGTH_SHORT).show();
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
            onSignUp(view);
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
        if (view.getId() == R.id.conLayOut || view.getId() == R.id.toolbar_T){
            View v = getCurrentFocus();
            if (v != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }
}
