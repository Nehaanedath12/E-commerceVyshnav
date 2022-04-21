package com.sangsolutions.e_commerce.Adapter.NotificationListAdapter;

public class NotificationList {
    String iId,iOrderId,sMessage,sRefNo;

    public NotificationList(String iId, String iOrderId, String sMessage, String sRefNo) {
        this.iId = iId;
        this.iOrderId = iOrderId;
        this.sMessage = sMessage;
        this.sRefNo = sRefNo;
    }

    public String getiId() {
        return iId;
    }

    public void setiId(String iId) {
        this.iId = iId;
    }

    public String getiOrderId() {
        return iOrderId;
    }

    public void setiOrderId(String iOrderId) {
        this.iOrderId = iOrderId;
    }

    public String getsMessage() {
        return sMessage;
    }

    public void setsMessage(String sMessage) {
        this.sMessage = sMessage;
    }

    public String getsRefNo() {
        return sRefNo;
    }

    public void setsRefNo(String sRefNo) {
        this.sRefNo = sRefNo;
    }
}
