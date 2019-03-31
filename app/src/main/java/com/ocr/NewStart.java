package com.ocr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NewStart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_start);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(NewStart.this);
        if(preferences.getString("allCardsIs", null) == null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("allCardsIs", 0 + ""); editor.apply();
        }

        Button button1 = (Button) findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewStart.this, StartActivity.class);
                startActivity(intent);
            }
        });

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewStart.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button buttonSett = (Button) findViewById(R.id.buttonSett);
        buttonSett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewStart.this, Settings.class);
                startActivity(intent);
            }
        });

        Button buttonLib = (Button) findViewById(R.id.buttonLib);
        buttonLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewStart.this, Lib.class);
                startActivity(intent);
            }
        });
    }
}
