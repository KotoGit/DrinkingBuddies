package com.example.drinkingbuddies;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
    private Button loginButton;
    private Button registerButton;
    private Button finalLoginButton;
    private Button loginCancelButton;
    private Dialog logDialog;
    private Dialog regDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logDialog = new Dialog(this);
        logDialog.setContentView(R.layout.login_dialog);
        loginButton = (Button)findViewById(R.id.loginButton);
        registerButton = (Button)findViewById(R.id.registerButton);
        finalLoginButton = (Button)logDialog.findViewById(R.id.final_login_button);
        loginCancelButton = (Button)logDialog.findViewById(R.id.login_cancel_button);
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
                EditText username = (EditText)findViewById(R.id.login_name);
                EditText pass = (EditText)findViewById(R.id.login_pass);
                String strUsername = username.getText().toString();
                String strPass = pass.getText().toString();
                //check if username and pass match the database
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
        regDialog = new Dialog(this);
        regDialog.setCancelable(false);
        //regDialog.setContentView(R.layout.register_dialog);
        //Button finalRegisterButton = (Button)findViewById();

    }
}
