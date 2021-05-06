package com.sahil.easytransportapp;

import com.parse.ParseGeoPoint;

public class Driver {

    private String driverName;
    private String driverPhoneNo;
    private String posterUrl;
    private String ownerName;
    private String ownerPhoneNo;
    private String vehicleName;
    private String vehicleNo;
    private String vehicleType;
    private ParseGeoPoint driverLocation;
    private String distance;

    public Driver() {
    }

    public Driver(String driverName, String driverPhoneNo, String posterUrl, String ownerName, String ownerPhoneNo, String vehicleName,
                  String vehicleNo, String vehicleType, ParseGeoPoint driverLocation, String distance) {
        this.driverName = driverName;
        this.driverPhoneNo = driverPhoneNo;
        this.posterUrl = posterUrl;
        this.ownerName = ownerName;
        this.ownerPhoneNo = ownerPhoneNo;
        this.vehicleName = vehicleName;
        this.vehicleNo = vehicleNo;
        this.vehicleType = vehicleType;
        this.driverLocation = driverLocation;
        this.distance = distance;
    }


    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhoneNo() {
        return driverPhoneNo;
    }

    public void setDriverPhoneNo(String driverPhoneNo) {
        this.driverPhoneNo = driverPhoneNo;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhoneNo() {
        return ownerPhoneNo;
    }

    public void setOwnerPhoneNo(String ownerPhoneNo) {
        this.ownerPhoneNo = ownerPhoneNo;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public ParseGeoPoint getDriverLocation() {
        return driverLocation;
    }

    public void setDriverLocation(ParseGeoPoint driverLocation) {
        this.driverLocation = driverLocation;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
