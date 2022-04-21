package com.sangsolutions.e_commerce.Adapter.OrderHistroryDetailsAdapter;

public class OrderHistoryDetails {
    private String sRefNo,ContactPerson,sMobNo,sAddress1,sAddress2,OrderDate,Product,fQty,fRate,sAltName,sShortDescription,sLongDescription,sAltLongDescription,sAltShortDescription,DeliveryStatus,sImagePath;

    public OrderHistoryDetails(String sRefNo, String contactPerson, String sMobNo, String sAddress1, String sAddress2, String orderDate, String product,String fQty ,String fRate,String sAltName, String sShortDescription, String sLongDescription, String sAltLongDescription, String sAltShortDescription, String deliveryStatus,String sImagePath) {
        this.sRefNo = sRefNo;
        this.ContactPerson = contactPerson;
        this.sMobNo = sMobNo;
        this.sAddress1 = sAddress1;
        this.sAddress2 = sAddress2;
        this.OrderDate = orderDate;
        this.Product = product;
        this.fQty = fQty;
        this.fRate = fRate;
        this.sAltName = sAltName;
        this.sShortDescription = sShortDescription;
        this.sLongDescription = sLongDescription;
        this.sAltLongDescription = sAltLongDescription;
        this.sAltShortDescription = sAltShortDescription;
        this.DeliveryStatus = deliveryStatus;
        this.sImagePath = sImagePath;
    }

    public String getsRefNo() {
        return sRefNo;
    }

    public void setsRefNo(String sRefNo) {
        this.sRefNo = sRefNo;
    }

    public String getContactPerson() {
        return ContactPerson;
    }

    public void setContactPerson(String contactPerson) {
        ContactPerson = contactPerson;
    }

    public String getsMobNo() {
        return sMobNo;
    }

    public void setsMobNo(String sMobNo) {
        this.sMobNo = sMobNo;
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

    public String getsAltName() {
        return sAltName;
    }

    public void setsAltName(String sAltName) {
        this.sAltName = sAltName;
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

    public String getsAltLongDescription() {
        return sAltLongDescription;
    }

    public void setsAltLongDescription(String sAltLongDescription) {
        this.sAltLongDescription = sAltLongDescription;
    }

    public String getsAltShortDescription() {
        return sAltShortDescription;
    }

    public void setsAltShortDescription(String sAltShortDescription) {
        this.sAltShortDescription = sAltShortDescription;
    }

    public String getDeliveryStatus() {
        return DeliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        DeliveryStatus = deliveryStatus;
    }

    public String getsImagePath() {
        return sImagePath;
    }

    public void setsImagePath(String sImagePath) {
        this.sImagePath = sImagePath;
    }
}
