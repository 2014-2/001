package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;
import java.util.Date;

import org.zw.android.framework.db.ColumnDate;
import org.zw.android.framework.db.ColumnDouble;
import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * 隧道内断面
 * 
 * @author zhouwei
 */
@Table(TableName = "TunnelCrossSectionIndex")
public class TunnelCrossSectionIndex implements Serializable {

    @ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
    @ColumnInt
    private int ID;

    ///////////////////////base info////////////////////

    @ColumnString(length = 64)
    private String ChainagePrefix; 		// 里程前缀

    @ColumnDouble
    private double Chainage; 			// 断面里程值

    @ColumnString(length=255)
    private String sectionName ;		// 断面名称

    @ColumnDate
    private Date InBuiltTime; 			// 埋设时间

    @ColumnFloat
    private float Width; 				// 断面宽度

    //////////////////////开挖方式//////////////////////////

    @ColumnString(length = 255)
    private String ExcavateMethod;  	// 施工方法

    @ColumnString(length = 255)
    private String SurveyPntName; 		// 测点编号

    //////////////////////变形阀值///////////////////////////

    @ColumnText
    private String info; 				// 备注

    @ColumnFloat
    private float GDU0; 				// 拱顶U0值

    @ColumnFloat
    private float GDVelocity; 			// 拱顶本次下沉速率

    @ColumnDate
    private Date GDU0Time; 				// 拱顶限差修改时间

    @ColumnText
    private String GDU0Description; 	// 拱顶极限备注

    @ColumnFloat
    private float SLU0; 				// 收敛uo值

    @ColumnFloat
    private float SLLimitVelocity; 		// 收敛本次下沉速率

    @ColumnDate
    private Date SLU0Time; 				// 收敛限差修改时间

    @ColumnText
    private String SLU0Description; 	// 收敛极限备注

    @ColumnString(length = 255)
    private String Lithologi; 			// 岩性

    @ColumnFloat
    private float LAYVALUE; 			// 埋深值

    @ColumnString(length = 255)
    private String ROCKGRADE; 			// 围岩级别

    private boolean used;				// 是否选中----------扩展

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public double getChainage() {
        return Chainage;
    }

    public void setChainage(double chainage) {
        Chainage = chainage;
    }

    public Date getInBuiltTime() {
        return InBuiltTime;
    }

    public void setInBuiltTime(Date inBuiltTime) {
        InBuiltTime = inBuiltTime;
    }

    public float getWidth() {
        return Width;
    }

    public void setWidth(float width) {
        Width = width;
    }

    public String getExcavateMethod() {
        return ExcavateMethod;
    }

    public void setExcavateMethod(String excavateMethod) {
        ExcavateMethod = excavateMethod;
    }

    public String getSurveyPntName() {
        return SurveyPntName;
    }

    public void setSurveyPntName(String surveyPntName) {
        SurveyPntName = surveyPntName;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getChainagePrefix() {
        return ChainagePrefix;
    }

    public void setChainagePrefix(String chainagePrefix) {
        ChainagePrefix = chainagePrefix;
    }

    public float getGDU0() {
        return GDU0;
    }

    public void setGDU0(float gDU0) {
        GDU0 = gDU0;
    }

    public float getGDVelocity() {
        return GDVelocity;
    }

    public void setGDVelocity(float gDVelocity) {
        GDVelocity = gDVelocity;
    }

    public Date getGDU0Time() {
        return GDU0Time;
    }

    public void setGDU0Time(Date gDU0Time) {
        GDU0Time = gDU0Time;
    }

    public String getGDU0Description() {
        return GDU0Description;
    }

    public void setGDU0Description(String gDU0Description) {
        GDU0Description = gDU0Description;
    }

    public float getSLU0() {
        return SLU0;
    }

    public void setSLU0(float sLU0) {
        SLU0 = sLU0;
    }

    public float getSLLimitVelocity() {
        return SLLimitVelocity;
    }

    public void setSLLimitVelocity(float sLLimitVelocity) {
        SLLimitVelocity = sLLimitVelocity;
    }

    public Date getSLU0Time() {
        return SLU0Time;
    }

    public void setSLU0Time(Date sLU0Time) {
        SLU0Time = sLU0Time;
    }

    public String getSLU0Description() {
        return SLU0Description;
    }

    public void setSLU0Description(String sLU0Description) {
        SLU0Description = sLU0Description;
    }

    public String getLithologi() {
        return Lithologi;
    }

    public void setLithologi(String lithologi) {
        Lithologi = lithologi;
    }

    public float getLAYVALUE() {
        return LAYVALUE;
    }

    public void setLAYVALUE(float lAYVALUE) {
        LAYVALUE = lAYVALUE;
    }

    public String getROCKGRADE() {
        return ROCKGRADE;
    }

    public void setROCKGRADE(String rOCKGRADE) {
        ROCKGRADE = rOCKGRADE;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

}
