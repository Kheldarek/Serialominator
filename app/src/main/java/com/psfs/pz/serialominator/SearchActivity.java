package com.psfs.pz.serialominator;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class   SearchActivity extends AppCompatActivity {

    EditText movieTitle;
    ProgressBar progressBar;
    ListView movieList;
    SearchRowBean[] response_array;
    Context context;
    static final String API_URL = "http://www.omdbapi.com/?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_search);


        movieTitle = (EditText) findViewById(R.id.searchTxt);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        movieList = (ListView) findViewById(R.id.searchLst);

        Button queryButton = (Button) findViewById(R.id.searchBtt);
        queryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new RetrieveMoviesTask(movieTitle.getText().toString()).execute();
            }
        });
        context = this;

    }

    private void initSeriesList()
    {
        movieList.setAdapter(new SearchListAdapter(context,R.layout.search_row,
                response_array));
    }

    class RetrieveMoviesTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        public String title;

        public RetrieveMoviesTask(String param)
        {
            title = param;
        }

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        protected String doInBackground(Void... urls) {

            // Do some validation he-re

            try {
                title = title.replace(" ","+");
                URL url = new URL(API_URL + "s=" + title + "&plot=short&r=json&type=series" );
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);



            try {
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray search = object.getJSONArray("Search");
                int movieCount = object.getInt("totalResults");
                if(movieCount>10) movieCount = 10;
                response_array = new SearchRowBean[movieCount];
                for(int i =0;i<movieCount;i++)
                {
                   JSONObject tmp =  search.getJSONObject(i);

                    response_array[i] = new SearchRowBean(
                            tmp.getString("Poster"),tmp.getString("Title"),tmp.getString("Year")
                    );
                }
                initSeriesList();


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
