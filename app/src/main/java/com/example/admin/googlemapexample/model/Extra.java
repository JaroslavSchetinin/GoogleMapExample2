package com.example.admin.googlemapexample.model;

public class Extra
{
    private String uid;

    private String zip;

    private String districtCode;

    private String status;

    private String address;

    private String[] NearbyStationList;

    public String getUid ()
    {
        return uid;
    }

    public void setUid (String uid)
    {
        this.uid = uid;
    }

    public String getZip ()
    {
        return zip;
    }

    public void setZip (String zip)
    {
        this.zip = zip;
    }

    public String getDistrictCode ()
    {
        return districtCode;
    }

    public void setDistrictCode (String districtCode)
    {
        this.districtCode = districtCode;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getAddress ()
    {
        return address;
    }

    public void setAddress (String address)
    {
        this.address = address;
    }

    public String[] getNearbyStationList ()
    {
        return NearbyStationList;
    }

    public void setNearbyStationList (String[] NearbyStationList)
    {
        this.NearbyStationList = NearbyStationList;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [uid = "+uid+", zip = "+zip+", districtCode = "+districtCode+", status = "+status+", address = "+address+", NearbyStationList = "+NearbyStationList+"]";
    }
}