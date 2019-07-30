package com.example.drinkingbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UserInterface extends AppCompatActivity {
    TextView userTitle;
    EditText priceText, hourText;
    Button priceButton, hourButton, filterButton;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interface);
        Bundle extras = getIntent().getExtras();
        username = "";
        if(extras != null) {
            username = extras.getString("user");
        }
        userTitle = (TextView)findViewById(R.id.user_text);
        String welcomeText = "Welcome " + username + "!";
        userTitle.setText(welcomeText);

        priceText = (EditText)findViewById(R.id.price_pref_text);
        hourText = (EditText)findViewById(R.id.hour_pref_text);
        priceButton = (Button)findViewById(R.id.price_pref_button);
        hourButton = (Button)findViewById(R.id.hour_pref_button);
        filterButton = (Button) findViewById(R.id.to_filter_button);

        priceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strPrice = priceText.getText().toString();
                if(strPrice != null && strPrice.compareTo("") != 0){
                    int userPrice = Integer.parseInt(strPrice);
                    ContentValues cvs = new ContentValues();
                    cvs.put(BarProvider.COLUMN_PRICEPREF, userPrice);
                    int result = getContentResolver().update(BarProvider.CONTENT_URI_LOG, cvs, BarProvider.COLUMN_USERNAME + " = ?", new String[]{username});
                }
            }
        });
        
        filterButton.setOnClickListener( new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent filterIntent = new Intent(view.getContext(), FilterActivity.class);
                    startActivity(filterIntent);
                }
        });
    }


}
