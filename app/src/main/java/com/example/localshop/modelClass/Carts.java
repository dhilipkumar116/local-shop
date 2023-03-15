package com.example.localshop.modelClass;

public class Carts {


    public String  mrp_price, no_of_product, pid, pname, quantity, selling_price, image ;
    public int sellP;

    public String getMrp_price() {
        return mrp_price;
    }

    public void setMrp_price(String mrp_price) {
        this.mrp_price = mrp_price;
    }

    public String getNo_of_product() {
        return no_of_product;
    }

    public void setNo_of_product(String no_of_product) {
        this.no_of_product = no_of_product;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(String selling_price) {
        this.selling_price = selling_price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getSellP() {
        return sellP;
    }

    public void setSellP(int sellP) {
        this.sellP = sellP;
    }

    public Carts(String mrp_price, String no_of_product, String pid, String pname,
                 String quantity, String selling_price, String image, int sellP) {
        this.mrp_price = mrp_price;
        this.no_of_product = no_of_product;
        this.pid = pid;
        this.pname = pname;
        this.quantity = quantity;
        this.selling_price = selling_price;
        this.image = image;
        this.sellP = sellP;
    }

    public Carts(){

    }
}
