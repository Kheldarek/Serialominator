package com.psfs.pz.serialominator;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

public class Favorites extends AppCompatActivity {
    ListView movieList;
    Context context;
    TvSeries[] db_array;
    SeriesDB seriesDB;
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
        movieList.setAdapter(new FavoritesAdapter(context, R.layout.favs_row,
                db_array));
    }
}
