package android_serialport_api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android.util.Log;

public class BT_Controller {	
	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	protected InputStream mInputStream;
	private ReadThread mReadThread;
	private onReadCmdListener onReadCmdListener;
	
	byte[] mCmd;
	int mCap=128;   //mCmd的容量
	int mPos=0;     //mCmd的实际大小
	boolean bNewCmd=true;
	
	public void BT_start() throws SecurityException, IOException {		
		mCap=128;
		mPos=0;
		mCmd = new byte[mCap];
		Arrays.fill(mCmd, (byte) 0x0);			
		
		mSerialPort = new SerialPort("/dev/ttymxc1", 19200, 0);
		mOutputStream = mSerialPort.getOutputStream();
		mInputStream = mSerialPort.getInputStream();

		/* Create a receiving thread */
		mReadThread = new ReadThread();
		mReadThread.start();		 
	}
	
	public void BT_stop(){
	  if (mReadThread != null){
		  mReadThread.interrupt();
	  }
	  
	  if (mSerialPort != null) {			  
          mSerialPort.close();
		  mSerialPort = null;
	  }
	}
	
	public boolean writeCmd(String cmd){
		if (mSerialPort != null) {						
			if (mOutputStream != null) {
				try {
					mOutputStream.write(cmd.getBytes());
					Log.d("BT_Controller", "write cmd success:" + cmd);
					return true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public String Read_Command(){
		String str = new String(mCmd, 0, mPos);
		return str;
	}
	
	protected void onDataReceived(final byte[] buffer, final int size) {
		if(bNewCmd){
			mPos = 0;
			Arrays.fill(mCmd, (byte) 0x0);
			bNewCmd = false;
		}
		int iSize = size;
		if(mPos+iSize-1>mCap){
			int len = mCap;					
			byte[] tmp = new byte[len];
			for(int j=0;j<len;j++){
		    	tmp[j] = mCmd[j];
			}
			mCap = mCap*2;
			mCmd = new byte[mCap];
			Arrays.fill(mCmd, (byte) 0x0);
			for(int k=0;k<len;k++){
				mCmd[k] = tmp[k];
			}
		}
		boolean bFind = true;
		boolean bClean = false;				
		while(bFind){
			int pos=-1;
			int i;
			for(i=0; i<iSize; i++){
				if(buffer[i] == 0x0a){
					pos = i;							
				}
			}
			if(pos != -1 && pos+1 != iSize){
				bClean = true;
				bFind = true;
				for(int j=0; j<iSize-pos-1; j++){
					buffer[j] = buffer[j+pos+1];
				}
				for(int k=iSize-pos-1; k<iSize; k++){
					buffer[k] = 0;
				}
				iSize = iSize-pos-1;
			}else{
				bFind = false;
				bClean = false;
			}
		}
				
		if(bClean){
			mPos = 0;
			Arrays.fill(mCmd, (byte) 0x0);
		}else{
			for(int z=0; z<iSize; z++){						
				mCmd[mPos] = buffer[z]; 
				mPos++;
			}
		}			
			
		if(mCmd[mPos-1] == 0x0a && (mPos > 1 && mCmd[mPos - 2] == 0x0d)){ 				
			bNewCmd = true;
		}					
	}
	
	protected void onHandleCmd(){
		if (onReadCmdListener != null){
			if (mCmd[0] != 'C'){
				Log.d("onHandleCmd", Read_Command());
			}
			onReadCmdListener.onReadCmd(mCmd, mPos);
		}
	}
	
	public void setOnReadCmdListener(onReadCmdListener onReadCmdListener){
		this.onReadCmdListener = onReadCmdListener;
	}
	
	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			while(!isInterrupted()) {
				int size;
				try {
					byte[] buffer = new byte[128];
					if (mInputStream == null) return;
					size = mInputStream.read(buffer);
					if (size > 0) {
						onDataReceived(buffer, size);
					}
					if (bNewCmd){
						onHandleCmd();
						try {
							sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}
	
	public interface onReadCmdListener {
		public void onReadCmd(byte[] buffer, int size);
	}
}
