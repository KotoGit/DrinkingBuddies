package com.example.drinkingbuddies;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FilterFragment extends Fragment{
    private Button queryButton;
    private EditText dollarAmount;
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

       // Log.d("onCreateView","Here");

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText( getActivity(), "Hello", Toast.LENGTH_LONG ).show();
                if ( !dollarAmount.getText().toString().equals("") )
                {
                    dollar = dollarAmount.getText().toString();
                    int dollarInt = Integer.parseInt(dollar);

                    Toast.makeText(getActivity(), "Amount is $" + dollarInt, Toast.LENGTH_SHORT).show();
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
