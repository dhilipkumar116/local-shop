package com.example.localshop.modelClass;

public class Orders {

    private String ID , Totprice , address,date,ordername,phone_number
            ,shopname,status,time,userId,payment,delivery_fee,km,deliveryID;
    private Double latitude,longtitude;

    public Orders(String deliveryID) {
        this.deliveryID = deliveryID;
    }

    public String getDeliveryID() {
        return deliveryID;
    }

    public void setDeliveryID(String deliveryID) {
        this.deliveryID = deliveryID;
    }

    public Orders(String delivery_fee, String km) {
        this.delivery_fee = delivery_fee;
        this.km = km;
    }

    public String getDelivery_fee() {
        return delivery_fee;
    }

    public void setDelivery_fee(String delivery_fee) {
        this.delivery_fee = delivery_fee;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public Orders(){

    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public Orders(String ID, String totprice, String address, String date, String ordername, String phone_number, String shopname, String status, String time, String userId, String payment, Double latitude, Double longtitude) {
        this.ID = ID;
        Totprice = totprice;
        this.address = address;
        this.date = date;
        this.ordername = ordername;
        this.phone_number = phone_number;
        this.shopname = shopname;
        this.status = status;
        this.time = time;
        this.userId = userId;
        this.payment = payment;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTotprice() {
        return Totprice;
    }

    public void setTotprice(String totprice) {
        Totprice = totprice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrdername() {
        return ordername;
    }

    public void setOrdername(String ordername) {
        this.ordername = ordername;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }


}
