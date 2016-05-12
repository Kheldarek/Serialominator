package com.psfs.pz.serialominator.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.psfs.pz.serialominator.R;
import com.psfs.pz.serialominator.database.SeriesDB;

public class ToDoList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        SeriesDB seriesDB = new SeriesDB(this);
    }
}
