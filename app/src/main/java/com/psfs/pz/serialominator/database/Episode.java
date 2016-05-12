package com.psfs.pz.serialominator.database;

/**
 * Created by psend on 11.05.2016.
 */
public class Episode
{
    private int id;
    private String title;
    private int watched;
    private int season;
    private int seriesID;
    private long released;
    private int episodeNumber;
    private String  episodeID;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }


    public int getSeriesID()
    {
        return seriesID;
    }

    public void setSeriesID(int seriesID)
    {
        this.seriesID = seriesID;
    }



    public int getEpisodeNumber()
    {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber)
    {
        this.episodeNumber = episodeNumber;
    }

;

    public String getEpisodeID()
    {
        return episodeID;
    }

    public void setEpisodeID(String episodeID)
    {
        this.episodeID = episodeID;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getWatched()
    {
        return watched;
    }

    public void setWatched(int watched)
    {
        this.watched = watched;
    }

    public int getSeason()
    {
        return season;
    }

    public void setSeason(int season)
    {
        this.season = season;
    }

    public long getReleased()
    {
        return released;
    }

    public void setReleased(long released)
    {
        this.released = released;
    }






}
