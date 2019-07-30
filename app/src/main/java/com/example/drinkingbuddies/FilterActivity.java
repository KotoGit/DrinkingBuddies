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

public class FilterActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private Button queryButton;
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


        queryButton.setOnClickListener(this);

        if(isServicesOK()){             //just does some location checking stuff for Spence's part
            init();                     //when button is clicked, go to map fragment
        }
    }

    private void init(){
        Button mapButton = (Button) findViewById(R.id.mapButton);
        mapButton.setOnClickListener(this);
    }

    //------------------------------------------------Spence's code
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(available == ConnectionResult.SUCCESS){
            //yay
            Log.d(TAG, "isServicesOK: GooglePlayServices is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occurred, we can fix it tho
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
        }
        return false;

    }

    //------------------------------------------------------------------------------
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
                String coords = mCursor.getString(2);

                // TODO: Send coordinates to Spencer's fragment for directions

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


                Toast.makeText(this, "Amount is $" + dollarInt, Toast.LENGTH_SHORT).show();

                // ScrollView possibly
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

        else if ( view.getId() == R.id.mapButton )
        {
            Log.d("Map","clicked");
            Intent intent = new Intent(getApplicationContext(), MapActivity.class);
            startActivity(intent);
        }


    }

}
