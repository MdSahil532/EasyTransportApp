package com.sahil.easytransportapp;

import com.parse.ParseGeoPoint;

public class Requester {

    private String name;
    private String pickAddress;
    private String deliVerAddress;
    private String imageUrl;
    private String pickDistance;
    private String deliveryDistance;
    private String requesterNo ;
    private String pickUpNo;
    private String deliveryNo;
    private ParseGeoPoint location;
    private String dLat;
    private String dLng;
    private String weight;



    public  Requester(){

    }

    public Requester(String name, String pickAddress, String deliVerAddress, String imageUrl,String pickDistance,String deliveryDistance,String requesterNo
    ,String pickUpNo,String deliveryNo, ParseGeoPoint location,String dLat,String dLng,String weight){
        this.name = name;
        this.pickAddress = pickAddress;
        this.deliVerAddress = deliVerAddress;
        this.imageUrl = imageUrl;
        this.pickDistance = pickDistance;
        this.deliveryDistance = deliveryDistance;
        this.requesterNo = requesterNo;
        this.pickUpNo = pickUpNo;
        this.deliveryNo = deliveryNo;
        this.location = location;
        this.dLat = dLat;
        this.dLng = dLng;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPickAddress() {
        return pickAddress;
    }

    public void setPickAddress(String pickAddress) {
        this.pickAddress = pickAddress;
    }

    public String getDeliVerAddress() {
        return deliVerAddress;
    }

    public void setDeliVerAddress(String deliVerAddress) {
        this.deliVerAddress = deliVerAddress;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPickDistance() {
        return pickDistance;
    }

    public void setPickDistance(String pickDistance) {
        this.pickDistance = pickDistance;
    }

    public String getDeliveryDistance() {
        return deliveryDistance;
    }

    public void setDeliveryDistance(String deliveryDistance) {
        this.deliveryDistance = deliveryDistance;
    }

    public String getRequesterNo() {
        return requesterNo;
    }

    public void setRequesterNo(String requesterNo) {
        this.requesterNo = requesterNo;
    }

    public String getPickUpNo() {
        return pickUpNo;
    }

    public void setPickUpNo(String pickUpNo) {
        this.pickUpNo = pickUpNo;
    }

    public String getDeliveryNo() {
        return deliveryNo;
    }

    public void setDeliveryNo(String deliveryNo) {
        this.deliveryNo = deliveryNo;
    }

    public ParseGeoPoint getLocation() {
        return location;
    }

    public void setLocation(ParseGeoPoint location) {
        this.location = location;
    }

    public String getdLat() {
        return dLat;
    }

    public void setdLat(String dLat) {
        this.dLat = dLat;
    }

    public String getdLng() {
        return dLng;
    }

    public void setdLng(String dLng) {
        this.dLng = dLng;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
