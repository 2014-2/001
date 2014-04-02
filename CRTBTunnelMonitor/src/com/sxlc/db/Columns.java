package com.sxlc.db;

public class Columns {
	static String[] ProjectSettingIndex = { "Id", "ProjectName", "ProjectID",
			"YMDFormat", "HMSFormat", "ChainagePrefix", "MaxDeformation",
			"Info" };
	
	static String[] TunnelCrossSectionIndex = { "Id", "Chainage",
			"InbuiltTime", "Width", "ExcavateMethod", "SurveyPntName", "Info",
			"ChainagePrefix", "GDU0", "GDVelocity", "GDU0Time",
			"GDU0Description", "SLU0", "SLLimitVelocity", "SLU0Time",
			"SLU0Description", "Lithologic", "LAYVALUE",
			"ROCKGRADE)" };

	static String[] SubsidenceCrossSectionIndex = { "Id", "Chainage",
			"InbuiltTime", "Width", "SurveyPnts", "SurveyPntName", "Info",
			"ChainagePrefix", "DBU0", "DBVelocity", "DBU0Time",
			"DBU0Description", "Lithologic", "LAYVALUE", "ROCKGRADE" };

	
	static String[] TunnelCrossSectionExIndex = { "Id", "ZONECODE", "SITECODE",
			"SECTNAME", "SECTCODE", "SECTKILO", "METHOD", "WIDTH",
			"MOVEVALUE_U0", "UPDATEDATE", "REMARK_U0", "HOLENAME",
			"HOLESTARTKILO", "HOLEENDKILO", "INNERCODES", "LAYTIME", "UPLOAD",
			"DESCRIPTION" };
	 static String[] RawSheetIndex={"Id",  
		  "CrossSectionType",  
		  "CreateTime",  
		  "Info" ,
		  "FACEDK",  
		  "FACEDESCRIPTION",  
		  "TEMPERATURE"  ,
		  "CrossSectionIDs"   };
	 static String[] SubsidenceTotalData={"Id"  
		  ,"StationId"  
		  ,"ChainageId"  
		  ,"SheetId"  
		  ,"Coordinate"  
		  ,"PntType"  
		  ,"SurveyTime"  
		  ,"MEASNo"  
		  ,"SurveyorID"  
		  ,"DataStatus"  
		  ,"DataCorrection"  
		  ,"Info"  
};
	 }
