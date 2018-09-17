package com.example.admin.googlemapexample.model;

public class BikeResponse
{
    private Network network;

    public Network getNetwork ()
    {
        return network;
    }

    public void setNetwork (Network network)
    {
        this.network = network;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [network = "+network+"]";
    }
}

