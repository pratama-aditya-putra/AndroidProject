package com.example.temp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SignUp extends AppCompatActivity {

    Button myButton;
    EditText username, name, passwordConfirm, password;
    String mUsername, mName, mPasswordConfirm, mPassword;
    DatabaseReference dbreff;
    private ArrayList<AppUser> listAppUser = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Button myButton = (Button) findViewById(R.id.loginButton);
        name = findViewById(R.id.signName);
        passwordConfirm = findViewById(R.id.signPassCon);
        username = findViewById(R.id.signUser);
        password = findViewById(R.id.signPass);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().length() < 8){
                    username.setError("Username minimal length is 8 character");
                    return;
                }
                if(name.getText().length() < 8){
                    username.setError("Fullname minimal length is 8 character");
                    return;
                }
                else if(password.getText().length() < 8){
                    password.setError("Password minimal length is 8 character");
                    return;
                }
                else if(!password.getText().toString().equals(passwordConfirm.getText().toString())){
                    passwordConfirm.setError("Password doesn't match");
                    return;
                }
                mUsername = username.getText().toString();
                mName = name.getText().toString();
                mPassword = password.getText().toString();
                AppUser appUser = new AppUser(mName, mUsername, mPassword, "book");
                listAppUser = new ArrayList<>();
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
                            dbreff.child("user").child(mUsername).setValue(appUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    String urlProfile;
                                    String fullName;
                                    fullName = listAppUser.get(0).getName();
                                    switchActivities(username.getText().toString(), fullName, "book");
                                    Toast.makeText(SignUp.this, mUsername + " Berhasil Didaftarkan", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Toast.makeText(SignUp.this, " Gagal Mendaftarkan User", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
                            username.setError("Username sudah terdaftar");
                            return;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                };
                dbreff.child("user")
                        .orderByChild("username")
                        .equalTo(mUsername)
                        .addValueEventListener(valueEventListener);
            }
        });
    }

    private void switchActivities(String value, String value1, String value2){
        Intent switchActivityIntent = new Intent(this, Login.class);
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