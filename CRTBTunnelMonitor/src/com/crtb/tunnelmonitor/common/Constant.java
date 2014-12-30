
package com.crtb.tunnelmonitor.common;

import java.util.Date;
import java.util.HashMap;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

import android.graphics.Color;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

/**
 * 常量定义
 */
public class Constant {

	private static Version curVersion = Version.Release;
	
	/*登录选择name和value*/
	public static final String LOGIN_TYPE = "name";
	public static final int LOCAL_USER = 1;
	public static final int SERVER_USER= 2;
	
	/**全站仪管理菜单项*/
	public static final CharSequence TotalStationItems[] = { 
		"Leica", "Trimble", "LeicaTPS",
		"Topcon","Pentax","Sokkia",
		"Nikon","South","South302",
		"KTS","SanDing","RuiDe",
		"Foif","GeMax","Kovan"
		};
	
	public static final CharSequence TotalStationNameItems[] = { 
		"徕卡", "天宝", "徕卡TPS",
		"拓普康","宾得","索佳",
		"尼康","南方","南方302",
		"科力达","三鼎","瑞得",
		"苏一光","中纬","科维"
		};
	
	public static final HashMap TotalStationIndex = new HashMap<String, String>() {
        {
            put("Leica", "4");
            put("Trimble", "8");
            put("LeicaTPS", "0");
            put("Topcon", "9");
            put("Pentax", "10");
            put("Sokkia", "5");
            put("Nikon", "11");
            put("South", "6");
            put("South302", "7");
            put("KTS", "12");
            put("SanDing", "13");
            put("RuiDe", "14");
            put("Foif", "17");
            put("GeMax", "15");
            put("Kovan", "16");
        }
    };

    public enum TotalStationType
	{
		Leica("徕卡",4),	
		Trimble("天宝",8),
		LeicaTPS("徕卡TPS",0),
		Topcon("拓普康",9),
		Pentax("宾得",10),
		Sokkia("索佳",5),
		Nikon("尼康",11),
		South("南方",6),
		South302("南方302",7),
		KTS("科力达",12),
		SanDing("三鼎",13),
		RuiDe("瑞得",14),
		Foif("苏一光",17),
		GeMax("中纬",15),
		Kovan("科维",16);
		private final String desc; 
		private final int val;

        private TotalStationType(String desc,int val) { 
                this.desc = desc; 
                this.val = val;
        } 

        public String getDesc() { 
                return desc; 
        }	
        
        /**
         * 根据Value获取全站仪的类型
         * @param ordinal
         * @return
         */
        public static TotalStationType parser(int ordinal){
        	switch (ordinal)
        	{
        	case 4: return Leica; 
        	case 8: return Trimble;
        	case 0: return LeicaTPS; 
        	case 9: return Topcon;
        	case 10: return Pentax;
        	case 5: return Sokkia;
        	case 11: return Nikon;
        	case 6: return South;
        	case 7: return South302;
        	case 12: return KTS;
        	case 13: return SanDing;
        	case 14: return RuiDe;
        	case 17: return Foif;
        	case 15: return GeMax;
        	case 16: return Kovan;
        	}
	        return null;
      	}
    }
    
	public static final CharSequence ControlPointsItems[] = { "蓝牙连接", "串口连接", "断开连接"};
	/*工作面行点击菜单项选择name和value*/
	public static final String Select_WorkRowClickItemsName_Name = "name";
	public static final String Select_SectionRowClickItemsName_Name = "name";
	public static final String Select_RecordRowClickItemsName_Name = "name";
	public static final String Select_RecordRowClickItemsName_Data = "data";
	public static final String Select_TotalStationRowClickItemsName_Name = "name";
	public static final String Select_TotalStationRowClickItemsName_Data = "data";
	public static final String Select_ControlPointsRowClickItemsName_Name = "name";
	public static final String Select_ControlPointsRowClickItemsName_Data = "data";
	public static final String Select_WorkRowClickItemsValue_Open = "数据库名称";
	public static final String Select_WorkRowClickItemsValue_Edit = "编辑";
	

	//命名空间
	public static final String NameSpace="webservice.riskcontrol.com";
	
	//测试用
	public static final String LOG_TAG = "CrtbLog";
	public static final String LOG_TAG_SERVICE = "CrtbWebService";
	public static final String LOG_TAG_ACTIVITY = "CrtbActivity";
	public static final String LOG_TAG_DAO = "CrtbDao";
	
	public static final String testUsername = "cl19h2";
	public static final String testPassword = "123456";
	public static String testPhysical= "04:4b:ff:07:de:22";
	public static final String testCard= "522722198906031000";
	//public static  String testPhysical= "72:80:09:00:60:11";
	public static final String testDeskey = "crtb1234";
	public static final String testPublicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMFUtGx6lOnO5dLxy/1uNqUAzG7mhRKkWFJEZ9QWup+Y1+bgRoz2xdlL1ZqwpFi3AYbFrCa37zK1A5WbCvq37j0CAwEAAQ==";

//YX 从AlertUtils 移到 当前文件 
//    public static final String[] U_TYPE_MSGS = {"拱顶累计下沉值超限", "拱顶下沉速率超限", "累计收敛超限", "收敛速率超限", "地表累计下沉值超限", "地表下沉速率超限"};
//
//    public static final String[] U_TYPE_MSGS_SAFE = {"拱顶累计下沉值", "拱顶下沉速率", "累计收敛", "收敛速率", "地表累计下沉值", "地表下沉速率"};
	
	/**
	 * 点位的超限信息
	 */
    public static final String[] U_TYPE_MSGS = {"拱顶累计下沉超限", "拱顶下沉速率超限", "累计收敛超限", "收敛速率超限", "地表累计下沉超限", "地表下沉速率超限"};

    /**
     * 点位的正常信息
     */
    public static final String[] U_TYPE_MSGS_SAFE = {"拱顶累计下沉", "拱顶下沉速率", "累计收敛", "收敛速率", "地表累计下沉", "地表下沉速率"};

    /**
     * 累积位移等级对应的颜色
     */
    public static int[] leijiOffsetLevelColor = new int[]{Color.GREEN,Color.RED,Color.parseColor("#C87A05")};
    
    /**
     * 累积位移等级对应的颜色
     */
    public static int[] sulvOffsetLevelColor = new int[]{Color.BLACK,Color.BLACK};
    
	/**
	 * 报警的最大值
	 */
	public static final int ALARM_MAX_VALUE = 3000;
	
    /**
     *  累计变形值阈值
     */
    public static final double ACCUMULATIVE_THRESHOLD = 100; // mm

    /**
     *  变形速率阈值
     */
    public static final double SPEED_THRESHOLD = 5; // mm/d
    
    /**
     *  对应  AlertList 表中的OriginalDataID列, 一条测线两测点数据id间的分隔符
     */
    public static final String ORIGINAL_ID_DIVIDER = ",";

    /**
     *  0表示拱顶累计下沉值超限
     */
    public static final int GONGDING_LEIJI_XIACHEN_EXCEEDING = 0;

    /**
     *  1表示拱顶下沉速率超限
     */
    public static final int GONGDINGI_XIACHEN_SULV_EXCEEDING = 1;

    /**
     *  2表示收敛累计值超限
     */
    public static final int SHOULIAN_LEIJI_EXCEEDING = 2;

    /**
     *  3表示收敛速率超限
     */
    public static final int SHOULIAN_SULV_EXCEEDING = 3;

    /**
     *  4表示地表累计下沉值超限
     */
    public static final int DIBIAO_LEIJI_XIACHEN_EXCEEDING = 4;

    /**
     *  5表示地表下沉速率超限
     */
    public static final int DIBIAO_XIACHEN_SULV_EXCEEDING = 5;

    /**
     * 0报警已经处理-即已消警
     */
    public static final int ALERT_STATUS_HANDLED = 0;
    
    /**
     * 1报警状态为开
     */
    public static final int ALERT_STATUS_OPEN = 1;
    
    /**
     * 2报警处理中
     */
    public static final int ALERT_STATUS_HANDLING = 2;
    
    /**
     * 报警状态消息
     */
    public static final String[] ALERT_STATUS_MSGS = {"已消警", "开", "处理中"};

    /**
     * 报警处理：0没有处理
     */
    public static final int POINT_DATASTATUS_NONE = 0;
    
    /**
     * 报警处理：1不参与计算
     */
    public static final int POINT_DATASTATUS_DISCARD = 1;
    
    /**
     * 报警处理：2作为首先
     */
    public static final int POINT_DATASTATUS_AS_FIRSTLINE = 2;
    
    /**
     * 报警处理：3添加改正值
     */
    public static final int POINT_DATASTATUS_CORRECTION = 3;
    
    /**
     * 报警处理：4正常计算
     */
    public static final int POINT_DATASTATUS_NORMAL = 4;
    
    /**
     * 报警处理消息
     */
    public static final String[] ALERT_HANDLING = {"没有处理", "不参与计算", "作为首行", "添加修正值", "正常参与计算"};
    
//YX 新增
	
	public static Date WaringDeadTime = CrtbUtils.parseDate("2015-12-31 0:0:0");
	//用户验证-wylctest
	public static final String USER_SELECT_FXTEST = "http://61.237.239.144/wylctest/basedown";
	//上传-wylctest
	public static final String UPDATE_FXTEST = "http://61.237.239.144/wylctest/testdata";
	
	//用户验证-fxkz
	public static final String USER_SELECT_FXKZ = "http://61.237.239.144/fxkz/basedown";
	//上传-fxkz
	public static final String UPDATE_FXKZ = "http://61.237.239.144/fxkz/testdata";
	
	public static final int STABLE_METHOD_END_INDEX = 7;
	
	public static final int CUSTOM_METHOD_START_INDEX = 9;
	
	public static final int NO_BASE_CUSTOM_METHOD_START_INDEX = 13;
	
	public static final int SERVER_ALLOW_TEST_LINE_MAX = 22;
	
	public static final int LEI_JI_INDEX = 0;
	
    public static final int SU_LV_INDEX = 1;
	public static final String WARNING_TYPE = "WARING_TYPE";
	
	private static VersionControl versionControl = null;
	
    /**
     * 累积位移管理等级基数
     */
    public static int[] LEI_JI_OFFSET_LEVEL_BASE = new int[]{40,40,40,50,75,75};
	
    /**
     * 获取用户验证的服务地址
     * @return
     */
	public static String getUserAuthUrl(){
		if(versionControl.getTestServer()){
			return USER_SELECT_FXTEST;
		} else{
			return USER_SELECT_FXKZ;
		}
	}
	
	/**
	 * 获取上传服务地址
	 * @return
	 */
	public static String getUploadUrl(){
		if(versionControl.getTestServer()){
			return UPDATE_FXTEST;
		} else{
			return UPDATE_FXKZ;
		}
	}
	
	/**
	 * 获取设备的Mac地址
	 * @return
	 */
	public static String getDeviceMac(){
		if(versionControl.getRealMac()){
			return AppCRTBApplication.getDeviceMac();
		} else{
			return testPhysical;
		}
	}
	
	/**
	 * 是否手输坐标
	 * @return
	 */
	public static boolean getStationDebug(){
		return versionControl.getStationDebug();
	}
	
	/**
	 * 是否加载用户测试信息
	 * @return
	 */
	public static boolean getIsTestInfo(){
		return versionControl.getTestInfo();
	}
	
	/**
	 * 是否能修改Mac地址
	 * @return
	 */
	public static boolean getIsEditMac(){
		return versionControl.getIsEditMac();
	}
	
	 /**
	  * 严格的上传限制：记录单中存在未处理的预警、则不能上传本条记录单
	  * @return
	  */
	public static boolean getStrictRestrictedUpload(){
		return versionControl.getStrictRestrictedUpload();
	}
	
	static{
		//station_debug,test_server,realMac,useTestInfo,editMac,noUploadDataWhenWarningUnHandled
		//正常发布版本：无严格上传限制
		if (curVersion == Version.Release) {
			versionControl = new VersionControl(false, false, true, false, false, false);
		} 
		//发布版本：严格上传限制版本-记录单中存在未处理的预警、则不能上传本条记录单
		else if (curVersion == Version.ReleaseUploadStrictRestricted) {
			versionControl = new VersionControl(false, false, true, false, false, true);
		} 
		//一般测试：手输坐标+测试服务器+非真实Mac地址+测试信息+能编辑Mac地址+非严格上传限制
		else if(curVersion == Version.InnerTest){
			versionControl = new VersionControl(true, true, false, true, true, false);
		}
		//测试严格上传限制:一般测试+严格上传限制
		else if(curVersion == Version.InnerTestUploadStrictRestricted){
			versionControl = new VersionControl(true, true, false, true, true, true);
		}
		//帮客户上传数据 : 一般测试+真实账号
		else if (curVersion == Version.HelpCustomUpload) {
			versionControl = new VersionControl(true, false, false, true, true, false);
		}
		//帮客户上传数据：严格限制
		else if (curVersion == Version.HelpCustomUploadStrictRestricted) {
			versionControl = new VersionControl(true, false, false, true, true, true);
		}
		
		
	}

	static class VersionControl{
		// 全站仪手输账号
		private boolean station_debug;
		
		// 是否为测试服务器
		private boolean test_server;
		
		// 是否为真实Mac地址
		private boolean realMac;
		
		// 是否加载用户测试信息
		private boolean loadingTestInfo; 
		
		// 是否能修改Mac地址
		private boolean editableMac;
		
		// 严格的上传限制：记录单中存在未处理的预警、则不能上传本条记录单
		private boolean strictRestrictedUpload;
		
		/**
		 * 
		 * @param station_debug 是否手输坐标
		 * @param test_server 是否为测试服务器
		 * @param realMac 是否为真实Mac地址
		 * @param loadingTestInfo 是否加载用户测试信息
		 * @param editableMac 是否能修改Mac地址
		 * @param strictRestrictedUpload 严格的上传限制：记录单中存在未处理的预警、则不能上传本条记录单
		 */
		public VersionControl(boolean station_debug,boolean test_server,boolean realMac,boolean loadingTestInfo,boolean editableMac,boolean strictRestrictedUpload){
			this.station_debug = station_debug;
			this.test_server = test_server;
			this.realMac = realMac;
			this.loadingTestInfo = loadingTestInfo;
			this.editableMac = editableMac;
			this.strictRestrictedUpload = strictRestrictedUpload;
		}
		
		public boolean getStationDebug(){
			return station_debug;
		}
		
		public boolean getTestServer(){
			return test_server;
		}
		
		public boolean getRealMac(){
			return realMac;
		}
		
		public boolean getTestInfo(){
			return loadingTestInfo;
		}
		
		public boolean getIsEditMac(){
			return editableMac;
		}
		
		public boolean getStrictRestrictedUpload(){
			return strictRestrictedUpload;
		}
	}
	
	enum Version{
		Release,
		ReleaseUploadStrictRestricted,
		InnerTest,
		InnerTestUploadStrictRestricted,
		HelpCustomUpload,
		HelpCustomUploadStrictRestricted
	}
}
