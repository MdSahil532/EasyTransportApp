package com.sahil.easytransportapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    ImageView editImageView, editImageView2, editPhoneNo;
    CircleImageView pickImage, imageVie_profile;
    ParseUser currentUser ;
    TextView profileEmail, profilePhoneNo;
    RelativeLayout relativeLayout;

    String imageName = UUID.randomUUID().toString() +".jpg";


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getPhoto();
            }
        }
    }
    public void getPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        final TextView profileName = (TextView) view.findViewById(R.id.profileTextView);
        profileEmail = (TextView) view.findViewById(R.id.profileTextView2);
        editImageView = (ImageView) view.findViewById(R.id.edit_profile);
        editImageView2 = (ImageView) view.findViewById(R.id.edit_email);
        editPhoneNo = (ImageView) view.findViewById(R.id.edit_phoneNo);
        pickImage = (CircleImageView) view.findViewById(R.id.pick_photo);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout1);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyHide();
            }
        });
        imageVie_profile = (CircleImageView) view.findViewById(R.id.imageView_profile);
        imageVie_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog settingsDialog = new Dialog(getActivity());
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.popup_imageshow
                        , null));
                CircleImageView imageView = settingsDialog.findViewById(R.id.image_popup);
                Drawable drawable = imageVie_profile.getDrawable();
                if (drawable != null){
                    imageView.setImageDrawable(drawable);
                }
                settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                settingsDialog.show();
               // keyHide();
            }
        });
        editImageView2.setOnClickListener(this);
        editPhoneNo.setOnClickListener(this);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profile");
        profilePhoneNo = (TextView)view.findViewById(R.id.phone_textView);
        currentUser = ParseUser.getCurrentUser();
        pickImage.setOnClickListener(this);
        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText nameEditText = new EditText(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(60,60,60,60);
                nameEditText.setLayoutParams(lp);
                RelativeLayout container = new RelativeLayout(getContext());
                RelativeLayout.LayoutParams rlParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                container.setLayoutParams(rlParams);
                container.addView(nameEditText);
                nameEditText.setText(ParseUser.getCurrentUser().getUsername());
                nameEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("edit or change  your username");
                builder.setTitle("EDIT ");
                builder.setView(container);
                builder.setPositiveButton("submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        final String str = nameEditText.getText().toString();
                        Pattern ps = Pattern.compile("^[a-zA-Z ]+$");
                        Matcher ms = ps.matcher(str);
                        boolean bs = ms.matches();
                        final ProgressDialog dialog1 = new ProgressDialog(getActivity());
                        dialog1.setMessage("Changing pls wait....");
                        dialog1.setCancelable(false);
                        if (str.isEmpty()){
                            Toast.makeText(getActivity(), " cannot be blank ", Toast.LENGTH_SHORT).show();
                        }else if (!bs) {
                            Toast.makeText(getActivity(), "Error :( make sure name should be character type ", Toast.LENGTH_SHORT).show();
                        } else {
                            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()){
                                dialog1.show();
                                relativeLayout.setEnabled(false);
                                imageVie_profile.setClickable(false);
                                currentUser.setUsername(str);
                                currentUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null){
                                            Toast.makeText(getActivity(), "you successfully updated your username ", Toast.LENGTH_SHORT).show();
                                            HomeActivity.usernameTextView.setText(str);
                                            profileName.setText(str);
                                        }else {
                                            Toast.makeText(getActivity(), "sry something went wrong :(", Toast.LENGTH_SHORT).show();
                                        }
                                        dialog1.dismiss();
                                        relativeLayout.setEnabled(true);
                                        imageVie_profile.setClickable(true);
                                    }
                                });
                            }else {
                                Toast.makeText(getActivity(), " Internet is required for updating username Turn on the internet  ", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(getActivity(), " Internet is required for updating username Turn on the internet  ", Toast.LENGTH_SHORT).show();
                                        dialogInterface.dismiss();
                                    }
                                });
                                adb.setIcon(android.R.drawable.ic_dialog_info);
                                adb.create().show();
                            }
                            }
                        }
                });
                builder.setNegativeButton("Cancel", null).create();
                builder.show();
            }
        });
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ParseUser.getCurrentUser() != null) {
            setProfilePic();
            String phoneNo = ParseUser.getCurrentUser().getString("phoneNo");
            profileName.setText(ParseUser.getCurrentUser().getUsername());
            profileEmail.setText(ParseUser.getCurrentUser().getEmail());
            profilePhoneNo.setText(phoneNo);
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.edit_email){
            final  EditText emailEditText = new EditText(getActivity());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(60,60,60,60);
            emailEditText.setLayoutParams(lp);
            RelativeLayout container = new RelativeLayout(getContext());
            RelativeLayout.LayoutParams rlParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            container.setLayoutParams(rlParams);
            container.addView(emailEditText);
            emailEditText.setText(ParseUser.getCurrentUser().getEmail());
            emailEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            new AlertDialog.Builder(getActivity())
                    .setTitle("EDIT")
                    .setMessage("edit or Change your email address")
                    .setView(container)
                    .setPositiveButton("submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final String str2 = emailEditText.getText().toString();
                            final ProgressDialog progressDialog = new ProgressDialog(getContext());
                            progressDialog.setMessage("Changing pls wait....");
                            progressDialog.setCancelable(false);
                            if (str2.isEmpty()){
                                Toast.makeText(getActivity(), " cannot be blank ", Toast.LENGTH_SHORT).show();
                            }else if (!Patterns.EMAIL_ADDRESS.matcher(str2).matches()){
                                Toast.makeText(getActivity(), " not an email  type pls enter a valid email ", Toast.LENGTH_SHORT).show();
                            } else {
                                ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()){
                                    progressDialog.show();
                                    relativeLayout.setEnabled(false);
                                    imageVie_profile.setClickable(false);
                                    currentUser.setEmail(str2);
                                    currentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Toast.makeText(getActivity(), " You successfully updated your email address", Toast.LENGTH_SHORT).show();
                                                HomeActivity.emailAddressTextView.setText(str2);
                                                profileEmail.setText(str2);
                                            }else {
                                                Toast.makeText(getActivity(), "sry something went wrong :(", Toast.LENGTH_SHORT).show();
                                            }
                                            progressDialog.dismiss();
                                            relativeLayout.setEnabled(true);
                                            imageVie_profile.setClickable(true);
                                        }
                                    });
                                }else {
                                    Toast.makeText(getActivity(), " Internet is required for updating email  Turn on the internet  ", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(getActivity(), " Internet is required for updating email  Turn on the internet  ", Toast.LENGTH_SHORT).show();
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    adb.setIcon(android.R.drawable.ic_dialog_info);
                                    adb.create().show();
                                }
                            }
                        }
                    })
                    .setNegativeButton("cancel",null)
                    .create()
                    .show();
        }else if (view.getId() == R.id.pick_photo){
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions( getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }else{
                getPhoto();

            }
        }else if (view.getId() == R.id.edit_phoneNo){
            final  EditText phoneNumberEditText = new EditText(getActivity());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(60,60,60,60);
            phoneNumberEditText.setLayoutParams(lp);
            RelativeLayout container = new RelativeLayout(getContext());
            RelativeLayout.LayoutParams rlParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            container.setLayoutParams(rlParams);
            container.addView(phoneNumberEditText);
            phoneNumberEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            phoneNumberEditText.setText(ParseUser.getCurrentUser().getString("phoneNo"));
            new AlertDialog.Builder(getActivity())
                    .setTitle("EDIT")
                    .setMessage("edit or Change your phone number")
                    .setView(container)
                    .setPositiveButton("submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final  String string = phoneNumberEditText.getText().toString();
                            final ProgressDialog pDialog = new ProgressDialog(getActivity());
                            pDialog.setMessage("Changing pls wait....");
                            pDialog.setCancelable(false);
                            if (string.isEmpty()){
                                Toast.makeText(getContext(), "cannot be blank {enter your phone no to change }", Toast.LENGTH_SHORT).show();
                            }if (string.length() != 10){
                                Toast.makeText(getContext(), "invalid mobile number number should be of 10 digit ", Toast.LENGTH_SHORT).show();
                            }else {
                                ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()){
                                    pDialog.show();
                                    relativeLayout.setEnabled(false);
                                    imageVie_profile.setClickable(false);
                                    currentUser.put("phoneNo",string);
                                    currentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null){
                                                profilePhoneNo.setText(string);
                                                Toast.makeText(getActivity(), "you successfully update your phone number", Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(getActivity(), "sry  something went wrong :(" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                            pDialog.dismiss();
                                            relativeLayout.setEnabled(true);
                                            imageVie_profile.setClickable(true);
                                        }
                                    });
                                }else {
                                    Toast.makeText(getActivity(), " Internet is required for updating phone no Turn on the internet  ", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(getActivity(), " Internet is required for updating phone no Turn on the internet  ", Toast.LENGTH_SHORT).show();
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    adb.setIcon(android.R.drawable.ic_dialog_info);
                                    adb.create().show();
                                }
                            }
                        }
                    })
                    .setNegativeButton("cancel",null)
                    .create()
                    .show();
        }
    }

    public void setProfilePic(){
        ParseFile file = ParseUser.getCurrentUser().getParseFile("profileImage");
        if (file != null) {
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null && data != null) {
                        Bitmap bitmap2 = BitmapFactory.decodeByteArray(data, 0, data.length);
                        imageVie_profile.setImageBitmap(bitmap2);
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ProgressDialog uploading = new ProgressDialog(getActivity());
        if (requestCode == 1 && resultCode == RESULT_OK  && data != null) {
            Uri selectedImage = data.getData();
            try {
                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()){
                    uploading.setMessage("Uploading pls wait....");
                    uploading.setCancelable(false);
                    relativeLayout.setEnabled(false);
                    imageVie_profile.setClickable(false);
                    uploading.show();
                    ParseFile file = new ParseFile(imageName, byteArray);
                    currentUser.put("profileImage", file);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                imageVie_profile.setImageBitmap(bitmap);
                                HomeActivity.profileImageView.setImageBitmap(bitmap);
                                Toast.makeText(getActivity(), " The image has been Uploaded :)", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getActivity(), "There has been an issue uploading the image :( " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            uploading.dismiss();
                            relativeLayout.setEnabled(true);
                            imageVie_profile.setClickable(true);
                        }
                    });
                }else {
                    Toast.makeText(getActivity(), " Internet is required for uploading image Turn on the internet  ", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getActivity(), " Internet is required for uploading image Turn on the internet  ", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }
                    });
                    adb.setIcon(android.R.drawable.ic_dialog_info);
                    adb.create().show();
                }
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getActivity(), "sry SomeThing went wrong :(  "+e.getMessage(), Toast.LENGTH_SHORT).show();
                uploading.dismiss();
                relativeLayout.setEnabled(true);
                imageVie_profile.setClickable(true);
            }

        }
    }

    public void  keyHide(){
        View v = getActivity().getCurrentFocus();
        if (v != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

}



