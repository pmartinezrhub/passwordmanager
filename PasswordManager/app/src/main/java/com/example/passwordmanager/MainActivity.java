package com.example.passwordmanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    static int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1;
    File appdir = new File(Environment.getExternalStorageDirectory() + File.separator + "passwordmanager");
    File passfile = new File(appdir.getAbsolutePath().toString(), "passwordmanager.md5");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = (TextView) findViewById(R.id.textView);
        final TextView password_text = (TextView) findViewById(R.id.loginpasstext);
        String firts_time_app = "Master Password";
        Button button_login = (Button) findViewById(R.id.button);
        FloatingActionButton fabquit = (FloatingActionButton) findViewById(R.id.fabquit);

        fabquit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        this.check_permissions();
        if (!password_file_is_created()) {
            Toast.makeText(this, "Firt time launch this app needs to setup a password, please enter an strong one, remember or note it", Toast.LENGTH_LONG).show();
            textView.setText(firts_time_app);
            button_login.setText("Create Password");
            button_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validate_password(password_text)) {
                        save_to_passfile(password_text.getText().toString());
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                }


            });
        } else {
            button_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    check_pass_is_correct(password_text.getText().toString());
                }


            });

        }

    }

    private void check_permissions() {
        Toast.makeText(this, "Checking permissions...", Toast.LENGTH_LONG).show();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            }


        }
    }

    private void save_to_passfile(String pass) {
        String password = pass;
        String md5_pass = "";
        try {
            passfile.getParentFile().mkdirs();
            OutputStreamWriter osw = new OutputStreamWriter(
                    new FileOutputStream(passfile));
            try {
                MD5Generator md5_generator = new MD5Generator(password);
                md5_pass = md5_generator.getGenerated_password();
            } catch (Exception e) {
                e.printStackTrace();
            }
            osw.write(md5_pass);
            osw.flush();
            osw.close();
            this.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean password_file_is_created() {
        if (passfile.exists()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean check_pass_is_correct(String pass) {
        String md5_file = "";
        try {
            md5_file = read_pass_file();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MD5Generator md5_generator = new MD5Generator(pass);
        String md5_pass = md5_generator.getGenerated_password().trim();
        if (md5_file.equals(md5_pass)) {
            Intent m2 = new Intent(this, PasswordListActivity.class);
            m2.putExtra("hashmd5", md5_pass);
            this.startActivity(m2);
            return true;
        } else {
            Toast.makeText(this, "Wrong password, please enter the good one", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private String read_pass_file() {
        BufferedReader br = null;
        String md5_hash = "";
        try {
            br = new BufferedReader(new FileReader(passfile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (line != null) {
                sb.append(line);
                try {
                    line = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            md5_hash = sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5_hash;
    }

    public boolean validate_password(TextView password_text) {
        boolean validated = true;
        if (!stringContainsNumber(password_text.getText().toString())) {
            Toast.makeText(this, "Password don't contains numbers", Toast.LENGTH_LONG).show();
            validated = false;
        }
        if (!stringContainsSpecialChars(password_text.getText().toString())) {
            Toast.makeText(this, "Password don't contains especial chars", Toast.LENGTH_LONG).show();
            validated = false;
        }
        if (password_text.getText().length() < 8) {
            Toast.makeText(this, "Password md5 to much short!", Toast.LENGTH_LONG).show();
            validated = false;
        }
        return validated;
    }

    public boolean stringContainsNumber(String s) {
        return Pattern.compile("[0-9]").matcher(s).find();
    }

    public boolean stringContainsSpecialChars(String password_text) {
        String password = password_text;
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(password);
        boolean b = m.find();
        if (b) {
            return true;
        } else {
            return false;
        }
    }

}
