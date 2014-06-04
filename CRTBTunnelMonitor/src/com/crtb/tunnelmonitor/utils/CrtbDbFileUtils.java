package com.crtb.tunnelmonitor.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.impl.ExecuteAsyncTaskImpl;
import org.zw.android.framework.impl.FrameworkFacade;
import org.zw.android.framework.util.StringUtils;

import ICT.utils.DbAESEncrypt;
import ICT.utils.DbNoneEncrypt;
import ICT.utils.IDbEncrypt;
import android.content.Context;
import android.os.Environment;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.AppConfig;
import com.crtb.tunnelmonitor.AppHandler;
import com.crtb.tunnelmonitor.AppLogger;
import com.crtb.tunnelmonitor.BaseAsyncTask;
import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.entity.ProjectIndex;

/**
 * 数据库文件导入导出
 * 1. 打开数据库文件(解密)
 * 2. 关闭数据库文件(加密->覆盖)
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
	
	/**
	 * 删除临时数据库文件
	 */
	public static void deleteTempDb(){
		
		String path = "/data/data/" + AppCRTBApplication.getAppContext().getPackageName() + "/databases/" ;
		
		File dir = new File(path);
		
		File[] fs = dir.listFiles() ;
		
		if(fs != null){
			
			for(File f : fs){
				
				String fn = f.getName() ;
				String suffix = fn.substring(fn.lastIndexOf("."));
				
				if(suffix.equals(AppConfig.DB_TEMP_SUFFIX)){
					f.delete() ;
				}
			}
		}
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
	
	public static String getLocalDbPath(String dbname){
		return getLocaPath(dbname + ".db");
	}
	
	public static String getLocalDbTempPath(String dbname){
		return getLocaPath(dbname + AppConfig.DB_TEMP_SUFFIX);
	}
	
	public static String getLocaPath(String fileName){
		return "/data/data/" + AppCRTBApplication.getAppContext().getPackageName() + "/databases/" + fileName;
	}
	
	public static boolean checkProjectIndex(Context context,String name){
		
		List<File> list = CrtbDbFileUtils.getLocalDbFiles(context) ;
		
		for(File f : list){
			
			String fn	= f.getName() ;
			String sn 	= fn.substring(0, fn.lastIndexOf("."));
			
			if(sn.equals(name)){
				return true;
			}
		}
		
		return false ;
	}
	
	public static void exportDb(String path,
			final String filename,AppHandler handler){
		
		if(path == null || filename == null || handler == null){
			return ;
		}
		
		ExecuteAsyncTaskImpl.defaultSyncExecutor().executeTask(new BaseAsyncTask(handler) {
			
			@Override
			public void process() {
				
				// 导出路径
				String outPath = getExportPath(AppConfig.DB_EXPORT_DIR) + "/" + filename + ".dtmsdb";
				
				// 目标路径
				String srcPath		= null ;
				IDbEncrypt encrypt 	= null ;
				
				// 存在临时文件: 当前工作面
				if(checkDbTemp(filename)){
					srcPath	= getLocalDbTempPath(filename);
					encrypt	= new DbAESEncrypt() ;
				} else {
					srcPath	=  getLocalDbPath(filename);
					encrypt	= new DbNoneEncrypt() ;
				}
				
				try {
					
					// 加密(加密|文件拷贝)
					boolean success = encrypt.encrypt(srcPath, outPath) ;
					
					Thread.sleep(2000);
					
					if(success){
						sendMessage(MSG_EXPORT_DB_SUCCESS,"已经将工作面(" + filename + ")" + "成功导出到: " + outPath) ;
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
	
	/**
	 * 导入数据库
	 * 
	 * @param context
	 * @param path
	 * @param handler
	 */
	public static void importDb(final Context context,final List<ProjectIndex> oldList,
			final String path,
			AppHandler handler){
		
		if(context == null || path == null || handler == null){
			return ;
		}
		
		ExecuteAsyncTaskImpl.defaultSyncExecutor().executeTask(new BaseAsyncTask(handler) {
			
			@Override
			public void process() {
				
				// 为了防止用户修改文件名称
				//////////////////////////////////////////打开导入数据库//////////////////////////////
				
				// 导入的临时文件名称
				String importName = "import_temp" ;
				
				// 解密器(文件复制)
				IDbEncrypt encrypt 	= new DbNoneEncrypt();
				String outPath 		= getLocalDbPath(importName);
				String outTempPath 	= getLocalDbTempPath(importName);
				
				boolean success 	= encrypt.decrypt(path, outPath) ;
				
				if(!success){
					sendMessage(MSG_INPORT_DB_FAILED,"文件解密失败") ;
					return ;
				}
				
				// 打开工作面
				String[] info 	= CrtbDbFileUtils.openDbFile(importName);
				
				if(info == null){
					sendMessage(MSG_INPORT_DB_FAILED,"工作面导入失败!") ;
					return ;
				}
				
				// 打开数据库
				final IAccessDatabase db = FrameworkFacade.getFrameworkFacade().openDatabaseByName(importName + ".temp", 0);
				
				// 检测是否存在相同的工作面
				
				if(db == null){
					sendMessage(MSG_INPORT_DB_FAILED,"打开数据库失败!") ;
					return ;
				}
				
				String sql = "select * from ProjectIndex" ;
				
				ProjectIndex obj = db.queryObject(sql, null, ProjectIndex.class);
				
				if(obj == null){
					sendMessage(MSG_INPORT_DB_FAILED,"导入数据库中没有工作面，无法导入") ;
					return ;
				}
				
				// 清除缓存
				FrameworkFacade.getFrameworkFacade().removeDatabaseByName(importName + ".temp");
				
				// 密码文件
				File file = new File(outPath);
				file.delete() ;
				
				// 解密文件
				file = new File(outTempPath);
				file.delete() ;
				
				// 是否存在相同工作面
				if(oldList != null){
					
					for(ProjectIndex pro : oldList){
						if(pro.getProjectName().equals(obj.getProjectName())){
							sendMessage(MSG_INPORT_DB_FAILED,"已经存在相同工作面") ;
							return ;
						}
					}
				}
				
				//////////////////////////////////正式导入////////////////////////////////
				
				// 导入的数据库名称
				String dbName 		= obj.getProjectName() ;
				String newPath 		= getLocalDbPath(dbName);
				
				// 文件重新复制
				success 	= encrypt.decrypt(path, newPath) ;
				
				if(!success){
					sendMessage(MSG_INPORT_DB_FAILED,"文件解密失败") ;
					return ;
				}
				
				// 导入的文件
				file = new File(newPath);
				
				// 写入数据库
				int code = ProjectIndexDao.defaultWorkPlanDao().importDb(obj) ;
				
				if(code == ProjectIndexDao.DB_EXECUTE_FAILED){
					
					file.delete() ;
					
					sendMessage(MSG_INPORT_DB_FAILED,"数据库导入失败") ;
					
				} else if(code == 100){
					
					file.delete() ;
					
					sendMessage(MSG_INPORT_DB_FAILED,"试用版用户, 最多只能有1个工作面") ;
					
				} else {
					
					AppLogger.d(TAG, "zhouwei : 数据库导入完成: " + outPath); 
					
					// 导入完成
					sendMessage(MSG_INPORT_DB_SUCCESS) ;
				}
			}
		}) ;
	}
	
	/**
	 * 数据库临时文件是否存在
	 * 
	 * @param dbName
	 * @return
	 */
	public static boolean checkDbTemp(String dbName){
		
		if(StringUtils.isEmpty(dbName)){
			return false ;
		}
		
		String path = getLocalDbTempPath(dbName);
		
		File file = new File(path);
		
		return file.exists() ;
	}
	
	/**
	 * 打开数据库 
	 * @param dbName
	 * @return
	 */
	public static String[] openDbFile(String dbName){
		
		if(StringUtils.isEmpty(dbName)){
			return null ;
		}
		
		try{
			
			// 数据库名称
			int pos 	= dbName.lastIndexOf(".");
			String name = dbName ;
			
			if(pos > 0){
				name	= dbName.substring(0, pos);	
			}
			
			// 加密文件
			String srcPath	= CrtbDbFileUtils.getLocalDbPath(name);
			
			// 解密后的临时文件
			String tempPath = CrtbDbFileUtils.getLocalDbTempPath(name);
			
			IDbEncrypt encrypt 	= new DbAESEncrypt() ;
			
			// 解密
			boolean noerror = encrypt.decrypt(srcPath, tempPath) ;
			
			return noerror ? new String[]{name,tempPath} : null ;
			
		} catch(Exception e){
			e.printStackTrace() ;
		}

		return null ;
	}
	
	/**
	 * 关闭数据库文件并生成加密文件
	 * 
	 * @param dbName
	 */
	public static String[] closeDbFile(String dbName){
		
		if(StringUtils.isEmpty(dbName)){
			return null ;
		}
		
		try{
			
			// 数据库名称
			int pos 	= dbName.lastIndexOf(".");
			String name = dbName ;
			
			if(pos > 0){
				name	= dbName.substring(0, pos);	
			}
			
			// sqlite 文件
			String srcPath	= CrtbDbFileUtils.getLocalDbTempPath(name);
			
			// 加密后的文件
			String desPath	= CrtbDbFileUtils.getLocalDbPath(name);
			
			// 删除旧的加密文件
			File desFile = new File(desPath);
			desFile.delete() ;
			
			// 加密
			IDbEncrypt encrypt 	= new DbAESEncrypt() ;
			boolean noerror 	= encrypt.encrypt(srcPath, desPath) ;
			
			// 删除原始文件
			if(noerror){
				File f = new File(srcPath);
				f.delete() ;
			}
			
			return noerror ? new String[]{dbName,desPath} : null ;
			
		} catch(Exception e){
			e.printStackTrace() ;
		}
		
		return null ;
	}
}
