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

import com.crtb.tunnelmonitor.utils.CrtbUtils;

/**
 * 隧道内断面; 更新时间: 2014-05-27
 * 
 * @author zhouwei
 */
@Table(TableName = "TunnelCrossSectionIndex")
public class TunnelCrossSectionIndex implements Serializable {

    @ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
    @ColumnInt
    private int ID;
    
    @ColumnDouble
    private double Chainage; 			// 断面里程值
    
    @ColumnDate
    private Date InBuiltTime; 			// 埋设时间
    
    @ColumnDouble
    private double Width; 				// 断面宽度
    
    @ColumnText
	private String Guid ;				// 唯一标示
	
    @ColumnInt
    private int ExcavateMethod;  		// 施工方法

    @ColumnString(length = 255)
    private String SurveyPntName; 		// 测点编号: 测量点对应的别名: A,S1,S2,S3
    
    @ColumnInt
    private int UploadStatus ;			// 0 表示全部状态；1表示未上传，2表示已上传，3表示部分上传

    @ColumnText
    private String Info; 				// 备注
    
    @ColumnString(length = 255)
    private String ChainagePrefix; 		// 里程前缀 默认是工程前缀
    
    //@ColumnString(length=255) //NOT A COLUMN IN THE DB TABLE
    private String sectionName;         // 断面名称					---- 非数据库字段

    @ColumnFloat
    private float 	GDU0; 				// 拱顶U0值

    @ColumnFloat
    private float 	GDVelocity; 		// 拱顶本次下沉速率

    @ColumnDate
    private Date 	GDU0Time; 			// 拱顶限差修改时间

    @ColumnText
    private String 	GDU0Description; 	// 拱顶极限备注

    @ColumnFloat
    private float 	SLU0; 				// 收敛uo值

    @ColumnFloat
    private float 	SLLimitVelocity; 	// 收敛本次下沉速率

    @ColumnDate
    private Date 	SLU0Time; 			// 收敛限差修改时间

    @ColumnText
    private String 	SLU0Description; 	// 收敛极限备注

    @ColumnString(length = 255)
    private String Lithologic; 			// 岩性

    @ColumnFloat
    private float LAYVALUE; 			// 埋深值

    @ColumnString(length = 255)
    private String ROCKGRADE; 			// 围岩级别

    private boolean used;				// 是否选中----------扩展

    public TunnelCrossSectionIndex(){
    	setUploadStatus(1);// 未上传
    	setGuid(CrtbUtils.generatorGUID());
    	setInfo(getGuid());
    }
    
    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

	public String getGuid() {
		return Guid;
	}

	public int getUploadStatus() {
		return UploadStatus;
	}

	public void setUploadStatus(int uploadStatus) {
		UploadStatus = uploadStatus;
	}

	public void setGuid(String guid) {
		Guid = guid;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getSectionName() {
		return sectionName == null ? 
				CrtbUtils.formatSectionName(ChainagePrefix, Chainage) : sectionName;
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

    public double getWidth() {
		return Width;
	}

	public void setWidth(double width) {
		Width = width;
	}

	public int getExcavateMethod() {
		return ExcavateMethod;
	}

	public void setExcavateMethod(int excavateMethod) {
		ExcavateMethod = excavateMethod;
	}

	public String getSurveyPntName() {
        return SurveyPntName;
    }

    public void setSurveyPntName(String surveyPntName) {
        SurveyPntName = surveyPntName;
    }
    
    public String getInfo() {
		return Info;
	}

	public void setInfo(String info) {
		Info = info;
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

    public String getLithologic() {
		return Lithologic;
	}

	public void setLithologic(String lithologic) {
		Lithologic = lithologic;
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
