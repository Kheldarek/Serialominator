package com.psfs.pz.serialominator.listAdapters;

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

import com.psfs.pz.serialominator.APIhandlers.APIHandler;
import com.psfs.pz.serialominator.R;
import com.psfs.pz.serialominator.database.SeriesDB;
import com.psfs.pz.serialominator.database.TvSeries;
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
            holder.btn = (Button) row.findViewById(R.id.addBtn);
            holder.btn.setOnClickListener(BtnClickListener);
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
        holder.btn.setTag(position);
        return row;
    }

    static class RowBeanHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtYear;
        Button btn;
    }
    private View.OnClickListener BtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            View parent = v.getRootView();
            Log.d("BTNLST", "pos " + position );
            ListView tmp = (ListView)parent.findViewById(R.id.searchLst);
            SearchRowBean tmpRow = (SearchRowBean)tmp.getItemAtPosition(position);
            Log.d("sth", tmpRow.Title + " ");
            SeriesDB seriesDB = new SeriesDB(parent.getContext());
            TvSeries add = new TvSeries();
            add.setImg((tmpRow.imgUrl));
            add.setYear(tmpRow.Year);
            add.setName(tmpRow.Title);
            seriesDB.addSeries(add);
            int seriesID = seriesDB.getByNameAndYear(tmpRow.Title,tmpRow.Year).getId();
            seriesDB.close();
            APIHandler.LoadEpisodesToDB(tmpRow.Title,seriesID,parent.getContext());



        }
    };

}
