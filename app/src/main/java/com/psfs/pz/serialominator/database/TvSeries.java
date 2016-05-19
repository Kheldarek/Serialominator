package com.psfs.pz.serialominator.database;

/**
 * Created by psend on 13.04.2016.
 */
public class TvSeries
{
    public String getImg()
    {
        return img;
    }

    public void setImg(String img)
    {
        this.img = img;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getYear()
    {
        return year;
    }

    public void setYear(String year)
    {
        this.year = year;
    }


    public String getIMDBid()
    {
        return IMDBid;
    }

    public void setIMDBid(String IMDBid)
    {
        this.IMDBid = IMDBid;
    }

    public String getPlot()
    {
        return plot;
    }

    public void setPlot(String plot)
    {
        this.plot = plot;
    }

    public String getRating()
    {
        return rating;
    }

    public void setRating(String rating)
    {
        this.rating = rating;
    }

    public String getGenre()
    {
        return genre;
    }

    public void setGenre(String genre)
    {
        this.genre = genre;
    }


    public String getRuntime()
    {
        return runtime;
    }

    public void setRuntime(String runtime)
    {
        this.runtime = runtime;
    }

    private int id;
    private String name;
    private String year;
    private String img;
    private String IMDBid;
    private String plot;
    private String rating;
    private String genre;
    private String runtime;
}
