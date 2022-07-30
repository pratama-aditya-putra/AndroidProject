package com.example.temp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class Setting extends AppCompatActivity {
    savesharedprefrences pref;
    Switch mySwitch;
    Button myButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mySwitch = findViewById(R.id.mode);
        Intent intent = getIntent();
        String username = intent.getStringExtra("uname");
        pref = new savesharedprefrences(this);
        myButton = findViewById(R.id.back);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if(pref.getstate() == true){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else{
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        if(pref.getstate()==true){
            mySwitch.setChecked(true);
        }

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public  void onCheckedChanged(CompoundButton compoundButton, boolean ischecked){
                if(ischecked){
                    pref.setsstate(true);
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }else{
                    pref.setsstate(false);
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            }
        });
    }

}