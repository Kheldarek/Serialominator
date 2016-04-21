package com.psfs.pz.serialominator;

import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.Math.ceil;

public class SeriesDetails extends AppCompatActivity
{
    TextView plot;
    TextView rating;
    TextView genre;
    TextView runtime;
    ImageView poster;
    TextView title;
    TextView yearfield;
    static final String API_URL = "http://www.omdbapi.com/?";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_details);
        plot = (TextView) findViewById(R.id.plot);
        rating = (TextView) findViewById(R.id.rating);
        genre = (TextView) findViewById(R.id.genre);
        runtime = (TextView) findViewById(R.id.runtime);
        poster = (ImageView) findViewById(R.id.poster);
        title = (TextView) findViewById(R.id.Title);
        yearfield = (TextView) findViewById(R.id.Year);
        String data = getIntent().getExtras().getString("DATA");
        Log.i("DATA", data);
        String parts[] = data.split("[|]");
        String name = parts[0];
        String year = parts[1];
        title.setText(name);
        yearfield.setText(year);
        Log.i("PARTS", name + year);
        if (Character.compare(year.charAt(year.length() - 1), '-') == 0)
        {
            year = year.substring(0, year.length() - 2);
        }
        new RetrieveSeries(name,year).execute();

    }


    class RetrieveSeries extends AsyncTask<Void, Void, String>
    {

        public String title;
        public String year;
        private Exception exception;

        public RetrieveSeries(String param, String param2)
        {
            title = param;
            year = param2;
        }

        protected void onPreExecute()
        {


        }

        protected String doInBackground(Void... urls)
        {
            String tmpResp = new String();

            try
            {
                title = title.replace(" ", "+");

                URL url = new URL(API_URL + "t=" + title + "&y=" + year + "&plot=long&r=json");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try
                {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null)
                    {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    tmpResp = stringBuilder.toString();

                } finally
                {
                    urlConnection.disconnect();
                }

                return tmpResp;
            } catch (Exception e)
            {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response)
        {

            if (response == null)
            {
                response = "THERE WAS AN ERROR";
            }

            Log.i("INFO", response);


            try
            {
                Log.i("RESP", response);
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                plot.setText(object.getString("Plot"));
                genre.setText(object.getString("Genre"));
                Picasso.with(getBaseContext())
                        .load(object.getString("Poster"))
                        .into(poster);
                rating.setText(object.getString("imdbRating"));
                runtime.setText(object.getString("Runtime"));

            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

    }
}

