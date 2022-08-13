package com.example.passwordmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class PasswordListActivity extends AppCompatActivity {

    protected static final int REQUEST_CODE = 10;
    public ArrayList<CredentialsItem> items = new ArrayList<CredentialsItem>();
    String md5_hash = "";
    File appdir = new File(Environment.getExternalStorageDirectory() + File.separator + "passwordmanager");
    File passfile = new File(appdir.getAbsolutePath().toString(), "passwordmanager.dat");
    File credentials_file = new File(appdir.getAbsolutePath().toString(), "passwordmanager.dat");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credential_list_activity);
        md5_hash = getIntent().getExtras().getString("hashmd5");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fabquit = (FloatingActionButton) findViewById(R.id.fabquit);
        final ListView listview = (ListView) findViewById(R.id.listView);
        if (credentials_file_is_created()) {
            items = charge_items(items);
        }
        try {
            ItemAdapter myItemAdapter = new ItemAdapter(this, items);
            listview.setAdapter(myItemAdapter);
            myItemAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addCredentials = new Intent(view.getContext(), AddPassActivity.class);
                addCredentials.putExtra("credential_list", items);
                startActivityForResult(addCredentials, REQUEST_CODE);
            }
        });

        fabquit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                final int onclick_item_position = position;
                Intent editPassActivity = new Intent(view.getContext(), EditPassActivity.class);
                editPassActivity.putExtra("credential_list", items);
                editPassActivity.putExtra("position", position);
                startActivityForResult(editPassActivity, REQUEST_CODE);
            }
        });

    }

    private  ArrayList<CredentialsItem> charge_items(ArrayList<CredentialsItem> items) {
        byte[] encrypted_string = read_credentials_file();
        try {
            AdvancedEncryptionStandard aes = new AdvancedEncryptionStandard(md5_hash.getBytes());
            byte[] decrypted = aes.decrypt(encrypted_string);
            JSONObject jsonObj = new JSONObject(new String(decrypted));
            JSONArray jsonArr = new JSONArray(jsonObj.getJSONArray("credentials").toString());
            for(int v=0;v<jsonArr.length();v++){
                items.add(new CredentialsItem(jsonArr.getJSONObject(v).getString("site"), jsonArr.getJSONObject(v).getString("user"), jsonArr.getJSONObject(v).getString("password")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            try{
                ArrayList<CredentialsItem> credential_list = (ArrayList<CredentialsItem>) data.getSerializableExtra("credential_list");
                items = credential_list;
                refresh_credential_list(items);
                JSONObject jsonObj = construct_json_objet();
                save_to_credentials_file(jsonObj);
            }
            catch (Exception e){e.printStackTrace();}

        }
    }

    private JSONObject construct_json_objet() {
        JSONObject jsonObj = new JSONObject();
        Collection<JSONObject> credentials_items = new ArrayList<JSONObject>();
        for (int v = 0; v < items.size(); v++) {
            JSONObject jsonObjReg = new JSONObject();
            try {
                jsonObjReg.put("site", items.get(v).getSite());
                jsonObjReg.put("user", items.get(v).getUser());
                jsonObjReg.put("password", items.get(v).getPassword());
                credentials_items.add(jsonObjReg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            jsonObj.put("credentials", new JSONArray(credentials_items));
            return jsonObj;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    private void save_to_credentials_file(JSONObject jsonObj) {
        try {
            credentials_file.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(credentials_file);
            try {
                AdvancedEncryptionStandard aes = new AdvancedEncryptionStandard(md5_hash.getBytes("UTF-8"));
                byte[] encrypted_string = aes.encrypt(jsonObj.toString().getBytes("UTF-8"));
                fos.write(encrypted_string);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean credentials_file_is_created() {
        if (passfile.exists()) {
            return true;
        } else {
            return false;
        }
    }


    private byte[] read_credentials_file(){
        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {
            bytesArray = new byte[(int) passfile.length()];
            fileInputStream = new FileInputStream(passfile);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return bytesArray;
    }

    private void refresh_credential_list(ArrayList<CredentialsItem> credential_list){
        ListView listview = (ListView) findViewById(R.id.listView);
        ItemAdapter myItemAdapter = new ItemAdapter(this, credential_list);
        listview.setAdapter(myItemAdapter);
        JSONObject jsonObj = construct_json_objet();
        save_to_credentials_file(jsonObj);
        myItemAdapter.notifyDataSetChanged();
    }
}
