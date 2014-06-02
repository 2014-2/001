package ICT.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.zw.android.framework.util.StringUtils;

import android.util.Log;

import com.crtb.tunnelmonitor.AppLogger;

/**
 * 标准文件导入/导出(文件复制)
 * 
 * @author zhouwei
 *
 */
public final class DbNoneEncrypt implements IDbEncrypt {
	
	static String TAG = "DbNoneEncrypt" ;

	public boolean encrypt(String srcFile, String destFile){
		
		if(StringUtils.isEmpty(srcFile) || StringUtils.isEmpty(destFile)){
			return false ;
		}
		
		try{
			
			FileInputStream in = new FileInputStream(srcFile);
			FileOutputStream out = new FileOutputStream(destFile);
			
			byte[] buffer = new byte[4 * 1024];
			int read = -1 ;
			
			while((read = in.read(buffer)) > 0){
				
				Log.d(TAG, "zhouwei : 导出文件...");
				
				out.write(buffer, 0, read);
				out.flush() ;
			}
			
			in.close() ;
			out.close() ;
			
			Thread.sleep(2000);
			
			return true ;
		} catch(Exception e){
			e.printStackTrace() ;
		}
		
		return false ;
	}

	public boolean decrypt(String srcFile, String destFile) {
		
		if(StringUtils.isEmpty(srcFile) || StringUtils.isEmpty(destFile)){
			return false ;
		}
		
		try{
			
			FileInputStream in 		= new FileInputStream(srcFile);
			FileOutputStream out 	= new FileOutputStream(destFile);
			
			byte[] buffer = new byte[4 * 1024];
			int read = -1 ;
			
			while((read = in.read(buffer)) > 0){
				
				AppLogger.d(TAG, "zhouwei : 导入数据库..."); 
				
				out.write(buffer, 0, read);
				out.flush() ;
			}
			
			in.close() ;
			out.close() ;
			
			return true ;
		} catch(Exception e){
			e.printStackTrace() ;
		}
		
		return false ;
	}
}
