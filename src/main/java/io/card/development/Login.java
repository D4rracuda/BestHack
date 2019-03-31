package io.card.development;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    public static final String APP_PREFERENCES = "mysettings";

    private String userPass;

    public static final String APP_PREFERENCES_NAME = "Pin";
    SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
        final String pass = sharedPreferences.getString("passwordStart", null);
        if(pass == null){
           FirstStart();
        } else {
            final EditText editPin = (EditText)findViewById(R.id.editText);


            Button signIn = (Button) findViewById(R.id.button);
            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userPass = editPin.getText().toString().trim();
                    if(userPass.equals(pass)){
                        FirstStart();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Pin неверный", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
        }
    }

    private void FirstStart() {
        // открытие SampleActivity
        Intent intent = new Intent(Login.this, SampleActivity.class);
        startActivity(intent);

    }
}
