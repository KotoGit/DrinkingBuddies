package com.example.drinkingbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class UserInterface extends AppCompatActivity {
    TextView userTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interface);
        Bundle extras = getIntent().getExtras();
        String username = "";
        if(extras != null) {
            username = extras.getString("user");
        }
        userTitle = (TextView)findViewById(R.id.user_text);
        String welcomeText = "Welcome " + username + "!";
        userTitle.setText(welcomeText);
    }


}
