package com.sangsolutions.e_commerce.Adapter.SearchProductAdapter;

public class SearchProduct {
   private String iId, sName, sCode, sAltName, sImagePath, iSubCategory, fPrice, sShortDescription, sLongDescription, sAltShortDescription, sAltLongDescription;

    public SearchProduct(String iId, String sName, String sCode, String sAltName, String sImagePath, String iSubCategory, String fPrice, String sShortDescription, String sLongDescription, String sAltShortDescription, String sAltLongDescription) {
        this.iId = iId;
        this.sName = sName;
        this.sCode = sCode;
        this.sAltName = sAltName;
        this.sImagePath = sImagePath;
        this.iSubCategory = iSubCategory;
        this.fPrice = fPrice;
        this.sShortDescription = sShortDescription;
        this.sLongDescription = sLongDescription;
        this.sAltShortDescription = sAltShortDescription;
        this.sAltLongDescription = sAltLongDescription;
    }


    public String getiId() {
        return iId;
    }

    public void setiId(String iId) {
        this.iId = iId;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsCode() {
        return sCode;
    }

    public void setsCode(String sCode) {
        this.sCode = sCode;
    }

    public String getsAltName() {
        return sAltName;
    }

    public void setsAltName(String sAltName) {
        this.sAltName = sAltName;
    }

    public String getsImagePath() {
        return sImagePath;
    }

    public void setsImagePath(String sImagePath) {
        this.sImagePath = sImagePath;
    }

    public String getiSubCategory() {
        return iSubCategory;
    }

    public void setiSubCategory(String iSubCategory) {
        this.iSubCategory = iSubCategory;
    }

    public String getfPrice() {
        return fPrice;
    }

    public void setfPrice(String fPrice) {
        this.fPrice = fPrice;
    }

    public String getsShortDescription() {
        return sShortDescription;
    }

    public void setsShortDescription(String sShortDescription) {
        this.sShortDescription = sShortDescription;
    }

    public String getsLongDescription() {
        return sLongDescription;
    }

    public void setsLongDescription(String sLongDescription) {
        this.sLongDescription = sLongDescription;
    }

    public String getsAltShortDescription() {
        return sAltShortDescription;
    }

    public void setsAltShortDescription(String sAltShortDescription) {
        this.sAltShortDescription = sAltShortDescription;
    }

    public String getsAltLongDescription() {
        return sAltLongDescription;
    }

    public void setsAltLongDescription(String sAltLongDescription) {
        this.sAltLongDescription = sAltLongDescription;
    }
}
