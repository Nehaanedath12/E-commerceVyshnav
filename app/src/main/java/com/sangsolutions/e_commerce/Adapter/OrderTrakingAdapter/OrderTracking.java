package com.sangsolutions.e_commerce.Adapter.OrderTrakingAdapter;

public class OrderTracking {
   private String ContactPerson,DeliveryStatus,OrderDate,Product,fQty,fRate,sAddress1,sAddress2,sAltLongDescription,sAltName,sAltShortDescription,sLongDescription,sMobNo,sRefNo,sShortDescription,sImagePath;

    public OrderTracking(String contactPerson, String deliveryStatus, String orderDate, String product,String fQty,String fRate, String sAddress1, String sAddress2, String sAltLongDescription, String sAltName, String sAltShortDescription, String sLongDescription, String sMobNo, String sRefNo, String sShortDescription,String sImagePath) {
        this.ContactPerson = contactPerson;
        this.DeliveryStatus = deliveryStatus;
        this.OrderDate = orderDate;
        this.Product = product;
        this.fQty = fQty;
        this.fRate = fRate;
        this.sAddress1 = sAddress1;
        this.sAddress2 = sAddress2;
        this.sAltLongDescription = sAltLongDescription;
        this.sAltName = sAltName;
        this.sAltShortDescription = sAltShortDescription;
        this.sLongDescription = sLongDescription;
        this.sMobNo = sMobNo;
        this.sRefNo = sRefNo;
        this.sShortDescription = sShortDescription;
        this.sImagePath = sImagePath;
    }

    public String getContactPerson() {
        return ContactPerson;
    }

    public void setContactPerson(String contactPerson) {
        ContactPerson = contactPerson;
    }

    public String getDeliveryStatus() {
        return DeliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        DeliveryStatus = deliveryStatus;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getProduct() {
        return Product;
    }

    public void setProduct(String product) {
        Product = product;
    }

    public String getfQty() {
        return fQty;
    }

    public void setfQty(String fQty) {
        this.fQty = fQty;
    }

    public String getfRate() {
        return fRate;
    }

    public void setfRate(String fRate) {
        this.fRate = fRate;
    }

    public String getsAddress1() {
        return sAddress1;
    }

    public void setsAddress1(String sAddress1) {
        this.sAddress1 = sAddress1;
    }

    public String getsAddress2() {
        return sAddress2;
    }

    public void setsAddress2(String sAddress2) {
        this.sAddress2 = sAddress2;
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

    public String getsLongDescription() {
        return sLongDescription;
    }

    public void setsLongDescription(String sLongDescription) {
        this.sLongDescription = sLongDescription;
    }

    public String getsMobNo() {
        return sMobNo;
    }

    public void setsMobNo(String sMobNo) {
        this.sMobNo = sMobNo;
    }

    public String getsRefNo() {
        return sRefNo;
    }

    public void setsRefNo(String sRefNo) {
        this.sRefNo = sRefNo;
    }

    public String getsShortDescription() {
        return sShortDescription;
    }

    public void setsShortDescription(String sShortDescription) {
        this.sShortDescription = sShortDescription;
    }

    public String getsImagePath() {
        return sImagePath;
    }

    public void setsImagePath(String sImagePath) {
        this.sImagePath = sImagePath;
    }
}
