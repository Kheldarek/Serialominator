package com.psfs.pz.serialominator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.psfs.pz.serialominator.R;

public class MainMenu extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void favs_Click(View view)
    {

        Intent goToFavs = new Intent(this,Favorites.class);

        startActivity(goToFavs);
    }

    public void search_Click(View view)
    {
        Intent goToSearch = new Intent(this,SearchActivity.class);
        startActivity(goToSearch);
    }

    public void todo_Click(View view)
    {

        Intent goToDo = new Intent(this,ToDoList.class);
        startActivity(goToDo);
    }

    public void calendar_Click(View view)
    {
        Intent goToCalendar = new Intent(this,CalendarActivity.class);
        startActivity(goToCalendar);
    }


}
