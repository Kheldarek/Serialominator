package com.psfs.pz.serialominator;

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

    private int id;
    private String name;
    private String year;
    private String img;
}
