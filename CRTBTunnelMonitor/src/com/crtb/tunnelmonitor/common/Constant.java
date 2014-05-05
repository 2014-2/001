
package com.crtb.tunnelmonitor.common;

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
	public enum TotalStationType
	{
		Leica("徕卡"),	
		Trimble("天宝"),
		LeicaTPS("徕卡TPS"),
		Topcon("拓普康"),
		Pentax("宾得"),
		Sokkia("索佳"),
		Nikon("尼康"),
		South("南方"),
		South302("南方302"),
		KTS("科力达"),
		SanDing("三鼎"),
		RuiDe("瑞得"),
		Foif("苏一光"),
		GeMax("中纬"),
		Kovan("科维");
		private final String desc; 

        private TotalStationType(String desc) { 
                this.desc = desc; 
        } 

        public String getDesc() { 
                return desc; 
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
	//用户验证
	public static final String UserSelect = "http://61.237.239.144/fxkz/basedown";
	//上传
	public static final String Update = "http://61.237.239.144/fxkz/testdata";
	
	//测试用
	public static final String testUsername = "cl19h1";
	public static final String testPassword = "123456";
	public static final String testPhysical= "04:4b:ff:07:de:23";
	public static final String testDeskey = "crtb1234";
	public static final String testPublicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMFUtGx6lOnO5dLxy/1uNqUAzG7mhRKkWFJEZ9QWup+Y1+bgRoz2xdlL1ZqwpFi3AYbFrCa37zK1A5WbCvq37j0CAwEAAQ==";


	public Constant() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
