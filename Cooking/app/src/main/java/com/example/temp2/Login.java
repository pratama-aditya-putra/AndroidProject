package com.example.temp2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Login extends AppCompatActivity {
    Button myButton;
    EditText username, password;
    String mUsername, mPassword;
    TextView myTxt;
    DatabaseReference dbreff;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private ArrayList<AppUser> listAppUser = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myButton = (Button)findViewById(R.id.loginButton);
        username = findViewById(R.id.logUser);
        password = findViewById(R.id.logPass);
        myTxt = findViewById(R.id.toSign);
        Intent intent = getIntent();
        if(intent.getStringExtra("uname") != null){
            username.setText(intent.getStringExtra("uname"));
        }
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().length() < 8){
                    username.setError("Username minimal length is 8 character");
                    return;
                }
                else if(password.getText().length() < 8){
                    password.setError("Password minimal length is 8 character");
                    return;
                }
                LinearLayout loadingScreen = (LinearLayout) findViewById(R.id.loadingScreen);
                loadingScreen.setVisibility(View.VISIBLE);
                dbreff = FirebaseDatabase.getInstance().getReference();
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listAppUser.clear();
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            AppUser appUserTemp = ds.getValue(AppUser.class);
                            listAppUser.add(appUserTemp);
                        }
                        if(listAppUser.isEmpty()){
                            //Toast.makeText(Login.this, "Username belum terdaftar", Toast.LENGTH_SHORT).show();
                            Toast.makeText(Login.this, " " + username.getText().toString() + " Tidak Ditemukan", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else{
                            if(listAppUser.get(0).getPassword().equals(password.getText().toString())){
                                String urlProfile;
                                String fullName;
                                fullName = listAppUser.get(0).getName();
                                urlProfile = listAppUser.get(0).getProfile();
                                switchActivities(username.getText().toString(), fullName, urlProfile);
                                Toast.makeText(Login.this, "Selamat Datang " + username.getText().toString(), Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(Login.this, "Password salah !!", Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                };
                dbreff.child("user")
                        .orderByChild("username")
                        .equalTo(username.getText().toString())
                        .addValueEventListener(valueEventListener);
            }
        });
        myTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchActivityIntent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(switchActivityIntent);
            }
        });

    }

    private void switchActivities(String value, String value1, String value2){
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        switchActivityIntent.putExtra("uname",value);
        switchActivityIntent.putExtra("full_name",value1);
        switchActivityIntent.putExtra("profile",value2);
        startActivity(switchActivityIntent);
        finish();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
}