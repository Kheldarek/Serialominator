package com.psfs.pz.serialominator;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by psend on 01.04.2016.
 */
public class FavoritesAdapter extends ArrayAdapter<TvSeries>
{
    Context context;
    int layoutResourceId;
    TvSeries data[] = null;
    private static final TvSeries[] EMPTY_SERIES_ARRAY = new TvSeries[0];


    public FavoritesAdapter(Context context, int layoutResourceId, TvSeries[] data) {
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
            holder.delBtn = (Button) row.findViewById(R.id.delBtn);
            holder.delBtn.setOnClickListener(BtnClickListener);
            row.setTag(holder);

        }
        else
        {
            holder = (RowBeanHolder)row.getTag();
        }

        TvSeries object = data[position];
        holder.txtTitle.setText(object.getName());
        Picasso .with(this.context)
                .load(object.getImg())
                .into((holder.imgIcon));
        holder.txtYear.setText(object.getYear());
        holder.delBtn.setTag(position);
        return row;
    }

    static class RowBeanHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtYear;
        Button delBtn;
    }



    private View.OnClickListener BtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            View parent = v.getRootView();
            Log.d("BTNLST", "pos " + position );
            ListView tmp = (ListView)parent.findViewById(R.id.favList);
            TvSeries tmpRow = (TvSeries)tmp.getItemAtPosition(position);
            Log.d("sth", tmpRow.getName() + " ");
            SeriesDB seriesDB = new SeriesDB(parent.getContext());
            TvSeries tmpSeries = seriesDB.getByNameAndYear(tmpRow.getName(),tmpRow.getYear());
            seriesDB.delSeries(tmpSeries.getId());
            List<TvSeries> list = new ArrayList<>();
            Collections.addAll(list, data);
            list.removeAll(Arrays.asList(data[position]));
            data = list.toArray(EMPTY_SERIES_ARRAY);
            for(TvSeries x : data)
            {
                Log.d("DATA", x.getName());
            }


            tmp.setAdapter(new FavoritesAdapter(parent.getContext(), R.layout.favs_row, data));
            seriesDB.close();



        }
    };
}
