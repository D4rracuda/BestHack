package com.ocr;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends AppCompatActivity {
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        editText = (EditText) findViewById(R.id.editText2);
        Button button = (Button) findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Settings.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("passwordStart", editText.getText().toString().trim());
                editor.apply();

                Toast.makeText(getApplicationContext(), "Сохранено", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
