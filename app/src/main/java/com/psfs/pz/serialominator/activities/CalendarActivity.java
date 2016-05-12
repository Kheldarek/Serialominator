package com.psfs.pz.serialominator.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.psfs.pz.serialominator.R;
import com.psfs.pz.serialominator.database.Episode;
import com.psfs.pz.serialominator.database.SeriesDB;
import com.psfs.pz.serialominator.database.TvSeries;
import com.psfs.pz.serialominator.listAdapters.FavoritesAdapter;
import com.psfs.pz.serialominator.listAdapters.PremiereListAdapter;

import java.util.List;

public class CalendarActivity extends AppCompatActivity
{

    ListView premieresList;
    Context context;
    Episode[] db_array;
    SeriesDB seriesDB;
    static final String DATA = "DATA";
    static final String SPLIT = "|";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        seriesDB = new SeriesDB(this);

        context = this;
        initPremieresList();
    }

    private void initPremieresList()
    {
        premieresList = (ListView) findViewById(R.id.premieresList);
        List<Episode> lst = seriesDB.getEpisodesSinceToday();
        db_array = new Episode[lst.size()];
        int i = 0;
        Log.i("CAL", lst.toString());
        for (Episode x : lst)
        {
            db_array[i] = x;
            i++;
        }
        premieresList.setAdapter(new PremiereListAdapter(context, R.layout.calendar_row,
                db_array));
    }
}
