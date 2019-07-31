package com.example.drinkingbuddies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

public class UserInterface extends AppCompatActivity {
    private TextView userTitle, priceText, hourText;
    private Button prefButton, filterButton, mapButton, favButton, setButton, setCancelButton, cancelMapButton;
    private Dialog setDialog;
    private EditText updatePricePref, updateHourPref;
    private String username;
    private Fragment mapFragment;

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

        //setup Set Dialog
        setDialog = new Dialog(this);
        setDialog.setContentView(R.layout.user_preferences);
        setButton = (Button)setDialog.findViewById(R.id.set_button);
        setCancelButton = (Button)setDialog.findViewById(R.id.set_cancel_button);
        updatePricePref = (EditText)setDialog.findViewById(R.id.price_pref_set);
        updateHourPref = (EditText)setDialog.findViewById(R.id.hour_pref_set);

        priceText = (TextView) findViewById(R.id.price_pref_text);
        hourText = (TextView) findViewById(R.id.hour_pref_text);
        prefButton = (Button)findViewById(R.id.pref_button);
        filterButton = (Button) findViewById(R.id.to_filter_button);
        mapButton = (Button)findViewById(R.id.to_map_button);
        favButton = (Button)findViewById(R.id.favorites_button);
        cancelMapButton = (Button)findViewById(R.id.cancel_map_button);
        cancelMapButton.setVisibility(View.INVISIBLE);
        cancelMapButton.setClickable(false);

        //setup user's preferences
        Cursor myCursor;
        String[] myProjection = {BarProvider.COLUMN_USERNAME, BarProvider.COLUMN_PRICEPREF, BarProvider.COLUMN_HOURPREF};
        String mySelection = BarProvider.COLUMN_USERNAME + " = ?";
        String[] mySelectionArgs = {username};
        try{
            myCursor = getContentResolver().query(BarProvider.CONTENT_URI_LOG, myProjection, mySelection, mySelectionArgs, null);
            if(myCursor.moveToFirst()){
                String price = myCursor.getString(myCursor.getColumnIndex(BarProvider.COLUMN_PRICEPREF));
                String hour = myCursor.getString(myCursor.getColumnIndex(BarProvider.COLUMN_HOURPREF));
                myCursor.close();
                if(price != null && price.compareTo("") != 0){
                    priceText.setText(price);
                }
                else{
                    priceText.setText("-");
                }
                if(hour != null && hour.compareTo("") != 0){
                    hourText.setText(hour);
                }
                else{
                    hourText.setText("-");
                }
            }
            else{
                //they don't match...
                myCursor.close();
                priceText.setText("-");
                hourText.setText("-");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        prefButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDialog();
            }
        });

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //populate dialog with list from database
            }
        });
        
        filterButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent filterIntent = new Intent(view.getContext(), FilterActivity.class);
                startActivity(filterIntent);
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapButton.setVisibility(View.INVISIBLE);
                mapButton.setClickable(false);
                cancelMapButton.setVisibility(View.VISIBLE);
                cancelMapButton.setClickable(true);
                mapFragment = new BarLocationsFragment();
                FragmentManager fragMan = getSupportFragmentManager();
                fragMan.beginTransaction().replace(R.id.mapFragmentContainer, mapFragment).commit();
            }
        });

        cancelMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragMan = getSupportFragmentManager();
                fragMan.beginTransaction().remove(mapFragment).commit();
                cancelMapButton.setVisibility(View.INVISIBLE);
                cancelMapButton.setClickable(false);
                mapButton.setVisibility(View.VISIBLE);
                mapButton.setClickable(true);
            }
        });
    }

    private void startDialog(){
        setDialog.setCancelable(false);
        setDialog.show();
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String errorMes = "";
                int errorcounter = 0;
                boolean mustToast = false;
                String price = updatePricePref.getText().toString();
                String hour = updatePricePref.getText().toString();
                if(price != null && price.compareTo("") != 0){
                    //update
                    ContentValues cvsPrice = new ContentValues();
                    cvsPrice.put(BarProvider.COLUMN_PRICEPREF, price);
                    String mySelection = BarProvider.COLUMN_USERNAME + " = ?";
                    String[] mySelectionArgs = {username};
                    int result = getContentResolver().update(BarProvider.CONTENT_URI_LOG, cvsPrice, mySelection, mySelectionArgs);
                    priceText.setText(price);
                }
                else{
                    errorMes = "Please set your price preference";
                    errorcounter++;
                    mustToast = true;
                }
                if(hour != null && hour.compareTo("") != 0){
                    //update
                    ContentValues cvsHour = new ContentValues();
                    cvsHour.put(BarProvider.COLUMN_HOURPREF, hour);
                    String mySelection = BarProvider.COLUMN_USERNAME + " = ?";
                    String[] mySelectionArgs = {username};
                    int result = getContentResolver().update(BarProvider.CONTENT_URI_LOG, cvsHour, mySelection, mySelectionArgs);
                    hourText.setText(hour);
                }
                else{
                    if(errorcounter > 0){
                        errorMes += "and your hour preference.";
                    }
                    else{
                        errorMes = "Please set your hour preference.";
                        mustToast = true;
                    }
                }
                if(mustToast){
                    Toast.makeText(UserInterface.this, errorMes, Toast.LENGTH_SHORT).show();
                }
                else{
                    setDialog.dismiss();
                    Toast.makeText(UserInterface.this, "Preferences updated!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        setCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialog.dismiss();
            }
        });
    }


}
