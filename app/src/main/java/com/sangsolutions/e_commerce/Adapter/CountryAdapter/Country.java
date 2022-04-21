package com.sangsolutions.e_commerce.Adapter.CountryAdapter;

public class Country {
    String iId,sCode,sName;

    public Country(String iId, String sCode, String sName) {
        this.iId = iId;
        this.sCode = sCode;
        this.sName = sName;
    }

    public String getiId() {
        return iId;
    }

    public void setiId(String iId) {
        this.iId = iId;
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
