package com.example.localshop.modelClass;

public class Products  {

    private String product,price,category,date,description,discount,no_of_Product,
            image,name,noofitem,pid,time,selling,quantity,producttype,shopname,kgmglml;
    private int sold;

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public String getPrice() {
        return price;
    }

    public Products(int sold) {
        this.sold = sold;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNoofitem() {
        return noofitem;
    }

    public void setNoofitem(String noofitem) {
        this.noofitem = noofitem;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSelling() {
        return selling;
    }

    public void setSelling(String selling) {
        this.selling = selling;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getProducttype() {
        return producttype;
    }

    public void setProducttype(String producttype) {
        this.producttype = producttype;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getKgmglml() {
        return kgmglml;
    }

    public void setKgmglml(String kgmglml) {
        this.kgmglml = kgmglml;
    }

    public Products(String product, String price, String category, String date, String description, String discount, String image, String name, String noofitem, String pid, String time, String selling, String quantity, String producttype, String shopname, String kgmglml) {
        this.product = product;
        this.price = price;
        this.category = category;
        this.date = date;
        this.description = description;
        this.discount = discount;
        this.image = image;
        this.name = name;
        this.noofitem = noofitem;
        this.pid = pid;
        this.time = time;
        this.selling = selling;
        this.quantity = quantity;
        this.producttype = producttype;
        this.shopname = shopname;
        this.kgmglml = kgmglml;
    }

    public Products(){

    }

}
