
package com.crtb.tunnelmonitor.entity;

public class MergedAlert {

    private AlertInfo leijiAlert = null;
    private AlertInfo sulvAlert = null;

    public AlertInfo getLeijiAlert() {
        return leijiAlert;
    }

    public void setLeijiAlert(AlertInfo leijiAlert) {
        this.leijiAlert = leijiAlert;
    }

    public AlertInfo getSulvAlert() {
        return sulvAlert;
    }

    public void setSulvAlert(AlertInfo sulvAlert) {
        this.sulvAlert = sulvAlert;
    }

}
