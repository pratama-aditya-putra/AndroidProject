package com.example.temp2;

import android.content.Context;
import android.content.SharedPreferences;

public class savesharedprefrences {

    Context context;
    SharedPreferences sharedPreferences;
    public savesharedprefrences(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("apreferences", Context.MODE_PRIVATE);
    }

    public void setsstate(Boolean bo){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("editor", bo);
        editor.apply();
    }

    public boolean getstate(){
        return sharedPreferences.getBoolean("editor", false);
    }
}
