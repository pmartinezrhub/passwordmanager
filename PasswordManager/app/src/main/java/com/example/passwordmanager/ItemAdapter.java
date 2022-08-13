package com.example.passwordmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemAdapter extends ArrayAdapter<CredentialsItem> {
    public ItemAdapter(Context context, ArrayList<CredentialsItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CredentialsItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        TextView url = (TextView) convertView.findViewById(R.id.site);
        TextView user = (TextView) convertView.findViewById(R.id.user);
        TextView pass = (TextView) convertView.findViewById(R.id.password);
        url.setText(item.getSite());
        pass.setText(item.getPassword());
        user.setText(item.getUser());
        return convertView;
    }

}
