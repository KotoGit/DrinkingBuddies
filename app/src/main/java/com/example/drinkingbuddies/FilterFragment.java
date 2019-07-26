package com.example.drinkingbuddies;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FilterFragment extends Fragment{
    private Button queryButton;
    private EditText dollarAmount;
    private TextView loadingBars;
    private String dollar;
    
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
        loadingBars = (TextView) rootView.findViewById(R.id.loading_text);

       // Log.d("onCreateView","Here");

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText( getActivity(), "Hello", Toast.LENGTH_LONG ).show();

                // Animation after being clicked
                RotateAnimation rotateAnimation = new RotateAnimation(0, 360);
                rotateAnimation.setDuration(1000);
                rotateAnimation.setInterpolator(new DecelerateInterpolator());
                queryButton.startAnimation(rotateAnimation);

                if ( !dollarAmount.getText().toString().equals("") )
                {

                    loadingBars.setVisibility(View.VISIBLE);
                    loadingBars.setText("Loading bars. . .");

                    dollar = dollarAmount.getText().toString();
                    int dollarInt = Integer.parseInt(dollar);

                    Toast.makeText(getActivity(), "Amount is $" + dollarInt, Toast.LENGTH_SHORT).show();

                    loadingBars.setVisibility(View.INVISIBLE);
                }

                else
                {
                    Toast.makeText( getActivity(), "Please enter dollar amount", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }


}
