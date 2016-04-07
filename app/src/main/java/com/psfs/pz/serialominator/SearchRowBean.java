package com.psfs.pz.serialominator;

/**
 * Created by psend on 01.04.2016.
 */
public class SearchRowBean
{
    public String imgUrl;
    public String Title;
    public String Year;

    public SearchRowBean(){}
    public SearchRowBean(String url, String title, String year)
    {
        imgUrl = url;
        Title = title;
        Year = year;
    }
}
