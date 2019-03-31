package com.ocr;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class Lib extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<DataOfCards> cards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        Button button = (Button) findViewById(R.id.button5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Lib.this);
                int counterOfCards = Integer.parseInt(preferences.getString("allCardsIs", "0"));
                for(int i = 0; i < counterOfCards; i++){
                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putString("NumberOfCard" + i, null);
                    editor.putString("TypeOfCard" + i, null);
                    editor.putString("NameOfBanck" + i, null);
                    editor.putString("Expire" + i, null);
                    editor.putString("Credit" + i, null);
                    editor.apply();
                }
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("allCardsIs", 0 + "");
                editor.apply();
                cards = new ArrayList<>();
                updateUI();
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Lib.this);
        int counterOfCards = Integer.parseInt(preferences.getString("allCardsIs", "0"));
        for (int i = 0; i < counterOfCards; i++){
            String num = preferences.getString("NumberOfCard" + i, "");
            String type = preferences.getString("TypeOfCard" + i, "");
            String type2 = preferences.getString("Credit" + i, "");
            String name = preferences.getString("NameOfBanck" + i, "");
            String date = preferences.getString("Expire" + i, "");
            cards.add(new DataOfCards(num, type, type2, date, name));
        }
        updateUI();

    }

    public void updateUI() {
        DataAdapter adapter = new DataAdapter(this, cards);
        recyclerView.setAdapter(adapter);
    }
}
