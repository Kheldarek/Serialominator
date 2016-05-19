package com.psfs.pz.serialominator.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.psfs.pz.serialominator.R;
import com.psfs.pz.serialominator.database.Episode;
import com.psfs.pz.serialominator.database.SeriesDB;
import com.psfs.pz.serialominator.database.TvSeries;
import com.psfs.pz.serialominator.listAdapters.ToDoListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ToDoList extends AppCompatActivity {

    Context context;
    List<TvSeries> groups;
    HashMap<TvSeries, List<Episode>> children;
    ExpandableListView toDoList;
    ExpandableListAdapter toDoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        context = this;
        children = new HashMap<>();
        initToDoList();


    }

    private void initToDoList()
    {
        // get the listview
        toDoList = (ExpandableListView) findViewById(R.id.ToDoExList);

        // preparing list data
        SeriesDB seriesDB = new SeriesDB(this);
        groups = seriesDB.getAllSeries();
        for(TvSeries x : groups)
        {
            children.put(x,seriesDB.getShowUnwatchedEpisodes(x.getName(),x.getYear()));
        }


        toDoListAdapter = new ToDoListAdapter(context, groups, children);

        // setting list adapter
        toDoList.setAdapter(toDoListAdapter);
    }
}
