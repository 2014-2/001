
package com.crtb.tunnelmonitor.entity;

import java.util.Date;

import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.utils.AlertUtils;

public class MergedAlert {

    private AlertInfo leijiAlert = null;
    private AlertInfo sulvAlert = null;
    private String sheetId = null;
    private Date sheetDate = null;

    public AlertInfo getLeijiAlert() {
        return leijiAlert;
    }

    public void setLeijiAlert(AlertInfo leijiAlert) {
        this.leijiAlert = leijiAlert;
        if (sheetId == null && leijiAlert != null) {
            sheetId = leijiAlert.getSheetId();
            if (sheetId != null && sheetDate == null) {
                RawSheetIndex sheet = RawSheetIndexDao.defaultDao().queryOneByGuid(sheetId);
                if (sheet != null) {
                    sheetDate = sheet.getCreateTime();
                }
            }
        }
    }

    public AlertInfo getSulvAlert() {
        return sulvAlert;
    }

    public void setSulvAlert(AlertInfo sulvAlert) {
        this.sulvAlert = sulvAlert;
        if (sheetId == null && sulvAlert != null) {
            sheetId = sulvAlert.getSheetId();
            if (sheetId != null && sheetDate == null) {
                RawSheetIndex sheet = RawSheetIndexDao.defaultDao().queryOneByGuid(sheetId);
                if (sheet != null) {
                    sheetDate = sheet.getCreateTime();
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

    //是否已上传
    public boolean isUploaded() {
        if (leijiAlert != null && leijiAlert.getUploadStatus() != 2) {
            return false;
        }

        if (sulvAlert != null && sulvAlert.getUploadStatus() != 2) {
            return false;
        }

        return true;
    }

    //是否已销警
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
