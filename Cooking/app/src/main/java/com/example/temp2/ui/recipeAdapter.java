package com.example.temp2.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.temp2.R;
import com.example.temp2.recipeList;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class recipeAdapter extends ArrayAdapter<recipeList> {

    private Context mContext;
    int mResource;

    public recipeAdapter(@NonNull @NotNull Context context, int resource, @NonNull @NotNull ArrayList<recipeList> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Integer id = getItem(position).getId();
        String judul = getItem(position).getTitle();
        String deskripsi = getItem(position).getDesc();

        recipeList Resep = new recipeList(id,judul,deskripsi);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvJudul = (TextView) convertView.findViewById(R.id.listJudul);
        TextView tvDeskripsi = (TextView) convertView.findViewById(R.id.listKet);

        tvJudul.setText(Resep.getTitle());
        tvDeskripsi.setText(Resep.getDesc());

        return convertView;
    }
}
