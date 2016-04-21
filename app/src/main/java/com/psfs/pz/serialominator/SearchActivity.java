package com.psfs.pz.serialominator;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


import static java.lang.Math.ceil;

public class SearchActivity extends AppCompatActivity
{

    static final String API_URL = "http://www.omdbapi.com/?";
    EditText movieTitle;
    ProgressBar progressBar;
    ListView movieList;
    SearchRowBean[] response_array;
    Context context;
    int pageCount;
    int resultsCount;
    int resultsOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        SeriesDB seriesDB = new SeriesDB(this);

        movieTitle = (EditText) findViewById(R.id.searchTxt);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        movieList = (ListView) findViewById(R.id.searchLst);
        context = this;
        movieList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                //Toast.makeText(SuggestionActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                Intent x = new Intent(context,Favorites.class);
                startActivity(x);
            }
        });
        Button queryButton = (Button) findViewById(R.id.searchBtt);
        queryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new RetrieveMoviesTask(movieTitle.getText().toString()).execute();
            }
        });


    }


    private void initSeriesList()
    {
        movieList.setAdapter(new SearchListAdapter(context, R.layout.search_row,
                response_array));
        movieList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                Intent x = new Intent(SearchActivity.this,SeriesDetails.class);
                SearchRowBean tmp = (SearchRowBean) movieList.getItemAtPosition(position);
                x.putExtra("DATA",tmp.Title + "|" + tmp.Year);
                startActivityForResult(x,0);
            }
        });
    }

    class RetrieveMoviesTask extends AsyncTask<Void, Void, String[]>
    {

        public String title;
        private Exception exception;

        public RetrieveMoviesTask(String param)
        {
            title = param;
        }

        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);

        }

        protected String[] doInBackground(Void... urls)
        {
            String[] tmpResp = new String[1];
            String tmpPage;
            int currentPage = 1;
            try
            {
                title = title.replace(" ", "+");
                do
                {
                    URL url = new URL(API_URL + "s=" + title + "&plot=short&r=json&type=series&page=" + currentPage);
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
                        tmpPage = stringBuilder.toString();
                        if (currentPage ==1)
                        {
                            JSONObject object = (JSONObject) new JSONTokener(tmpPage).nextValue();
                            resultsCount = object.getInt("totalResults");
                            if(resultsCount>50) resultsCount = 50;
                            pageCount = (int)ceil((resultsCount / 10.0));
                            //resultsOffset = pageCount*10 - resultsCount;
                            tmpResp = new String[pageCount];
                            Log.i("PAGE_COUNT", pageCount + " ");
                        }
                        Log.i("ADD_PAGE",currentPage + " resp: " + tmpPage);
                        tmpResp[currentPage-1]=tmpPage;
                        currentPage++;
                    } finally
                    {
                        urlConnection.disconnect();
                    }
                }while(currentPage -1 < pageCount);
                return tmpResp;
            } catch (Exception e)
            {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String[] responseTab)
        {
            int seriesCount = 0;
            if (responseTab[0] == null)
            {
                responseTab[0] = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", responseTab[0]);
            String response;
            for(int j=0; j<pageCount; j++)
            {
                response = responseTab[j];
                try
                {   Log.i("RESP",response);
                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray search = object.getJSONArray("Search");
                    Log.i("PAGE_COUNT","page =" + j);
                    Log.i("PAGE_CONTENT",search.toString());
                    // int movieCount = object.getInt("totalResults");*/
                    if(j==0) response_array = new SearchRowBean[resultsCount];
                    for (int i = 0; i < search.length(); i++)
                    {
                        JSONObject tmp = search.getJSONObject(i);

                        response_array[seriesCount] = new SearchRowBean(
                                tmp.getString("Poster"), tmp.getString("Title"), tmp.getString("Year")
                        );
                        Log.i("resp_array",response_array[seriesCount].Title + "" + response_array[seriesCount].Year);
                        seriesCount++;
                    }
                    Log.i("DONE","Page "+j+" parsed");


                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            initSeriesList();
        }
    }
}
