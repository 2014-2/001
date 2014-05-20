package com.crtb.tunnelmonitor.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.impl.ExecuteAsyncTaskImpl;

import ICT.utils.DbAESEncrypt;
import ICT.utils.IDbEncrypt;
import android.content.Context;
import android.os.Environment;

import com.crtb.tunnelmonitor.AppConfig;
import com.crtb.tunnelmonitor.AppHandler;
import com.crtb.tunnelmonitor.AppLogger;
import com.crtb.tunnelmonitor.BaseAsyncTask;
import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;

/**
 * 数据库文件导入导出
 * @author zhouwei
 *
 */
public final class CrtbDbFileUtils {
	
	static String TAG = "CrtbDbFileUtils" ;

	public static void initCrtbDbFiles(Context context){
	
		final File file = Environment.getExternalStorageDirectory() ;
		
		if(file != null){
			
			try{
				
				String path = file.getAbsolutePath() ;
				
				File crtb = new File(path + AppConfig.DB_ROOT);
				
				if(!crtb.isDirectory()){
					crtb.mkdir() ;
				}
				
				File export = new File(crtb, AppConfig.DB_EXPORT_DIR);
				
				if(!export.isDirectory()){
					export.mkdir() ;
				}
				
				File inport = new File(crtb, AppConfig.DB_IMPORT_DIR);
				
				if(!inport.isDirectory()){
					inport.mkdir() ;
				}
				
			} catch(Exception e){
				e.printStackTrace() ;
			}
		}
	}
	
	private static String getExportPath(String dir){
		
		final File file = Environment.getExternalStorageDirectory() ;
		
		File crtb = new File(file.getAbsolutePath() + AppConfig.DB_ROOT);
		
		return new File(crtb, dir).getAbsolutePath() ;
	}
	
	public static List<File> getImportFiles(){
		
		List<File> list = new ArrayList<File>();
		
		String inPath = getExportPath(AppConfig.DB_IMPORT_DIR) ;
		
		File root = new File(inPath);
		
		File[] fs = root.listFiles() ;
		
		for(int index = 0 , size = fs.length ; index < size ; index++){
			
			if(!fs[index].isDirectory()){
				list.add(fs[index]);
			}
		}
		
		return list ;
	}
	
	public static List<File> getLocalDbFiles(Context context){
		
		List<File> list = new ArrayList<File>();
		
		if(context != null){
			
			String path = "/data/data/" + context.getPackageName() + "/databases/" ;
			
			File dir = new File(path);
			
			File[] fs = dir.listFiles() ;
			
			if(fs != null){
				for(File f : fs){
					list.add(f);
				}
			}
		}
		
		return list ;
	}
	
	public static void exportDb(final String path,
			final String filename,AppHandler handler){
		
		if(path == null || filename == null || handler == null){
			return ;
		}
		
		ExecuteAsyncTaskImpl.defaultSyncExecutor().executeTask(new BaseAsyncTask(handler) {
			
			@Override
			public void process() {
				
				String outPath = getExportPath(AppConfig.DB_EXPORT_DIR) + "/" + filename ;
				
				IDbEncrypt encrypt = new DbAESEncrypt() ;
				
				try {
					
					// 加密
					boolean success = encrypt.encrypt(path, outPath) ;
					
					Thread.sleep(2000);
					
					if(success){
						AppLogger.d(TAG, "zhouwei : 数据库导出完成: " + outPath); 
						sendMessage(MSG_EXPORT_DB_SUCCESS) ;
					} else {
						sendMessage(MSG_EXPORT_DB_FAILED) ;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					sendMessage(MSG_EXPORT_DB_FAILED) ;
				}
			}
		}) ;
	}
	
	public static void importDb(final Context context,
			final String path,
			AppHandler handler){
		
		if(context == null || path == null || handler == null){
			return ;
		}
		
		ExecuteAsyncTaskImpl.defaultSyncExecutor().executeTask(new BaseAsyncTask(handler) {
			
			@Override
			public void process() {
				
				String filename = path.substring(path.lastIndexOf("/") + 1);
				String outPath 	= "data/data/" + context.getPackageName() + "/databases/" + filename ;
				
				IDbEncrypt encrypt = new DbAESEncrypt() ;
				
				try {
					
					// 解密
					boolean success = encrypt.decrypt(path, outPath) ;
					
					if(success){
						
						// 写入数据库
						int code = ProjectIndexDao.defaultWorkPlanDao().inportDb(filename) ;
						
						Thread.sleep(2000);
						
						if(code == 100){
							
							// 删除文件
							File f = new File(outPath);
							f.delete() ;
							
							AppLogger.d(TAG, "zhouwei : 数据库导入失败"); 
							
							sendMessage(MSG_INPORT_DB_FAILED,"非注册用户,不能保存2个以上的工作面") ;
						} else {
							
							AppLogger.d(TAG, "zhouwei : 数据库导入完成: " + outPath); 
							
							sendMessage(MSG_INPORT_DB_SUCCESS) ;
						}
					} else {
						sendMessage(MSG_INPORT_DB_FAILED) ;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					sendMessage(MSG_INPORT_DB_FAILED) ;
				}
			}
		}) ;
	}
}
