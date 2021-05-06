package com.sahil.easytransportapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

     EditText currentPassword , newPassword, newPasswordConfirm;
     Button changePassword;
     LinearLayout layout;
     Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        layout = findViewById(R.id.lLayout);
        toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Change Your Password");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        currentPassword = findViewById(R.id.previous_password);
        newPassword = findViewById(R.id.new_password);
        newPasswordConfirm = findViewById(R.id.confirm_newPassword);
        changePassword = (Button) findViewById(R.id.changePass_button);
        layout.setOnClickListener(this);
        toolbar.setOnClickListener(this);
        newPasswordConfirm.setOnKeyListener(this);
    }

    public void setChangePassword(final View view){
        String buttonText = changePassword.getText().toString();
        final ParseUser currentUser = ParseUser.getCurrentUser();
        String currentUserName = ParseUser.getCurrentUser().getUsername();
        String oldPassword = currentPassword.getText().toString();
        final String newPass = newPassword.getText().toString();
        String confirmTheNewPass = newPasswordConfirm.getText().toString();
        if (buttonText.equals("Change Password")) {
            if (oldPassword.isEmpty() || oldPassword.matches("")) {
                currentPassword.setError("Previous password is required ");
                showMsg("Yous must enter your previous password to change  password");
            } else if (newPass.isEmpty() || newPass.matches("")) {
                newPassword.setError("Enter your new password");
                showMsg("You did not enter your new Password");
            } else if (confirmTheNewPass.isEmpty() || confirmTheNewPass.matches("")) {
                newPasswordConfirm.setError("Confirmation of the new password is required");
                showMsg("you must confirm the password");
            } else if (!newPass.equals(confirmTheNewPass)) {
                showMsg(" The confirmation password did not match with the new password");
            } else {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()){
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("Changing password.....");
                    progressDialog.show();
                    ParseUser.logInInBackground(currentUserName, oldPassword, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                if (user != null) {
                                    currentUser.setPassword(newPass);
                                    currentUser.saveInBackground();
                                    AlertDialog.Builder adb = new AlertDialog.Builder(ChangePassword.this);
                                    adb.setTitle("Password change successful");
                                    adb.setMessage("Your password has changed to new password");
                                    adb.setPositiveButton("Return Home", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            onBackPressed();
                                        }
                                    });
                                    adb.setIcon(android.R.drawable.ic_notification_overlay);
                                    adb.show();
                                } else {
                                    showMsg("Your current password is incorrect");
                                }
                            }else {
                                Toast.makeText(ChangePassword.this, "error ; - " +e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    Toast.makeText(ChangePassword.this, " Internet is required for changing password Turn on the internet  ", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder adb = new AlertDialog.Builder(ChangePassword.this);
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
                            Toast.makeText(ChangePassword.this, " Internet is required for changing password Turn on the internet  ", Toast.LENGTH_SHORT).show();
                        }
                    });
                    adb.setIcon(android.R.drawable.ic_dialog_info);
                    adb.create().show();

                }
            }
        }else {
            onBackPressed();
        }

    }

    public void showMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.lLayout  || view.getId() == R.id.toolbar4){
            View view1 = getCurrentFocus();
            if (view1 != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            }
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            setChangePassword(view);
            View view1 = getCurrentFocus();
            if (view1 != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            }
        }
        return false;
    }
}