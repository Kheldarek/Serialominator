
package com.psfs.pz.serialominator.database;


/**
 * Created by psend on 13.04.2016.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class SeriesDB extends SQLiteOpenHelper
{
    static final String CREATE_SERIES_TABLE = "create table TvSeries(" +
            "id integer primary key autoincrement," + "imdbID text," + "name text," + "year text," + "img text," + "plot text," +
            "genre text," + "runtime text,"+ "rating text)";
    static final String CREATE_EPISODES_TABLE = "create table Episodes(" + "id integer primary key autoincrement," + "title text," +
            "seriesID text," + "episodeID text," + "episodeNumber int," + "watched integer," + "season int," + "released int" + ");";

    public SeriesDB(Context context)
    {
        super(context, "series.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

        db.execSQL(CREATE_SERIES_TABLE);
        db.execSQL(CREATE_EPISODES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }

    public void updateEpisode(Episode episode)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", episode.getTitle());
        values.put("seriesID", episode.getSeriesID());
        values.put("episodeID",episode.getEpisodeID());
        values.put("episodeNumber", episode.getEpisodeNumber());
        values.put("watched", episode.getWatched());
        values.put("season", episode.getSeason());
        values.put("released", episode.getReleased());
        String args[] = {episode.getId() + ""};
        db.update("Episodes", values, "id=?", args);
    }
    public void addSeries(TvSeries tvSeries)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", tvSeries.getName());
        values.put("imdbID",tvSeries.getIMDBid());
        values.put("year", tvSeries.getYear());
        values.put("img", tvSeries.getImg());
        values.put("plot", tvSeries.getPlot());
        values.put("rating",tvSeries.getRating());
        values.put("genre",tvSeries.getGenre());
        values.put("runtime", tvSeries.getRuntime());
        db.insertOrThrow("TvSeries", null, values);
    }

    public void addEpisodes(Episode episode)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", episode.getTitle());
        values.put("seriesID", episode.getSeriesID());
        values.put("episodeID", episode.getEpisodeID());
        values.put("episodeNumber", episode.getEpisodeNumber());
        values.put("watched", episode.getWatched());
        values.put("season", episode.getSeason());
        values.put("released", episode.getReleased());
        db.insertOrThrow("Episodes", null, values);
    }

    public List<Episode> getEpisodesSinceToday()
    {
        List<Episode> results = new ArrayList<>();
        String[] columns = {"title", "seriesID", "released", "season","episodeNumber"};
        SQLiteDatabase db = getReadableDatabase();
        String args[] = { new Date().getTime() + ""};
        Cursor cursor = db.query("Episodes", columns, "released > ?", args, null, null, "released ASC",null);
        while (cursor.moveToNext())
        {
            Episode episode = new Episode();
            episode.setTitle(cursor.getString(0));
            episode.setSeriesID(cursor.getInt(1));
            episode.setReleased(cursor.getLong(2));
            episode.setSeason(cursor.getInt(3));
            episode.setEpisodeNumber(cursor.getInt(4));
            results.add(episode);
        }

        return results;
    }

    public List<Episode> getShowUnwatchedEpisodes(String title, String year)
    {
        List<Episode> results = new ArrayList<>();
        TvSeries tvseries= getByNameAndYear(title,year);
        String[] columns = {"title", "seriesID", "released", "season","episodeNumber","id","watched","episodeID"};
        SQLiteDatabase db = getReadableDatabase();
        String args[] = { tvseries.getId()+"","0"};
        Cursor cursor = db.query("Episodes", columns, "seriesID=? and watched=?", args, null, null, null,null);
        while (cursor.moveToNext())
        {
            Episode episode = new Episode();
            episode.setTitle(cursor.getString(0));
            episode.setSeriesID(cursor.getInt(1));
            episode.setReleased(cursor.getLong(2));
            episode.setSeason(cursor.getInt(3));
            episode.setEpisodeNumber(cursor.getInt(4));
            episode.setId(cursor.getInt(5));
            episode.setWatched(cursor.getInt(6));
            episode.setEpisodeID(cursor.getString(7));
            results.add(episode);
        }

        return results;
    }
    public void delSeries(int id)
    {
        SQLiteDatabase db = getWritableDatabase();
        String[] arguments = {"" + id};
        db.delete("TvSeries", "id=?", arguments);
        deleteAllEpisodes(id);
    }

    public void delSeries(TvSeries tvSeries)
    {
        SQLiteDatabase db = getWritableDatabase();
        String[] arguments = {tvSeries.getName(), tvSeries.getYear()};
        db.delete("TvSeries", "name=? and year =?", arguments);
        deleteAllEpisodes(tvSeries.getId());

    }

    public void deleteAllEpisodes(int id)
    {
        SQLiteDatabase db = getWritableDatabase();
        String[] arguments = {"" + id};
        db.delete("Episodes", "seriesID=?", arguments);
    }

    public void updateSeries(TvSeries tvSeries)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", tvSeries.getName());
        values.put("year", tvSeries.getYear());
        values.put("img", tvSeries.getImg());
        String args[] = {tvSeries.getId() + ""};
        db.update("TvSeries", values, "id=?", args);
    }

    public List<TvSeries> getAllSeries()
    {
        List<TvSeries> series = new LinkedList<TvSeries>();
        String[] columns = {"id", "name", "year", "img"};
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("TvSeries", columns, null, null, null, null, null);
        while (cursor.moveToNext())
        {
            TvSeries tvSeries = new TvSeries();
            tvSeries.setId(cursor.getInt(0));
            tvSeries.setName(cursor.getString(1));
            tvSeries.setYear(cursor.getString(2));
            tvSeries.setImg(cursor.getString(3));
            series.add(tvSeries);
        }
        return series;
    }

    public TvSeries getTvSeries(int id)
    {
        TvSeries series = new TvSeries();
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"id", "name", "year", "img"};
        String args[] = {id + ""};
        Cursor cursor = db.query("TvSeries", columns, " id=?", args, null, null, null, null);
        if (cursor != null)
        {
            cursor.moveToFirst();
            series.setId(cursor.getInt(0));
            series.setName(cursor.getString(1));
            series.setYear(cursor.getString(2));
            series.setImg(cursor.getString(3));
        }
        return series;
    }

    public TvSeries getByNameAndYear(String nme, String yr)
    {
        TvSeries series = new TvSeries();
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"id", "name", "year", "img"};
        String args[] = {nme, yr};
        Cursor cursor = db.query("TvSeries", columns, " name=? and year=?", args, null, null, null, null);
        if(cursor!=null) {
            if (cursor.getCount() != 0) {

                cursor.moveToFirst();
                series.setId(cursor.getInt(0));
                series.setName(cursor.getString(1));
                series.setYear(cursor.getString(2));
                series.setImg(cursor.getString(3));
                return series;
            } else return null;
        }
        return series;
    }

    public List<TvSeries> getByYear(String year)
    {
        List<TvSeries> seriesy = new LinkedList<TvSeries>();
        String[] columns = {"id", "name", "year", "img"};
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select id,name,year,img from TvSeries where year='"
                + year +
                "' order by name asc", null);

/*safer rawQuery
         *
		 * Cursor cursor =db.rawQuery
		 * ("select id,name,year,img from TvSeries where year=?	order by name asc", nazwi);
		 * */

        while (cursor.moveToNext())
        {
            TvSeries series = new TvSeries();
            series.setId(cursor.getInt(0));
            series.setName(cursor.getString(1));
            series.setYear(cursor.getString(2));
            series.setImg(cursor.getString(3));
            seriesy.add(series);
        }
        return seriesy;
    }

}

