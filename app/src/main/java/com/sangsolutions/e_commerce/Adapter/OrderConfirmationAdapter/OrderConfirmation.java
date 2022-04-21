package com.sangsolutions.e_commerce.Adapter.OrderConfirmationAdapter;

public class OrderConfirmation {

    String fPrice,beforePrice,beforeQty, iId, sAltLongDescription, sAltName, sAltShortDescription, sImagePath, sLongDescription, sName,
            sShortDescription, Qty;

    public OrderConfirmation(String fPrice,String beforePrice,String beforeQty,  String iId, String sAltLongDescription, String sAltName, String sAltShortDescription, String sImagePath, String sLongDescription, String sName, String sShortDescription, String Qty) {
        this.fPrice = fPrice;
        this.beforePrice =beforePrice;
        this.beforeQty =beforeQty;
        this.iId = iId;
        this.sAltLongDescription = sAltLongDescription;
        this.sAltName = sAltName;
        this.sAltShortDescription = sAltShortDescription;
        this.sImagePath = sImagePath;
        this.sLongDescription = sLongDescription;
        this.sName = sName;
        this.sShortDescription = sShortDescription;
        this.Qty = Qty;
    }

    public String getfPrice() {
        return fPrice;
    }

    public String getBeforePrice() {
        return beforePrice;
    }

    public void setBeforePrice(String beforePrice) {
        this.beforePrice = beforePrice;
    }

    public String getBeforeQty() {
        return beforeQty;
    }

    public void setBeforeQty(String beforeQty) {
        this.beforeQty = beforeQty;
    }

    public void setfPrice(String fPrice) {
        this.fPrice = fPrice;
    }

    public String getiId() {
        return iId;
    }

    public void setiId(String iId) {
        this.iId = iId;
    }

    public String getsAltLongDescription() {
        return sAltLongDescription;
    }

    public void setsAltLongDescription(String sAltLongDescription) {
        this.sAltLongDescription = sAltLongDescription;
    }

    public String getsAltName() {
        return sAltName;
    }

    public void setsAltName(String sAltName) {
        this.sAltName = sAltName;
    }

    public String getsAltShortDescription() {
        return sAltShortDescription;
    }

    public void setsAltShortDescription(String sAltShortDescription) {
        this.sAltShortDescription = sAltShortDescription;
    }


    public String getsImagePath() {
        return sImagePath;
    }

    public void setsImagePath(String sImagePath) {
        this.sImagePath = sImagePath;
    }

    public String getsLongDescription() {
        return sLongDescription;
    }

    public void setsLongDescription(String sLongDescription) {
        this.sLongDescription = sLongDescription;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsShortDescription() {
        return sShortDescription;
    }

    public void setsShortDescription(String sShortDescription) {
        this.sShortDescription = sShortDescription;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }
}
