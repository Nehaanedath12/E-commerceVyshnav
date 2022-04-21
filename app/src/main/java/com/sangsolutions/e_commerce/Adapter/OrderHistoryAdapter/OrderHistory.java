package com.sangsolutions.e_commerce.Adapter.OrderHistoryAdapter;

public class OrderHistory {
    private String sRefNo,ContactPerson,sMobNo,sAddress1,sAddress2,OrderDate,DeliveryStatus;

    public OrderHistory(String sRefNo, String contactPerson, String sMobNo, String sAddress1, String sAddress2, String orderDate, String deliveryStatus) {
        this.sRefNo = sRefNo;
        this.ContactPerson = contactPerson;
        this.sMobNo = sMobNo;
        this.sAddress1 = sAddress1;
        this.sAddress2 = sAddress2;
        this.OrderDate = orderDate;
        this.DeliveryStatus = deliveryStatus;
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

    public String getDeliveryStatus() {
        return DeliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        DeliveryStatus = deliveryStatus;
    }
}
