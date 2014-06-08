package com.crtb.tunnelmonitor.task;

import java.util.Date;

/**
 * 抽象测量数据
 * 
 * @author tim
 *
 */
public abstract class MeasureData {
	 /**
	  * 获取测量时间 
	  * 
	  * @return
	  */
     public abstract Date getMeasureDate();

     /**
      * 获取测量点的编码序列
      * @param sectionCode
      * @return
      */
     public abstract String getPointCodeList(String sectionCode);
     /**
      * 获取测量点坐标序列
      * @return
      */
     public abstract String getCoordinateList();
     
     /**
      * 获取测量点测量值序列
      * @return
      */
     public abstract String getValueList();

     /**
      * 将测量数据标识为已上传
      */
     public abstract void markAsUploaded();
}
