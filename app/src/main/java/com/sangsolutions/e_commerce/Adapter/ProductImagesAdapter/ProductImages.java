package com.sangsolutions.e_commerce.Adapter.ProductImagesAdapter;

public class ProductImages {
    private String IId,iImageId,sImagePath;

    public ProductImages(String IId, String iImageId, String sImagePath) {
        this.IId = IId;
        this.iImageId = iImageId;
        this.sImagePath = sImagePath;
    }

    public String getIId() {
        return IId;
    }

    public void setIId(String IId) {
        this.IId = IId;
    }

    public String getiImageId() {
        return iImageId;
    }

    public void setiImageId(String iImageId) {
        this.iImageId = iImageId;
    }

    public String getsImagePath() {
        return sImagePath;
    }

    public void setsImagePath(String sImagePath) {
        this.sImagePath = sImagePath;
    }
}
