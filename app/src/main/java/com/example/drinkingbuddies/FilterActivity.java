package com.example.drinkingbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.maps.model.LatLng;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private Button queryButton, logoutButton;
    private EditText dollarAmount;
    private String [] bars;
    private String dollar;
    private Cursor mCursor;
    private ListView lv;

    private static final String TAG = "MainActivitiy";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        queryButton = (Button) findViewById(R.id.query_button);
        dollarAmount = (EditText) findViewById(R.id.query_amount);
        logoutButton = (Button) findViewById(R.id.logout_button);


        queryButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long l )
    {
        String mSelectionClause = BarProvider.COLUMN_NAME + " = ? ";
        String [] mSelectionArgs = new String []{ lv.getItemAtPosition(i).toString() };

        mCursor = mCursor = getContentResolver().query(BarProvider.CONTENT_URI_LOC, null, mSelectionClause, mSelectionArgs, null);

        try{
            if (mCursor != null)
            {
                mCursor.moveToFirst();
                Double lat = Double.parseDouble(mCursor.getString(2));
                Double lon = Double.parseDouble(mCursor.getString(3));

                // TODO: Send coordinates to Spencer's fragment for directions

                Intent mapIntent = new Intent( getApplicationContext(), MapActivity.class);
                mapIntent.putExtra("latitude",lat);
                mapIntent.putExtra("longitude", lon);

                startActivity(mapIntent);
            }
        }
        catch ( SQLiteException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view)
    {
        if( view.getId() == R.id.query_button )
        {
            RotateAnimation rotateAnimation = new RotateAnimation(0, 360);
            rotateAnimation.setDuration(1000);
            rotateAnimation.setInterpolator(new DecelerateInterpolator());
            queryButton.startAnimation(rotateAnimation);

            dollar = dollarAmount.getText().toString();
            lv = (ListView) findViewById(R.id.bar_listview);
//        Log.d("onCreateView","Here");
            int dollarInt = 0;

            if ( !dollar.equals(""))
                dollarInt = Integer.parseInt(dollar);

            if ( !dollar.equals("") && dollarInt > 0 ) {

                String mSelectionClause = BarProvider.COLUMN_PRICE + " <= ? ";
                final String[] mSelectionArgs = new String[]{dollar};

                mCursor = getContentResolver().query(BarProvider.CONTENT_URI_LOC, null, mSelectionClause, mSelectionArgs, null);

                try {
                    if (mCursor != null) {
                        mCursor.moveToFirst();

                        bars = new String[mCursor.getCount()];

                        for (int i = 0; i < bars.length; i++) {
                            bars[i] = mCursor.getString(1);
                            mCursor.moveToNext();
                        }
                    }
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, bars);

                lv.setAdapter(adapter);
                lv.setVisibility(View.VISIBLE);
                lv.setOnItemClickListener(this);
            }
            else if ( dollarInt <= 0 )
            {
                Toast.makeText(this.getApplicationContext(), "Please enter valid dollar amount.", Toast.LENGTH_SHORT).show();
            }
        }

        else if ( view.getId() == R.id.logout_button)
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }


    }

}
