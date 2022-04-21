package com.sangsolutions.e_commerce.Adapter;

public class Banner {
    private String iId, sImageLink, sName;

    public Banner(String iId, String sImageLink, String sName) {
        this.iId = iId;
        this.sImageLink = sImageLink;
        this.sName = sName;
    }


    public String getiId() {
        return iId;
    }

    public void setiId(String iId) {
        this.iId = iId;
    }

    public String getsImageLink() {
        return sImageLink;
    }

    public void setsImageLink(String sImageLink) {
        this.sImageLink = sImageLink;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }
}
