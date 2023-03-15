package com.example.localshop.modelClass;

public class Shops {
    private String shop_email ,shop_name,owner_name,shop_phone,
            shop_password,street,postcode,district,image,category;

    private double latitude,longtitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public Shops()
    {

    }

    public Shops(double latitude, double longtitude) {
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public String getShop_email() {
        return shop_email;
    }

    public void setShop_email(String shop_email) {
        this.shop_email = shop_email;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getShop_phone() {
        return shop_phone;
    }

    public void setShop_phone(String shop_phone) {
        this.shop_phone = shop_phone;
    }

    public String getShop_password() {
        return shop_password;
    }

    public void setShop_password(String shop_password) {
        this.shop_password = shop_password;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Shops(String shop_email, String shop_name, String owner_name, String shop_phone,
                 String shop_password, String street, String postcode, String district, String image, String category) {
        this.shop_email = shop_email;
        this.shop_name = shop_name;
        this.owner_name = owner_name;
        this.shop_phone = shop_phone;
        this.shop_password = shop_password;
        this.street = street;
        this.postcode = postcode;
        this.district = district;
        this.image = image;
        this.category = category;
    }
}
