package com.byd.player.bluetooth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BtActionManager {
	
    public enum BtCmdEnum{
    	BT_CMD_INVALID,
    	BT_CMD_LINK_LAST_BT,
    	BT_CMD_DISCONNECT,
        BT_CMD_PAIR,
        BT_CMD_EXIT_PAIR,
        BT_CMD_ANSWER,
        BT_CMD_HANG_UP,
        BT_CMD_REJECT,
        BT_CMD_REDIAL,
        BT_CMD_VOL_UP,
        BT_CMD_VOL_DOWN,
        BT_CMD_RELEASE_WAITING_CALL,
        BT_CMD_SWITCH_VOICE_DIAL,
        BT_CMD_SWITCH_AUDIO,
        BT_CMD_VERSION,
        BT_CMD_ID3_SUPPORT_INDICATION,
        BT_CMD_AVRCP_CONECT,
        BT_CMD_GET_ID3_INFO,
        BT_CMD_SET_AUTO_ANSWER,
        BT_CMD_CLR_AUTO_ANSWER,
        BT_CMD_STARTUP_SET_AUTO_LINK,
        BT_CMD_CLR_AUTO_LINK,
        BT_CMD_DIAL_TEL_NUMBER,
        BT_CMD_SEND_DTMF,
        BT_CMD_AV_PLAY,
        BT_CMD_CONNECT_A2DP,
        BT_CMD_AV_PAUSE,
        BT_CMD_AV_FORWARD,
        BT_CMD_AV_BACKWARD,
        BT_CMD_AV_STOP,
        BT_CMD_FAST_FORWARD,
        BT_CMD_FAST_FORWARD_STOP,
        BT_CMD_FAST_BACKWARD,
        BT_CMD_FAST_BACKWARD_STOP,
        BT_CMD_DOWNLOAD_SIM_PB,
        BT_CMD_DOWNLOAD_LOCAL_MOBILE_PB,
        BT_CMD_DOWNLOAD_DAIL_PB,
        BT_CMD_DOWNLOAD_MISS_PB,
        BT_CMD_DOWNLOAD_RECEIVE_PB,
        BT_CMD_SEARCH_BT_MOBILE,
        BT_CMD_STOP_SEARCH_BT_MOBILE,
        BT_CMD_CONNECT_SEARCHED_MOBILE_SERIAL_NUMBER,
        BT_CMD_CONNECT_PAIRED_MOBILE_SERIAL_NUMBER,
        BT_CMD_DELETE_PAIRED_MOBILE_SERIAL_NUMBER,
        BT_CMD_READ_PAIRED_DEVICE_LIST_INFO,
        BT_CMD_MIC_MUTE,
        BT_CMD_MIC_CLOSE_MUTE,
        BT_CMD_READ_PAIR_PIN,
        BT_CMD_MODIFY_PAIR_PIN,
        BT_CMD_READ_DEVICE_NAME,
        BT_CMD_CHANGE_DEVICE_NAME,
        BT_CMD_DFU_UPDATE_MODE,
        BT_CMD_GET_CURRENT_CONNECTED_MOBILE_NAME,
        BT_CMD_GET_CURRENT_BT_SETTING_FUNCTION_STATUS,
        BT_CMD_SEND_SPP_DATA_UTF8_ENCODE
    }
    
    public class BtCmdConfig{
    	byte[] cmdStart = null;
    	byte[] retStart = null;
    	byte[] cmdEnd = null;
    	byte[] retEnd = null;    	
    	
    	public boolean hasRet = false;
    	
    	public BtCmdConfig(byte[] cmd, byte[] ret){
    		this.cmdStart = cmd;
    		this.retStart = ret;
    		if (ret != null){
    			hasRet = true;
    		}
    	}
    	
    	public BtCmdConfig(byte[] cmdStart, byte[] cmdEnd, byte[] retStart, byte[] retEnd){
    		this.cmdStart = cmdStart;
    		this.retStart = retStart;
    		this.cmdEnd = cmdEnd;
    		this.retEnd = retEnd;
    		if (retStart != null || retEnd != null){
    			hasRet = true;
    		}
    	}
    }
    
    private static Map<BtCmdEnum, BtCmdConfig> BtCmdMap = new HashMap<BtCmdEnum, BtCmdConfig>();    
    private static BtActionManager instance = null;
    public static BtActionManager instance(){
    	if (instance == null){
    		instance = new BtActionManager();
    	}
    	return instance;
    }
    
    private BtActionManager(){
        init();
    }
    
    private void init(){
    	BtCmdMap.put(BtCmdEnum.BT_CMD_LINK_LAST_BT, 
    			new BtCmdConfig(new byte[]{'S'}, new byte[]{'s'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_DISCONNECT, 
    			new BtCmdConfig(new byte[]{'Q'}, new byte[]{'q'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_PAIR, 
    			new BtCmdConfig(new byte[]{'P'}, new byte[]{'P'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_EXIT_PAIR, 
    			new BtCmdConfig(new byte[]{'p'}, new byte[]{'p'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_ANSWER, 
    			new BtCmdConfig(new byte[]{'A'}, new byte[]{'a'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_HANG_UP, 
    			new BtCmdConfig(new byte[]{'H'}, new byte[]{'h'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_REJECT, 
    			new BtCmdConfig(new byte[]{'R'}, new byte[]{'r'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_REDIAL, 
    			new BtCmdConfig(new byte[]{'L'}, new byte[]{'l'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_VOL_UP, 
    			new BtCmdConfig(new byte[]{'U'}, new byte[]{'u'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_VOL_DOWN, 
    			new BtCmdConfig(new byte[]{'D'}, new byte[]{'d'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_RELEASE_WAITING_CALL, 
    			new BtCmdConfig(new byte[]{'O'}, new byte[]{'o'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_SWITCH_VOICE_DIAL, 
    			new BtCmdConfig(new byte[]{'V'}, new byte[]{'v'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_SWITCH_AUDIO, 
    			new BtCmdConfig(new byte[]{'Z'}, new byte[]{'Z'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_VERSION, 
    			new BtCmdConfig(new byte[]{'Y'}, new byte[]{'V'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_ID3_SUPPORT_INDICATION, 
    			new BtCmdConfig(new byte[]{'M', 'I'}, new byte[]{'M', 'I'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_AVRCP_CONECT, 
    			new BtCmdConfig(new byte[]{'M', 'J'}, null));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_GET_ID3_INFO, 
    			new BtCmdConfig(new byte[]{'M', 'H'}, null));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_SET_AUTO_ANSWER, 
    			new BtCmdConfig(new byte[]{'T'}, new byte[]{'T'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_CLR_AUTO_ANSWER, 
    			new BtCmdConfig(new byte[]{'t'}, new byte[]{'t'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_STARTUP_SET_AUTO_LINK, 
    			new BtCmdConfig(new byte[]{'N'}, new byte[]{'N'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_CLR_AUTO_LINK, 
    			new BtCmdConfig(new byte[]{'n'}, new byte[]{'n'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_DIAL_TEL_NUMBER, 
    			new BtCmdConfig(new byte[]{'('}, new byte[]{')'}, null, null));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_SEND_DTMF, 
    			new BtCmdConfig(new byte[]{'G'}, null));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_AV_PLAY, 
    			new BtCmdConfig(new byte[]{'<', 'F', 'P', '>'}, null));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_CONNECT_A2DP, 
    			new BtCmdConfig(new byte[]{'<', 'F', 'L', '>'}, null));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_AV_PAUSE, 
    			new BtCmdConfig(new byte[]{'<', 'F', 'U', '>'}, new byte[]{'<', 'f', 'u', '>'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_AV_FORWARD, 
    			new BtCmdConfig(new byte[]{'<', 'F', 'F', '>'}, new byte[]{'<', 'f', 'f', '>'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_AV_BACKWARD, 
    			new BtCmdConfig(new byte[]{'<', 'F', 'B', '>'}, new byte[]{'<', 'f', 'b', '>'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_AV_STOP, 
    			new BtCmdConfig(new byte[]{'<', 'F', 'S', '>'}, new byte[]{'<', 'f', 's', '>'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_FAST_FORWARD, 
    			new BtCmdConfig(new byte[]{'<', 'F', 'C', '>'}, new byte[]{'<', 'f', 'c', '>'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_FAST_FORWARD_STOP, 
    			new BtCmdConfig(new byte[]{'<', 'F', 'D', '>'}, new byte[]{'<', 'f', 'd', '>'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_FAST_BACKWARD, 
    			new BtCmdConfig(new byte[]{'<', 'F', 'E', '>'}, new byte[]{'<', 'f', 'e', '>'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_FAST_BACKWARD_STOP, 
    			new BtCmdConfig(new byte[]{'<', 'F', 'G', '>'}, new byte[]{'<', 'f', 'g', '>'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_DOWNLOAD_SIM_PB, 
    			new BtCmdConfig(new byte[]{'B'}, null));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_DOWNLOAD_LOCAL_MOBILE_PB, 
    			new BtCmdConfig(new byte[]{'E'}, null));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_DOWNLOAD_DAIL_PB, 
    			new BtCmdConfig(new byte[]{'[', 'D', ']'}, null));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_DOWNLOAD_MISS_PB, 
    			new BtCmdConfig(new byte[]{'[', 'M', ']'}, null));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_DOWNLOAD_RECEIVE_PB, 
    			new BtCmdConfig(new byte[]{'[', 'R', ']'}, null));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_SEARCH_BT_MOBILE, 
    			new BtCmdConfig(new byte[]{'W', '0'}, new byte[]{'W', '0'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_STOP_SEARCH_BT_MOBILE, 
    			new BtCmdConfig(new byte[]{'w'}, new byte[]{'w'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_CONNECT_SEARCHED_MOBILE_SERIAL_NUMBER, 
    			new BtCmdConfig(new byte[]{'W'}, new byte[]{'W'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_CONNECT_PAIRED_MOBILE_SERIAL_NUMBER, 
    			new BtCmdConfig(new byte[]{'I'}, new byte[]{'I'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_DELETE_PAIRED_MOBILE_SERIAL_NUMBER, 
    			new BtCmdConfig(new byte[]{'J'}, new byte[]{'J'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_READ_PAIRED_DEVICE_LIST_INFO, 
    			new BtCmdConfig(new byte[]{'#'}, null));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_MIC_MUTE, 
    			new BtCmdConfig(new byte[]{'[', 'N', ']'}, new byte[]{'[', 'N', ']'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_MIC_CLOSE_MUTE, 
    			new BtCmdConfig(new byte[]{'[', 'Y', ']'}, new byte[]{'[', 'Y', ']'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_READ_PAIR_PIN, 
    			new BtCmdConfig(new byte[]{'[', 'U', ']'}, null, new byte[]{'[', 'U'}, new byte[]{']'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_MODIFY_PAIR_PIN, 
    			new BtCmdConfig(new byte[]{'[', 'P'}, new byte[]{']'}, new byte[]{'[', 'U', ']'}, null));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_READ_DEVICE_NAME, 
    			new BtCmdConfig(new byte[]{'[', 'A', ']'}, null, new byte[]{'[', 'A'}, new byte[]{']'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_CHANGE_DEVICE_NAME, 
    			new BtCmdConfig(new byte[]{'[', 'C'}, new byte[]{']'}, new byte[]{'[', 'C', ']'}, null));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_DFU_UPDATE_MODE, 
    			new BtCmdConfig(new byte[]{'[', 'F', ']'}, null));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_GET_CURRENT_CONNECTED_MOBILE_NAME, 
    			new BtCmdConfig(new byte[]{'[', 'S', ']'}, null, new byte[]{'$'},  new byte[]{'$'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_GET_CURRENT_BT_SETTING_FUNCTION_STATUS, 
    			new BtCmdConfig(new byte[]{'?'}, new byte[]{'?'}));
    	BtCmdMap.put(BtCmdEnum.BT_CMD_SEND_SPP_DATA_UTF8_ENCODE, 
    			new BtCmdConfig(new byte[]{'S', 'P', 'P'}, new byte[]{'S', 'P', 'P', 'e', 'n', 'd'}));
    }
    
    public BtCmd createBtCmd(BtCmdEnum type, String param){
    	BtCmdConfig config = BtCmdMap.get(type);
    	if (config != null){
        	return new BtCmd(type, config, param);
    	} else {
    		return null;
    	}
    }
    
//    public BtCmdEnum getActionTypeByReturnValue(byte[] buffer, int size){
//    	
//    	for (BtCmdEnum cmdEnum : BtCmdEnum.values()){
//    		BtCmdConfig config = BtCmdMap.get(cmdEnum);
//    		if (config != null){
//    		}
//    	}
//    	
//    	return BtCmdEnum.BT_CMD_INVALID;
//    }
    
    public class BtCmd{
    	
    	public static final int BTCMD_STATUS_NOT_START = 0;
    	public static final int BTCMD_STATUS_START = 1;
    	public static final int BTCMD_STATUS_SUCCESS = 2;
    	public static final int BTCMD_STATUS_FAIL = 3;

    	public int status = BTCMD_STATUS_NOT_START;
    	public String cmd;
    	public String param;
    	public byte[] ret;
    	public int retLen;
    	public BtCmdConfig config;
    	public BtCmdEnum type;
    	
    	private BtCmd(BtCmdEnum type, BtCmdConfig config, String param){
    		this.type = type;
    		this.config = config;
    		this.param = param;
    		cmd = " ";
    		if (config.cmdStart != null){
    			cmd += new String(config.cmdStart);
    		}
    		if (param != null && !param.isEmpty()){
    			cmd += param;
    		}
    		if (config.cmdEnd != null){
    			cmd += new String(config.cmdEnd);
    		}
    	}
    	
    	/*
    	 * 解析返回值，获得返回参数。
    	 * return true
    	 */
    	private static final int endFlagLen = 2;// the length of \r\n
    	public boolean checkRetValue(byte[] buffer, int size){
    		int start = 0;
    		int end = 0;
    		if (config.retStart != null){
    			start = config.retStart.length;
    		}
    		if (config.retEnd != null){
    			end = config.retEnd.length;
    		}
    		retLen = size - start - end - endFlagLen;
    		if (retLen < 0){
    			return false;
    		}
    		//check retStart
			for (int i = 0; i < start; i++){
				if (config.retStart[i] != buffer[i]){
					return false;
				}
			}
			//check retEnd
			for (int i = 0; i < end; i++){
				if (config.retEnd[i] != buffer[start + retLen + i]){
					return false;
				}
			}
			if (retLen > 0){
				ret = Arrays.copyOfRange(buffer, start, start + retLen);
			}
    		return true;
    	}
    }
}
