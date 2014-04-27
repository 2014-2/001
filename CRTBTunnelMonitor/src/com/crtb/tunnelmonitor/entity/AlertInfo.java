
package com.crtb.tunnelmonitor.entity;

public class AlertInfo {
    public static int count = 0;
    public static int yixiao = 0;
//    public boolean state1;
    public int alertId = -1;
    public int alertHandlingId = -1;
    public int sheetId = -1;
    public int sectionId = -1;
    public String xinghao;
    public String date;
    public String pntType;
    public String uTypeMsg;
    public int uType;
    public String alertStatusMsg;
    public int alertStatus;
    public String edtState;
    public String chuliFangshi;

//    public boolean isState1() {
//        return state1;
//    }
//
//    public void setState1(boolean state1) {
//        this.state1 = state1;
//    }

    public int getAlertId() {
        return alertId;
    }

    public void setAlertId(int alertId) {
        this.alertId = alertId;
    }

    public int getAlertHandlingId() {
        return alertHandlingId;
    }

    public void setAlertHandlingId(int alertHandlingId) {
        this.alertHandlingId = alertHandlingId;
    }

    public void setSheetId(int sheetId) {
    	this.sheetId = sheetId;
    }
    
    public int getSheetId() {
    	return sheetId;
    }
    
    public void setSectionId(int sectionId) {
    	this.sectionId = sectionId;
    }
    
    public int getSectionId() {
    	return sectionId;
    }
    
    public String getChuliFangshi() {
        return chuliFangshi;
    }

    public void setChuliFangshi(String chuliFangshi) {
        this.chuliFangshi = chuliFangshi;
    }

    public String getXinghao() {
        return xinghao;
    }

    public void setXinghao(String xinghao) {
        this.xinghao = xinghao;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPntType() {
        return pntType;
    }

    public void setPntType(String pntType) {
        this.pntType = pntType;
    }

    public String getUTypeMsg() {
        return uTypeMsg;
    }

    public int getUType() {
        return uType;
    }

    public void setUType(int uType) {
        this.uType = uType;
    }

    public int getAlertStatus() {
        return alertStatus;
    }

    public void setAlertStatus(int alertStatus) {
        this.alertStatus = alertStatus;
    }

    public void setUTypeMsg(String uTypeMsg) {
        this.uTypeMsg = uTypeMsg;
    }

    public String getAlertStatusMsg() {
        return alertStatusMsg;
    }

    public void setAlertStatusMsg(String alertStatusMsg) {
        this.alertStatusMsg = alertStatusMsg;
    }

    public String getEdtState() {
        return edtState;
    }

    public void setEdtState(String edtState) {
        this.edtState = edtState;
    }
}
