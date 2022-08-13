package com.example.passwordmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class AddPassActivity extends AppCompatActivity {

    private static final int OK_RESULT_CODE = 1;
    private  int len_password = 8;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_credentials_activity);
        final EditText editTextUrl = (EditText) findViewById(R.id.editTextUrl);
        final EditText editTextUser = (EditText)findViewById(R.id.editTextUser);
        final EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        Button button_ok = (Button) findViewById(R.id.buttonOk);
        Button button_cancel = (Button) findViewById(R.id.buttonCancel);
        Button buttongenpass = (Button) findViewById(R.id.buttongenpass);
        SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);
        final TextView textlen = (TextView) findViewById(R.id.textlen);

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    finish();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CredentialsItem newItem =  new CredentialsItem("site:" + editTextUrl.getText(),
                        "user:" + editTextUser.getText(),
                        "password:" + editTextPassword.getText());
                try{
                    ArrayList<CredentialsItem> credential_list = (ArrayList<CredentialsItem>) getIntent().getSerializableExtra("credential_list");
                    credential_list.add(newItem);
                    returnParams(credential_list);
                    finish();
                }
                catch (Exception e){e.printStackTrace();}
            }
        });
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                textlen.setText("Password length " + Integer.toString(progress));
                len_password = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar) {}

        });
        buttongenpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password_generated;
                PasswordGenerator passgen = new PasswordGenerator();
                password_generated = passgen.generatePassword(len_password);
                editTextPassword.setText(password_generated);
            }
        });

    }

    protected void returnParams(ArrayList<CredentialsItem>credential_list) {
        Intent intent = new Intent();
        intent.putExtra("credential_list", credential_list);
        setResult(OK_RESULT_CODE, intent);
        finish();
    }

}
