
package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;

import com.crtb.tunnelmonitor.utils.AlertUtils;

import android.text.TextUtils;

public class AlertInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    public static int count = 0;
    public static int yixiao = 0;
    //    public boolean state1;
    private int alertId = -1;
    private int alertHandlingId = -1;
    private String sheetId = null;
    private String sectionId = null;
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
    private String duePerson;
    private int uType;
    private String alertStatusMsg;
    private int alertStatus = 1;
    private String chuliFangshi;
    private int uploadStatus;
    private String alertInfo;
    private String SECTCODE;
    private String rockGrade;

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

    public void setSheetId(String sheetId) {
        this.sheetId = sheetId;
    }

    public String getSheetId() {
        return sheetId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionId() {
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
        //TODO: Workaround for the "null" string problem
        if (TextUtils.isEmpty(alertStatusMsg) || alertStatusMsg.equalsIgnoreCase("null")) {
            if (alertStatus >= AlertUtils.ALERT_STATUS_HANDLED
                    && alertStatus <= AlertUtils.ALERT_STATUS_HANDLING) {
                alertStatusMsg = AlertUtils.ALERT_STATUS_MSGS[alertStatus];
            }
        }
        return alertStatusMsg;
    }

    public void setAlertStatusMsg(String alertStatusMsg) {
        this.alertStatusMsg = alertStatusMsg;
    }

    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getAlertInfo() {
        return alertInfo;
    }

    public void setAlertInfo(String alertInfo) {
        this.alertInfo = alertInfo;
    }

    public String getSECTCODE() {
        return SECTCODE;
    }

    public void setSECTCODE(String sECTCODE) {
        SECTCODE = sECTCODE;
    }

    public String getDuePerson() {
        return duePerson;
    }

    public void setDuePerson(String duePerson) {
        this.duePerson = duePerson;
    }

    public String getRockGrade(){
    	return rockGrade;
    }
    
    public void SetRockGrade(String rockGrade){
    	this.rockGrade = rockGrade;
    }
}
