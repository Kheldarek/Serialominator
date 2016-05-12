package com.psfs.pz.serialominator.APIhandlers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.psfs.pz.serialominator.database.Episode;
import com.psfs.pz.serialominator.database.SeriesDB;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by psend on 11.05.2016.
 */
public class APIHandler
{

    public static final String API_EPISODES_URL = "http://www.omdbapi.com/?t=%s&season=%s";
    public static final String API_URL = "http://www.omdbapi.com/?s=%s&plot=short&r=json&type=series&page=%s&";
    public static final String POSTER_KEY = "Poster";
    public static final String RESULTS_KEY = "totalResults";
    public static final String TITLE_KEY = "Title";
    public static final String YEAR_KEY = "Year";
    public static final String SEARCH_KEY = "Search";
    public static final String NO_CONNECTION = "No internet connection";
    public static final String SHOW_NOT_FOUND = "TV Show not found";
    public static final String PLOT_KEY = "Plot";
    public static final String GENRE_KEY = "Genre";
    public static final String RUNTIME_KEY = "Runtime";
    public static final String RATING_KEY = "imdbRating";
    public static final String OMDB_ERROR_SERIES_NF = "{\"Response\":\"False\",\"Error\":\"Series or season not found!\"}";

    public static void LoadEpisodesToDB(String title, int SeriesID, Context context)
    {

        new EpisodeAdder(title, SeriesID, context).execute();

    }


    static class EpisodeAdder extends AsyncTask<Void, Void, ArrayList<String>>
    {

        public String title;
        public int SeriesID;
        public Context context;
        private Exception exception;

        public EpisodeAdder(String param, int param2, Context con)
        {
            title = param;
            SeriesID = param2;
            context = con;

        }

        protected void onPreExecute()
        {


        }

        protected ArrayList<String> doInBackground(Void... urls)
        {
            ArrayList<String> tmpResp = new ArrayList<String>();
            int season = 1;

            try
            {
                title = title.replace(" ", "+");
                String oneSeason;
                for (season = 1; ; season++)
                {
                    URL url = new URL(String.format(API_EPISODES_URL, title, season));
                    Log.i("URL", url.toString());
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
                        oneSeason = stringBuilder.toString();

                    } finally
                    {
                        urlConnection.disconnect();
                    }
                    if (!oneSeason.contains(OMDB_ERROR_SERIES_NF))
                        tmpResp.add(oneSeason);
                    else
                        break;
                }
                return tmpResp;
            } catch (Exception e)
            {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(ArrayList<String> response)
        {

            if (response == null)
            {
                Toast.makeText(context, NO_CONNECTION, Toast.LENGTH_LONG).show();
                return;
            }

            //Log.i("INFO", response);


            try
            {
                SeriesDB seriesDB = new SeriesDB(context);
                SimpleDateFormat x = new SimpleDateFormat("yyyy-MM-dd");
                for (int i = 0; i < response.size(); i++)
                {
                    Log.i("RESP", response.toString());
                    JSONObject root = (JSONObject) new JSONTokener(response.get(i)).nextValue();
                    int season = root.getInt("Season");
                    JSONArray episodelist = root.getJSONArray("Episodes");
                    for (int j = 0; j < episodelist.length(); j++)
                    {
                        Episode tmpEp = new Episode();
                        JSONObject tmpObj = episodelist.getJSONObject(j);
                        tmpEp.setEpisodeID(tmpObj.getString("imdbID"));
                        tmpEp.setEpisodeNumber(tmpObj.getInt("Episode"));
                        tmpEp.setSeason(i+1);
                        tmpEp.setTitle(tmpObj.getString("Title"));

                        Date date = new Date();
                        String release = tmpObj.getString("Released");
                        if (!release.contains("N/A"))
                        {
                            try
                            {
                                date = x.parse(release);
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            tmpEp.setReleased(date.getTime());
                        }
                        tmpEp.setWatched(0);
                        tmpEp.setSeriesID(SeriesID);

                        seriesDB.addEpisodes(tmpEp);
                    }
                }
                seriesDB.close();

            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

    }

}
