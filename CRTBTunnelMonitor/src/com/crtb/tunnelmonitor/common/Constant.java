
package com.crtb.tunnelmonitor.common;

import java.util.HashMap;

import com.crtb.tunnelmonitor.AppCRTBApplication;

/**
 * 常量定义
 */
public class Constant {

	/*登录选择name和value*/
	public static final String LOGIN_TYPE = "name";
	public static final int LOCAL_USER = 1;
	public static final int SERVER_USER= 2;
	
	/*工作面行点击菜单项*/
	public static final CharSequence WorkRowClickItems[] = { "打开", "编辑", "导出", "删除" };
	public static final CharSequence SectionRowClickItems[] = {"编辑","删除" };
	public static final CharSequence RecordRowClickItems[] = {"编辑","删除" };
	/**全站仪管理菜单项*/
	public static final CharSequence TotalStationItems[] = { "Leica", "Trimble", "LeicaTPS",
		"Topcon","Pentax","Sokkia",
		"Nikon","South","South302",
		"KTS","SanDing","RuiDe",
		"Foif","GeMax","Kovan"};
	public static final CharSequence TotalStationNameItems[] = { "徕卡", "天宝", "徕卡TPS",
		"拓普康","宾得","索佳",
		"尼康","南方","南方302",
		"科力达","三鼎","瑞得",
		"苏一光","中纬","科维"};
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
		Leica("徕卡",4),	//4
		Trimble("天宝",8),//8
		LeicaTPS("徕卡TPS",0),//0
		Topcon("拓普康",9),//9
		Pentax("宾得",10),//10
		Sokkia("索佳",5),//5
		Nikon("尼康",11),//11
		South("南方",6),//6
		South302("南方302",7),//7
		KTS("科力达",12),//12
		SanDing("三鼎",13),//13
		RuiDe("瑞得",14),//14
		Foif("苏一光",17),//17
		GeMax("中纬",15),//15
		Kovan("科维",16);//16
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
	public static final String testUsername = "cl19h1";
	public static final String testPassword = "123456";
	public static final String testPhysical= "04:4b:ff:07:de:23";
	public static final String testDeskey = "crtb1234";
	public static final String testPublicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMFUtGx6lOnO5dLxy/1uNqUAzG7mhRKkWFJEZ9QWup+Y1+bgRoz2xdlL1ZqwpFi3AYbFrCa37zK1A5WbCvq37j0CAwEAAQ==";

//YX 新增
	//用户验证-fxtest
	public static final String USER_SELECT_FXTEST = "http://61.237.239.144/fxtest/basedown";
	//上传-fxtest
	public static final String UPDATE_FXTEST = "http://61.237.239.144/fxtest/testdata";
	
	//用户验证-fxkz
	public static final String USER_SELECT_FXKZ = "http://61.237.239.144/fxkz/basedown";
	//上传-fxkz
	public static final String UPDATE_FXKZ = "http://61.237.239.144/fxkz/testdata";
	
	public static final int CUSTOM_METHOD_START_INDEX = 9;

	private static VersionControl versionControl = null;
	
	public static String getUserAuthUrl(){
		if(versionControl.getTestServer()){
			return USER_SELECT_FXTEST;
		} else{
			return USER_SELECT_FXKZ;
		}
	}
	
	public static String getUploadUrl(){
		if(versionControl.getTestServer()){
			return UPDATE_FXTEST;
		} else{
			return UPDATE_FXKZ;
		}
	}
	
	public static String getDeviceMac(){
		if(versionControl.getRealMac()){
			return AppCRTBApplication.getDeviceMac();
		} else{
			return testPhysical;
		}
	}
	
	public static boolean getStationDebug(){
		return versionControl.getStationDebug();
	}
	
	public Constant() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	static{
		//YX测试版本:手输坐标-fxtest-测试mac地址
		versionControl = new VersionControl(true,true,false);
				
		//测试版本:手输坐标-fxtest-测试mac地址
		//versionControl = new VersionControl(true,true,false);
		
		//发布版本:全站仪坐标-fxkz-真实mac地址
		//versionControl = new VersionControl(false,false,true);
	}

	static class VersionControl{
		// 全站仪手输账号
		private boolean station_debug;
		
		// 测试服务器地址
		private boolean test_server;
		
		//真实Mac地址
		private boolean realMac;
		
		public VersionControl(boolean station_debug,boolean test_server,boolean realMac){
			this.station_debug = station_debug;
			this.test_server = test_server;
			this.realMac = realMac;
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
	}
}
