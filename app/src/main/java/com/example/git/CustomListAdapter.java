package com.example.git;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<Repo> {

    ArrayList<Repo> repos;
    Context context;
    int resource;

    public CustomListAdapter(Context context, int resource, ArrayList<Repo> repo) {
        super(context, resource, repo);
        this.repos = repo;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.custom_list_layout, null, true);

        }
        Repo product = getItem(position);

       ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        Picasso.get().load(product.getImage()).into(imageView);

        TextView txtName = (TextView) convertView.findViewById(R.id.txtName);
        txtName.setText(product.getName());

        TextView txtfull = (TextView) convertView.findViewById(R.id.txtFull);
        txtfull.setText(product.getFull());

        TextView txCount = (TextView)convertView.findViewById(R.id.txtCount);
        txCount.setText(product.getCount());

        return convertView;
    }
}
