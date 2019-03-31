package com.ocr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    private EditText log;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
        pass = sharedPreferences.getString("passwordStart", null);
        log = (EditText) findViewById(R.id.editText);
        if(pass == null){
            Intent intent = new Intent(Login.this, NewStart.class);
            startActivity(intent);
        }
        else {
            Button button = (Button) findViewById(R.id.button3);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(pass.equals(log.getText().toString().trim())){
                        Intent intent = new Intent(Login.this, NewStart.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Заблокированно", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
