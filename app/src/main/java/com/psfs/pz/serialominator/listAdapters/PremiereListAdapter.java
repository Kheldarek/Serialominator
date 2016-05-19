package com.psfs.pz.serialominator.listAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.psfs.pz.serialominator.R;
import com.psfs.pz.serialominator.database.Episode;
import com.psfs.pz.serialominator.database.SeriesDB;
import com.psfs.pz.serialominator.database.TvSeries;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by psend on 12.05.2016.
 */
public class PremiereListAdapter extends ArrayAdapter<Episode>
{
    Context context;
    int layoutResourceId;
    Episode data[] = null;
    SeriesDB seriesDB;


    public PremiereListAdapter(Context context, int layoutResourceId, Episode[] data) {
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
            holder.txtEpTitle = (TextView) row.findViewById(R.id.txtEpTitle);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtDate = (TextView)row.findViewById(R.id.txtYear);
            row.setTag(holder);

        }
        else
        {
            holder = (RowBeanHolder)row.getTag();
        }

        Episode object = data[position];
        seriesDB = new SeriesDB(context);
        TvSeries tmp= seriesDB.getTvSeries(object.getSeriesID());
        seriesDB.close();
        holder.txtTitle.setText(tmp.getName() + "(" + object.getSeason() + "-" + object.getEpisodeNumber() + ")" );
        holder.txtEpTitle.setText(object.getTitle());
        Picasso.with(this.context)
                .load(tmp.getImg())
                .into((holder.imgIcon));
        Date tmpDt = new Date(object.getReleased());
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
        holder.txtDate.setText(dateformat.format(tmpDt));

        return row;
    }

    static class RowBeanHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtEpTitle;
        TextView txtDate;

    }

}