package com.sangsolutions.e_commerce.Adapter.ListCategoryAdapter;

public class ListCategory {
    private String iId, sAltName, sCode, sName;

    public ListCategory(String iId, String sAltName, String sCode, String sName) {
        this.iId = iId;
        this.sAltName = sAltName;
        this.sCode = sCode;
        this.sName = sName;
    }


    public String getiId() {
        return iId;
    }

    public void setiId(String iId) {
        this.iId = iId;
    }

    public String getsAltName() {
        return sAltName;
    }

    public void setsAltName(String sAltName) {
        this.sAltName = sAltName;
    }

    public String getsCode() {
        return sCode;
    }

    public void setsCode(String sCode) {
        this.sCode = sCode;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }
}
