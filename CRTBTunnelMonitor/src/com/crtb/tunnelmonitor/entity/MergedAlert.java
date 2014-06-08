
package com.crtb.tunnelmonitor.entity;

import java.util.Date;

import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.utils.AlertUtils;

public class MergedAlert {

    private AlertInfo leijiAlert = null;
    private AlertInfo sulvAlert = null;
    private String sheetId = null;
    private Date sheetDate = null;
    private String sectionGuid = null;
    private String pntType = null;

    public AlertInfo getLeijiAlert() {
        return leijiAlert;
    }

    public void setLeijiAlert(AlertInfo leijiAlert) {
        this.leijiAlert = leijiAlert;
        if (leijiAlert != null) {
            if (sheetId == null) {
                sheetId = leijiAlert.getSheetId();
                if (sheetId != null && sheetDate == null) {
                    RawSheetIndex sheet = RawSheetIndexDao.defaultDao().queryOneByGuid(sheetId);
                    if (sheet != null) {
                        sheetDate = sheet.getCreateTime();
                    }
                }
            }

            if (sectionGuid == null) {
                sectionGuid = leijiAlert.getSectionId();
            }

            if (pntType == null) {
                pntType = leijiAlert.getPntType();
                if (pntType != null && pntType.length() > 3 && pntType.startsWith("S")
                        && pntType.contains("-")) {
                    pntType = pntType.substring(0, pntType.length() - 2);
                }
            }
        }
    }

    public AlertInfo getSulvAlert() {
        return sulvAlert;
    }

    public void setSulvAlert(AlertInfo sulvAlert) {
        this.sulvAlert = sulvAlert;
        if (sulvAlert != null) {
            if (sheetId == null) {
                sheetId = sulvAlert.getSheetId();
                if (sheetId != null && sheetDate == null) {
                    RawSheetIndex sheet = RawSheetIndexDao.defaultDao().queryOneByGuid(sheetId);
                    if (sheet != null) {
                        sheetDate = sheet.getCreateTime();
                    }
                }
            }

            if (sectionGuid == null) {
                sectionGuid = sulvAlert.getSectionId();
            }

            if (pntType == null) {
                pntType = sulvAlert.getPntType();
                if (pntType != null && pntType.length() > 3 && pntType.startsWith("S")
                        && pntType.contains("-")) {
                    pntType = pntType.substring(0, pntType.length() - 2);
                }
            }
        }
    }

    public String getSheetId() {
        return sheetId;
    }

    public Date getSheetDate() {
        return sheetDate;
    }

    public String getSectionGuid() {
        return sectionGuid;
    }

    public String getPntType() {
        return pntType;
    }

    // 是否已上传
    public boolean isUploaded() {
        if (leijiAlert != null && leijiAlert.getUploadStatus() != 2) {
            return false;
        }

        if (sulvAlert != null && sulvAlert.getUploadStatus() != 2) {
            return false;
        }

        return true;
    }

    // 是否已销警
    public boolean isHandled() {
        if (leijiAlert != null && leijiAlert.getAlertStatus() != AlertUtils.ALERT_STATUS_HANDLED) {
            return false;
        }
        if (sulvAlert != null && sulvAlert.getAlertStatus() != AlertUtils.ALERT_STATUS_HANDLED) {
            return false;
        }

        return true;
    }
}
