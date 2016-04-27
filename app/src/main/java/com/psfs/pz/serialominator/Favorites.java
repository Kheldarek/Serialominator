package com.psfs.pz.serialominator;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

public class Favorites extends AppCompatActivity {
    ListView movieList;
    Context context;
    TvSeries[] db_array;
    SeriesDB seriesDB;
    static final String DATA = "DATA";
    static final String SPLIT = "|";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        seriesDB = new SeriesDB(this);

        context =this;
        initFavList();
    }

    private void initFavList()
    {
        movieList = (ListView)findViewById(R.id.favList);
        List<TvSeries> lst = seriesDB.getAllSeries();
        db_array = new TvSeries[lst.size()];
        int i=0;
        for(TvSeries x : lst)
        {
            db_array[i] = x;
            i++;
        }
        movieList.setItemsCanFocus(false);
        movieList.setAdapter(new FavoritesAdapter(context, R.layout.favs_row,
                db_array));
        movieList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                Intent x = new Intent(Favorites.this,SeriesDetails.class);
                TvSeries tmp = (TvSeries)movieList.getItemAtPosition(position);
                x.putExtra(DATA,tmp.getName() + SPLIT + tmp.getYear());
                startActivityForResult(x,0);
            }
        });
    }
}
