
package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;

public class AlertInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    public static int count = 0;
    public static int yixiao = 0;
    //    public boolean state1;
    private int alertId = -1;
    private int alertHandlingId = -1;
    private int sheetId = -1;
    private int sectionId = -1;
    private int alertLevel;
    private float correction = 0f;
    private double uvalue;
    private String originalDataID;
    private String handling;
    private String handlingTime;
    private String xinghao;
    private String date;
    private String pntType;
    private String uTypeMsg;
    private int uType;
    private String alertStatusMsg;
    private int alertStatus;
    private String chuliFangshi;

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

    public void setAlertLevel(int level) {
    	this.alertLevel = level;
    }
    
    public int getAlertLevel() {
    	return alertLevel;
    }
    
    public void setUValue(double uvalue) {
    	this.uvalue = uvalue;
    }
    
    public double getUValue() {
    	return uvalue;
    }

    public float getCorrection() {
        return correction;
    }

    public void setCorrection(float correction) {
        this.correction = correction;
    }

    public void setOriginalDataID(String id) {
    	this.originalDataID = id;
    }
    
    public String getOriginalDataID() {
    	return originalDataID;
    }
    
    public void setHandling(String handling) {
    	this.handling = handling;
    }
    
    public String getHandling() {
    	return handling;
    }
    
    public void setHandlingTime(String time) {
    	handlingTime = time;
    }
    
    public String getHandlingTime() {
    	return handlingTime;
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
}
