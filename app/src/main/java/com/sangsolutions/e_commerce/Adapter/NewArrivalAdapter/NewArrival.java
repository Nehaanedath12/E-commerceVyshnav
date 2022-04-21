package com.sangsolutions.e_commerce.Adapter.NewArrivalAdapter;

public class NewArrival {
   private String fPrice, iId, iSubCategory, sAltLongDescription, sAltName, sAltShortDescription, sCode, sImagePath, sLongDescription, sName,
            sShortDescription;


    public NewArrival(String fPrice, String iId, String iSubCategory, String sAltLongDescription, String sAltName, String sAltShortDescription, String sCode, String sImagePath, String sLongDescription, String sName, String sShortDescription) {
        this.fPrice = fPrice;
        this.iId = iId;
        this.iSubCategory = iSubCategory;
        this.sAltLongDescription = sAltLongDescription;
        this.sAltName = sAltName;
        this.sAltShortDescription = sAltShortDescription;
        this.sCode = sCode;
        this.sImagePath = sImagePath;
        this.sLongDescription = sLongDescription;
        this.sName = sName;
        this.sShortDescription = sShortDescription;
    }

    public String getfPrice() {
        return fPrice;
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

    public String getiSubCategory() {
        return iSubCategory;
    }

    public void setiSubCategory(String iSubCategory) {
        this.iSubCategory = iSubCategory;
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

    public String getsCode() {
        return sCode;
    }

    public void setsCode(String sCode) {
        this.sCode = sCode;
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
}
