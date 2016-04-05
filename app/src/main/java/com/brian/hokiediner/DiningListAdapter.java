package com.brian.hokiediner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by brian on 4/5/16.
 */
public class DiningListAdapter extends ArrayAdapter<String> {

    ArrayList<String> list = null;
    Context c;
    LayoutInflater inflater;

    public DiningListAdapter(Context context, ArrayList<String> list) {
        super(context, R.layout.dining_cell_model, list);
        this.list = list;
        this.c = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.dining_cell_model, null);
        }

        //Make a ViewHolder object
        final DiningViewHolder holder = new DiningViewHolder();
        final String diningHall = list.get(position);

        holder.title = (TextView) convertView.findViewById(R.id.cellTitleLabel);
        holder.title.setText(diningHall);

        return convertView;
    }

    private class DiningViewHolder
    {
        TextView title;
    }
}
