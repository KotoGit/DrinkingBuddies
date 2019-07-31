package com.example.drinkingbuddies;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button loginButton, registerButton, finalLoginButton, loginCancelButton, finalRegisterButton, regCancelButton;
    private Dialog logDialog, regDialog;
    private EditText username, pass, regUser, regPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Login Dialog Setup
        logDialog = new Dialog(this);
        logDialog.setContentView(R.layout.login_dialog);
        finalLoginButton = (Button)logDialog.findViewById(R.id.final_login_button);
        loginCancelButton = (Button)logDialog.findViewById(R.id.login_cancel_button);
        username = (EditText)logDialog.findViewById(R.id.login_name);
        pass = (EditText)logDialog.findViewById(R.id.login_pass);

        //Register Dialog Setup
        regDialog = new Dialog(this);
        regDialog.setContentView(R.layout.register_dialog);
        finalRegisterButton = (Button)regDialog.findViewById(R.id.final_reg_button);
        regCancelButton = (Button)regDialog.findViewById(R.id.reg_cancel_button);
        regUser = (EditText)regDialog.findViewById(R.id.reg_name);
        regPass = (EditText)regDialog.findViewById(R.id.reg_pass);

        //Main Page Setup
        loginButton = (Button)findViewById(R.id.loginButton);
        registerButton = (Button)findViewById(R.id.registerButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginDialog();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerDialog();
            }
        });
    }

    private void loginDialog(){
        logDialog.setCancelable(false);
        logDialog.show();
        finalLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //login parsing logic
                String strUsername = username.getText().toString();
                String strPass = pass.getText().toString();
                //check if username and pass match the database
                Cursor myCursor;
                String[] myProjection = {BarProvider.COLUMN_USERNAME, BarProvider.COLUMN_PASSWD};
                String mySelection = BarProvider.COLUMN_USERNAME + " = ? AND " + BarProvider.COLUMN_PASSWD + " = ? ";
                String[] mySelectionArgs = {strUsername, strPass};
                try{
                    myCursor = getContentResolver().query(BarProvider.CONTENT_URI_LOG, myProjection, mySelection, mySelectionArgs, null);
                    if(myCursor.moveToFirst()){
                        //username and passwd match the database -> login!
                        myCursor.close();
                        Bundle userInterfaceBundle = new Bundle();
                        userInterfaceBundle.putString("user", strUsername);
                        Intent userInterfaceIntent = new Intent(view.getContext(), UserInterface.class);
                        userInterfaceIntent.putExtras(userInterfaceBundle);
                        startActivity(userInterfaceIntent);
                    }
                    else{
                        //they don't match...
                        myCursor.close();
                        Toast.makeText(MainActivity.this, "Username and password are not valid", Toast.LENGTH_SHORT).show();
                    }
                }catch(SQLException e){
                    e.printStackTrace();
                }catch(NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
        loginCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logDialog.dismiss();
            }
        });
    }

    private void registerDialog(){
        regDialog.setCancelable(false);
        regDialog.show();
        finalRegisterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //register parsing logic
                String strUsername = regUser.getText().toString();
                String strPass = regPass.getText().toString();
                //check if username already exists in database
                Cursor myCursor;
                String[] myProjection = {BarProvider.COLUMN_USERNAME};
                String mySelection = BarProvider.COLUMN_USERNAME + " = ? ";
                String[] mySelectionArgs = {strUsername};
                try{
                    myCursor = getContentResolver().query(BarProvider.CONTENT_URI_LOG, myProjection, mySelection, mySelectionArgs, null);
                    if(myCursor.moveToFirst()){
                        myCursor.close();
                        //username is already in the database
                        Toast.makeText(MainActivity.this, "This username is already in use.", Toast.LENGTH_LONG).show();
                    }
                    else{
                        //username is not in database
                        myCursor.close();
                        ContentValues cvs = new ContentValues();
                        cvs.put(BarProvider.COLUMN_USERNAME, strUsername);
                        cvs.put(BarProvider.COLUMN_PASSWD, strPass);
                        Uri uri = getContentResolver().insert(BarProvider.CONTENT_URI_LOG, cvs);
                        Bundle userInterfaceBundle = new Bundle();
                        userInterfaceBundle.putString("user", strUsername);
                        Intent userInterfaceIntent = new Intent(view.getContext(), UserInterface.class);
                        userInterfaceIntent.putExtras(userInterfaceBundle);
                        startActivity(userInterfaceIntent);
                    }
                }catch(SQLException e){
                    e.printStackTrace();
                }catch(NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
        regCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regDialog.dismiss();
            }
        });
    }
}
