package com.psfs.pz.serialominator.APIhandlers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.psfs.pz.serialominator.database.Episode;
import com.psfs.pz.serialominator.database.SeriesDB;
import com.psfs.pz.serialominator.database.TvSeries;
import com.psfs.pz.serialominator.listAdapters.SearchRowBean;
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

import static java.lang.Math.ceil;

/**
 * Created by psend on 11.05.2016.
 */
public class APIHandler
{

    public static final String API_EPISODES_URL = "http://www.omdbapi.com/?t=%s&season=%s";
    public static final String API_URL = "http://www.omdbapi.com/?s=%s&plot=short&r=json&type=series&page=%s&";
    public static final String API_SHOW_DETAILS_URL = "http://www.omdbapi.com/?t=%s&y=%s&plot=long&r=json";
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
    public static final String IMDBID_KEY = "imdbID";
    public static final String OMDB_ERROR_SERIES_NF = "{\"Response\":\"False\",\"Error\":\"Series or season not found!\"}";
    public static final String SPACE = " ";
    public static final String PLUS = "+";
    public static final String SPLIT = "|";
    public static final String DATA = "DATA";
    public static final String OMDB_ERROR_NF = "{\"Response\":\"False\",\"Error\":\"Movie not found!\"}";



    public static void LoadEpisodesToDB(String title, int SeriesID, Context context)
    {

        new EpisodeAdder(title, SeriesID, context).execute();

    }

    public static void LoadSeriesToDB(String title,String year, Context context)
    {
        new SeriesAdder(title,year, context).execute();
    }

    public static SearchRowBean[] GetSeries(String param, Context con, SearchRowBean[] resp, ProgressBar bar)
    {
        RetrieveSeriesTask tmp =  new RetrieveSeriesTask(param,con,resp,bar);
        tmp.execute();

        return resp;

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


    static class SeriesAdder extends AsyncTask<Void, Void, String>
    {

        public String title;
        public String year;
        public Context context;
        private Exception exception;

        public SeriesAdder(String param, String param2, Context cntxt)
        {
            title = param;
            year = param2;
            context = cntxt;
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

                URL url = new URL(String.format(API_SHOW_DETAILS_URL,title,year));
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
                Toast.makeText(context,NO_CONNECTION,Toast.LENGTH_LONG).show();
                return;
            }

            Log.i("INFO", response);


            try
            {
                Log.i("RESP", response);
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                TvSeries toAdd = new TvSeries();
                toAdd.setName(object.getString(TITLE_KEY));
                toAdd.setYear(object.getString(YEAR_KEY));
                toAdd.setPlot(object.getString(PLOT_KEY));
                toAdd.setGenre(object.getString(GENRE_KEY));
                toAdd.setImg(object.getString(POSTER_KEY));
                toAdd.setRating(object.getString(RATING_KEY));
                toAdd.setRuntime(object.getString(RUNTIME_KEY));
                toAdd.setIMDBid(object.getString(IMDBID_KEY));
                SeriesDB tmp =new SeriesDB(context);
                tmp.addSeries(toAdd);
                tmp.close();
                APIHandler.LoadEpisodesToDB(toAdd.getName(),tmp.getByNameAndYear(toAdd.getName(),toAdd.getYear()).getId(),context);


            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

    }


   static class RetrieveSeriesTask extends AsyncTask<Void, Void, String[]>
    {

        public String title;
        private Exception exception;
        int resultsCount,pageCount =1;
        Context context;
        SearchRowBean[] response_array;
        ProgressBar progressBar;

        public RetrieveSeriesTask(String param, Context con, SearchRowBean[] resp, ProgressBar bar)
        {
            title = param;
            context = con;
            response_array = resp;
            progressBar = bar;
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
                if(title.charAt(title.length()-1) ==SPACE.charAt(0))
                {
                    title = title.substring(0,title.length()-1);
                }
                Log.i("TITLE",title);
                title = title.replace(SPACE, PLUS);

                do
                {
                    URL url = new URL(String.format(API_URL, title, currentPage));
                    Log.i("URL", url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(2000);
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

                            if(tmpPage.contains(OMDB_ERROR_NF))
                            {
                                tmpResp[0]=tmpPage;
                                urlConnection.disconnect();
                                break;
                            }
                            resultsCount = object.getInt(RESULTS_KEY);
                            if(resultsCount>50) resultsCount = 50;
                            pageCount = (int)ceil((resultsCount / 10.0));
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
            progressBar.setVisibility(View.GONE);
            if (responseTab == null)
            {

                Toast.makeText(context,NO_CONNECTION, Toast.LENGTH_LONG).show();
                return;
            }
            if (responseTab[0].contains(OMDB_ERROR_NF))
            {
                Toast.makeText(context,SHOW_NOT_FOUND, Toast.LENGTH_LONG).show();
                return;
            }

            Log.i("INFO", responseTab[0]);
            String response;
            for(int j=0; j<pageCount; j++)
            {
                response = responseTab[j];
                try
                {   Log.i("RESP",response);
                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray search = object.getJSONArray(SEARCH_KEY);
                    Log.i("PAGE_COUNT","page =" + j);
                    Log.i("PAGE_CONTENT",search.toString());
                    if(j==0) response_array = new SearchRowBean[resultsCount];
                    for (int i = 0; i < search.length(); i++)
                    {
                        JSONObject tmp = search.getJSONObject(i);

                        response_array[seriesCount] = new SearchRowBean(
                                tmp.getString(POSTER_KEY), tmp.getString(TITLE_KEY), tmp.getString(YEAR_KEY)
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

        }
    }

}
