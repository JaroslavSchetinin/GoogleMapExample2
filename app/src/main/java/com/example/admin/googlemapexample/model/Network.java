package com.example.admin.googlemapexample.model;

import android.location.Location;

public class Network
{
    private String id;

    private Stations[] stations;

    private Location location;

    private String name;

    private String[] company;

    private String href;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public Stations[] getStations ()
    {
        return stations;
    }

    public void setStations (Stations[] stations)
    {
        this.stations = stations;
    }

    public Location getLocation ()
    {
        return location;
    }

    public void setLocation (Location location)
    {
        this.location = location;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String[] getCompany ()
    {
        return company;
    }

    public void setCompany (String[] company)
    {
        this.company = company;
    }

    public String getHref ()
    {
        return href;
    }

    public void setHref (String href)
    {
        this.href = href;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", stations = "+stations+", location = "+location+", name = "+name+", company = "+company+", href = "+href+"]";
    }
}
