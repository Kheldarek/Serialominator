package com.psfs.pz.serialominator;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by psend on 01.04.2016.
 */
public class SearchListAdapter extends ArrayAdapter<SearchRowBean>
{
    Context context;
    int layoutResourceId;
    SearchRowBean data[] = null;

    public SearchListAdapter(Context context, int layoutResourceId, SearchRowBean[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RowBeanHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RowBeanHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtYear = (TextView)row.findViewById(R.id.txtYear);
            row.setTag(holder);
        }
        else
        {
            holder = (RowBeanHolder)row.getTag();
        }

        SearchRowBean object = data[position];
        holder.txtTitle.setText(object.Title);
        Picasso .with(this.context)
                .load(object.imgUrl)
                .into((holder.imgIcon));
        holder.txtYear.setText(object.Year);
        return row;
    }

    static class RowBeanHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtYear;
    }

}
