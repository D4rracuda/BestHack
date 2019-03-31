package io.card.development;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class Login extends AppCompatActivity {

    public static final String APP_PREFERENCES = "mysettings";

    public static final String APP_PREFERENCES_NAME = "Pin";
    SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
        String pass = sharedPreferences.getString("passwordStart", null);
        if(pass == null){
           ifFirstStart();
        } else {
            // тут получение пароля (обработка кнопки) и проверка пароля с переменной pass
        }
    }

    private void ifFirstStart(){

        class SharedPreferencesExample extends Activity {
        private static final String MY_SETTINGS = "my_settings";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            SharedPreferences sp = getSharedPreferences(MY_SETTINGS,
                    Context.MODE_PRIVATE);
            // проверяем, первый ли раз открывается программа
            boolean hasVisited = sp.getBoolean("hasVisited", false);

            if (!hasVisited) {
                // выводим нужную активность
                SharedPreferences.Editor e = sp.edit();
                e.putBoolean("hasVisited", true);
                e.commit(); // не забудьте подтвердить изменения
            }
        }

    }

    private void checkPass(){
        EditText editPin = (EditText)findViewById(R.id.editPin);
        String strNickName = editPin.getText().toString();
    }
}
