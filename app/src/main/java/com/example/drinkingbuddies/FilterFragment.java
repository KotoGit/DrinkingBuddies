package com.example.drinkingbuddies;
import android.content.ContentProvider;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import java.util.Timer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FilterFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private Button queryButton;
    private EditText dollarAmount;
    private String [] bars;
    private String dollar;
    private Cursor mCursor;
    private ListView lv;

    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_filter, container, false);

        queryButton = (Button) rootView.findViewById(R.id.query_button);
        dollarAmount = (EditText) rootView.findViewById(R.id.query_amount);
        lv = (ListView) rootView.findViewById(R.id.bar_listview);
       // Log.d("onCreateView","Here");

        queryButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long l )
    {
        String mSelectionClause = BarProvider.COLUMN_NAME + " = ? ";
        String [] mSelectionArgs = new String []{ lv.getItemAtPosition(i).toString() };

        mCursor = mCursor = getActivity().getContentResolver().query(BarProvider.CONTENT_URI_LOC, null, mSelectionClause, mSelectionArgs, null);

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
            int dollarInt = 0;

            if ( !dollar.equals(""))
                dollarInt = Integer.parseInt(dollar);

            if ( !dollar.equals("") && dollarInt > 0 ) {


                Toast.makeText(getActivity(), "Amount is $" + dollarInt, Toast.LENGTH_SHORT).show();

                // ScrollView possibly
                String mSelectionClause = BarProvider.COLUMN_PRICE + " <= ? ";
                final String[] mSelectionArgs = new String[]{dollar};

                mCursor = getActivity().getContentResolver().query(BarProvider.CONTENT_URI_LOC, null, mSelectionClause, mSelectionArgs, null);

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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1, bars);

                lv.setAdapter(adapter);
                lv.setVisibility(View.VISIBLE);
                lv.setOnItemClickListener(this);
            }
            else if ( dollarInt < 0 )
            {
                Toast.makeText(getActivity(), "Please enter valid dollar amount.", Toast.LENGTH_SHORT).show();
            }
        }

    }


    public interface OnFragmentInteractionListener {
       void onStartFilter();
       void onStartDirections();
    }

}
