package com.sahil.easytransportapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.TextViewCompat;

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

import java.io.IOError;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,View.OnKeyListener {

    EditText emailEditText, passEditText;
    TextView forgetTextView, signUpTextView, welTextView, easTextView,transTextView;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailEditText = findViewById(R.id.editText3);
        passEditText = findViewById(R.id.editText5);
        forgetTextView = findViewById(R.id.textView5);
        signUpTextView = findViewById(R.id.textView7);
        layout = findViewById(R.id.cLayOut);
        welTextView = findViewById(R.id.textView);
        easTextView = findViewById(R.id.textView4);
        transTextView = findViewById(R.id.textView6);

        layout.setOnClickListener(this);
        welTextView.setOnClickListener(this);
        easTextView.setOnClickListener(this);
        transTextView.setOnClickListener(this);
        passEditText.setOnKeyListener(this);
        if (ParseUser.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }
        forgetTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ResetActivity.class));
            }
        });
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });


    }
    public void onLogin(View view){
        String emailId = emailEditText.getText().toString();
        String password = passEditText.getText().toString();

        if (emailId.isEmpty()){
            emailEditText.setError("Enter your username or email address  ");
            showMessage("email or username is required for login ");
        } else if (password.isEmpty()){
            passEditText.setError("Password Is required  ");
            showMessage("Password is required for login ");
        }else {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()){
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("LogIn....");
                dialog.show();
                ParseUser.logInInBackground(emailId, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e == null && user != null){
                            dialog.dismiss();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        }else {
                            dialog.dismiss();
                            showMessage("Error :- " +  e.getMessage());
                            Log.i("Error",e.getMessage());
                        }
                    }
                });
            }else {
                Toast.makeText(LoginActivity.this, "Turn on internet otherwise you wont be able to Login ", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder adb = new AlertDialog.Builder(LoginActivity.this);
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
                        Toast.makeText(LoginActivity.this, "Turn on internet otherwise you wont be able to Login ", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
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
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.cLayOut:
               keyHide();
           case R.id.textView:
               keyHide();
           case R.id.textView4:
               keyHide();
           case R.id.textView6:
               keyHide();
       }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            onLogin(view);
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

}
