
package com.psfs.pz.serialominator;


/**
 * Created by psend on 13.04.2016.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

public class SeriesDB extends SQLiteOpenHelper{

    public SeriesDB(Context context) {super(context, "series.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table TvSeries(" +
                        "id integer primary key autoincrement," +
                        "name text," +
                        "year text," +
                        "img text);" +
                        "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public void addSeries(TvSeries tvSeries){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", tvSeries.getName());
        values.put("year",tvSeries.getYear());
        values.put("img", tvSeries.getImg());
        db.insertOrThrow("TvSeries",null, values);
    }

    public void delSeries(int id){
        SQLiteDatabase db = getWritableDatabase();
        String[] arguments={""+id};
        db.delete("TvSeries", "id=?", arguments);
    }
    public void delSeries(TvSeries tvSeries){
        SQLiteDatabase db = getWritableDatabase();
        String[] arguments={tvSeries.getName(),tvSeries.getYear()};
        db.delete("TvSeries", "name=? and year =?", arguments);

    }

    public void updateSeries(TvSeries tvSeries){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues wartosci = new ContentValues();
        wartosci.put("name", tvSeries.getName());
        wartosci.put("year", tvSeries.getYear());
        wartosci.put("img", tvSeries.getImg());
        String args[]={tvSeries.getId()+""};
        db.update("TvSeries", wartosci,"id=?",args);
    }

    public List<TvSeries> getAllSeries(){
        List<TvSeries> series = new LinkedList<TvSeries>();
        String[] columns={"id","name","year","img"};
        SQLiteDatabase db = getReadableDatabase();
        Cursor kursor =db.query("TvSeries",columns,null,null,null,null,null);
        while(kursor.moveToNext()){
            TvSeries tvSeries = new TvSeries();
            tvSeries.setId(kursor.getInt(0));
            tvSeries.setName(kursor.getString(1));
            tvSeries.setYear(kursor.getString(2));
            tvSeries.setImg(kursor.getString(3));
            series.add(tvSeries);
        }
        return series;
    }

    public TvSeries getTvSeries(int id){
        TvSeries series=new TvSeries();
        SQLiteDatabase db = getReadableDatabase();
        String[] columns={"id","name","year","img"};
        String args[]={id+""};
        Cursor kursor=db.query("TvSeries",columns," id=?",args,null,null,null,null);
        if(kursor!=null){
            kursor.moveToFirst();
            series.setId(kursor.getInt(0));
            series.setName(kursor.getString(1));
            series.setYear(kursor.getString(2));
            series.setImg(kursor.getString(3));
        }
        return series;
    }

    public TvSeries getByNameAndYear(String nme, String yr){
        TvSeries series=new TvSeries();
        SQLiteDatabase db = getReadableDatabase();
        String[] columns={"id","name","year","img"};
        String args[]={nme,yr};
        Cursor kursor=db.query("TvSeries",columns," name=? and year=?",args,null,null,null,null);
        if(kursor!=null){
            kursor.moveToFirst();
            series.setId(kursor.getInt(0));
            series.setName(kursor.getString(1));
            series.setYear(kursor.getString(2));
            series.setImg(kursor.getString(3));
        }
        return series;
    }

    public List<TvSeries> getByYear(String year){
        List<TvSeries> seriesy = new LinkedList<TvSeries>();
        String[] columns={"id","name","year","img"};
        SQLiteDatabase db = getReadableDatabase();
        Cursor kursor =db.rawQuery("select id,name,year,img from TvSeries where year='"
                +year+
                "' order by name asc", null);

/*safer rawQuery
		 *
		 * Cursor kursor =db.rawQuery
		 * ("select id,name,year,img from TvSeries where year=?	order by name asc", nazwi);
		 * */

        while(kursor.moveToNext()){
            TvSeries series = new TvSeries();
            series.setId(kursor.getInt(0));
            series.setName(kursor.getString(1));
            series.setYear(kursor.getString(2));
            series.setImg(kursor.getString(3));
            seriesy.add(series);
        }
        return seriesy;
    }

}

