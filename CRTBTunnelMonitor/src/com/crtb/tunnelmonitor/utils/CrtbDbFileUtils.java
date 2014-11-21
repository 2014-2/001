package com.crtb.tunnelmonitor.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.impl.ExecuteAsyncTaskImpl;
import org.zw.android.framework.impl.FrameworkFacade;
import org.zw.android.framework.util.DateUtils;
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
import com.crtb.tunnelmonitor.dao.impl.v2.AbstractDao;
import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.entity.CrtbUser;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;

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
				
				File backup = new File(crtb, AppConfig.DB_SDCARD_BACKUP);
				
				if(!backup.isDirectory()){
					backup.mkdir() ;
				}
				
				File all = new File(crtb, AppConfig.DB_SDCARD_BACKUP_ALL);
				
				if(!all.isDirectory()){
					all.mkdir() ;
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
		return getLocaPath(dbname + AppConfig.DB_SUFFIX);
	}
	
	public static String getLocalDbTempPath(String dbname){
		return getLocaPath(dbname + AppConfig.DB_TEMP_SUFFIX);
	}
	
	public static String getLocalDbBackupPath(String dbname){
		return getLocaPath(dbname + AppConfig.DB_SUFFIX_BACKUP);
	}
	
	public static String getExternalBackupPath(String dbname){
		
		String date = DateUtils.toDateString(DateUtils.getCurrtentTimes(),DateUtils.DATE_FORMAT) ;
		String path = getExportPath(AppConfig.DB_SDCARD_BACKUP) + "/" + dbname+ "-" + date + ".dtmsdb" ;
		
		File f = new File(path);
		
		if(f.exists()){
			f.delete() ;
		}
		
		return path ;
	}
	
	public static String getExternalBackupAllPath(String dbname){
		
		String date = DateUtils.toDateString(DateUtils.getCurrtentTimes(),DateUtils.DATE_FORMAT) ;
		String path = getExportPath(AppConfig.DB_SDCARD_BACKUP_ALL) + "/" + dbname+ "-" + date + ".dtmsdb" ;
		
		File f = new File(path);
		
		if(f.exists()){
			f.delete() ;
		}
		
		return path ;
	}
	
	public static String getDbName(String dbname){
		
		if(dbname == null) return "" ;
		
		int pos = dbname.lastIndexOf(AppConfig.DB_SUFFIX);
		
		if(pos > 0) return dbname.substring(0, pos) ;
		
		return dbname ;
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
				
				final int currentUserType	= AppCRTBApplication.getInstance().getCurUserType() ;
				
				// 导入临时数据库名称
				final String tempDbName = importName + AppConfig.DB_TEMP_SUFFIX ;
				
				// 打开数据库
				final IAccessDatabase db = FrameworkFacade.getFrameworkFacade().openDatabaseByName(tempDbName, 0);
				
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
				
				int maxSection = currentUserType == CrtbUser.LICENSE_TYPE_REGISTERED ? 1000 : AbstractDao.TRIAL_USER_MAX_SECTION_COUNT ;
				
				// 判断导入的隧道内工作面
				sql = "select * from TunnelCrossSectionIndex" ;
				
				List<TunnelCrossSectionIndex> tl = db.queryObjects(sql, TunnelCrossSectionIndex.class);
				
				if(tl != null && tl.size() > maxSection){
					sendMessage(MSG_INPORT_DB_FAILED,"试用版用户,不能导入超过10个断面的工作面") ;
					return ;
				}
				
				// 判断导入的隧道下沉工作面
				sql = "select * from SubsidenceCrossSectionIndex" ;
				
				List<SubsidenceCrossSectionIndex> sl = db.queryObjects(sql, SubsidenceCrossSectionIndex.class);
				
				if(sl != null && sl.size() > maxSection){
					sendMessage(MSG_INPORT_DB_FAILED,"试用版用户,不能导入超过10个断面的工作面") ;
					return ;
				}
				
				// 清除缓存
				FrameworkFacade.getFrameworkFacade().removeDatabaseByName(tempDbName);
				
				// 密码文件
				File file = new File(outPath);
				file.delete() ;
				
				// 解密文件
				file = new File(outTempPath);
				file.delete() ;
				
				// 是否存在相同工作面
				if(oldList != null){
					
					for(ProjectIndex pro : oldList){
						
						String on	= pro.getProjectName().toLowerCase();
						String nn	= obj.getProjectName().toLowerCase() ;
						
						if(on.equals(nn)){
							sendMessage(MSG_INPORT_DB_FAILED,"已经存在相同工作面") ;
							return ;
						}
					}
				}
				
				//////////////////////////////////正式导入////////////////////////////////
				
				// 导入的数据库名称
				String dbName 		= obj.getProjectName() ;
				String newPath 		= getLocalDbPath(dbName);
				String bpPath		= getLocalDbBackupPath(dbName);
				
				// 文件解密
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
					
					// 生成备份文件
					File src = new File(newPath);
					File des	= new File(bpPath);
					fileCopy(src, des);
					
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
			String name = getDbName(dbName) ;
			
			// 1. 加密文件
			String srcPath	= CrtbDbFileUtils.getLocalDbPath(name);
			
			// 2. 加密文件是否存在
			File srcFile = new File(srcPath);
			if(!srcFile.exists()){
				
				System.out.println("zhouwei >> openDbFile-->加密文件不存在");
				
				// 3.备份文件是否存在
				String bpPath = CrtbDbFileUtils.getLocalDbBackupPath(name);
				srcFile = new File(bpPath);
				
				// 4. 如果加密文件与备份文件都不存在,则打开数据库失败
				if(!srcFile.exists()){
					System.out.println("zhouwei >> openDbFile-->备份文件不存在");
					return null ;
				}
				
				// 5. 通过备份文件生成.db文件
				fileCopy(srcFile, new File(srcPath));
				
				// 指向备份文件
				srcPath = bpPath ;
			}
			
			// 加密数据库文件是否存在
			srcFile = new File(srcPath);
			if(!srcFile.exists()){
				System.out.println("zhouwei ERROR : 数据库文件不存在-->" + srcFile);
				return null ;
			}
			
			// 6. 解密后的临时文件
			String tempPath = CrtbDbFileUtils.getLocalDbTempPath(name);
			
			// 7. 删除临时文件
			File f = new File(tempPath);

			if(f != null && f.exists()){			
				if(tempPath.endsWith("import_temp.bin")){
					//YX 如果是上次导入的记录，则删除数据
					f.delete() ;
				} else {
					//YX 非正常退出后，把原文件删除后，可能导致最后的更新没有写入到数据库问题
					return new String[]{name,tempPath};
				}
			}
			
			// 解密
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
	public static String[] closeDbAndEncrypt(String dbName,IAccessDatabase db,boolean removeBin){
		
		if(StringUtils.isEmpty(dbName)){
			return null ;
		}
		
		// 数据库名称
		String name 	= getDbName(dbName);

		// xx.bin 文件
		String binPath 	= CrtbDbFileUtils.getLocalDbTempPath(name);

		// xx.db 加密后的文件
		String desPath 	= CrtbDbFileUtils.getLocalDbPath(name);

		// xx.dbbp 备份文件
		String bpPath 	= CrtbDbFileUtils.getLocalDbBackupPath(name);
		
		try{
			
			// 临时文件
			File binF = new File(binPath);
			if(!binF.exists()){
				System.out.println("zhouwei >> <<<<<加密临时文件不存在>>>>>");
				return null ;
			}
			
			// 1. 生成备份文件
			File src = new File(desPath);
			File des = new File(bpPath);
			if(src.exists()){
				fileCopy(src, des);
			}
			
			// 2. 删除旧的加密文件
			src.delete() ;
			
			// 3. 加密并生成新文件
			IDbEncrypt encrypt 	= new DbAESEncrypt() ;
			boolean noerror 	= encrypt.encrypt(binPath, desPath) ;
			
			// 加密失败
			if(!noerror){
				
				// 删除加密文件
				File f = new File(desPath);
				f.delete();
				
				// 从备份文件拷贝到加密文件(恢复加密文件)
				src = new File(bpPath);
				des = new File(desPath);
				if(src.exists()){
					fileCopy(src, des);
				}
				
				return null ;
			}
			
			// 删除临时文件(非加密文件)
			if(removeBin){
				binF.delete() ;
			}
			
			// 4. 如果加密成功,重新生成备份文件
			src = new File(desPath);
			des = new File(bpPath);
			if(src.exists()){
				fileCopy(src, des);
			}
			
			// 备份到外部存储卡
			if(db != null){
				
				List<ProjectIndex> list = db.queryObjects("select * from ProjectIndex", ProjectIndex.class);
			
				if(list != null && !list.isEmpty()){
					
					// 外部备份路径
					String exbpPath	= CrtbDbFileUtils.getExternalBackupPath(name);
					
					src = new File(desPath);
					des = new File(exbpPath);
					
					fileCopy(src, des);
				}
			}
			
			return noerror ? new String[]{dbName,desPath} : null ;
			
		} catch(Exception e){
			e.printStackTrace() ;
			
			System.out.println("zhouwei >> <<<<<加密文件失败>>>>>");
			
			// 如果加密文件失败
			// 通过备份文件
			File src = new File(bpPath);
			File des = new File(desPath);
			
			fileCopy(src, des);
		}
		
		return null ;
	}
	
	public static boolean fileCopy(File src, File des){
		
		if(src == null || des == null) return false ;
		
		FileInputStream fi 	= null;
		FileOutputStream fo = null;
		FileChannel in 		= null;
		FileChannel out 	= null;

		try {

			long size = src.length() ;
			
			fi = new FileInputStream(src);
			fo = new FileOutputStream(des);
			
			in = fi.getChannel();// 得到对应的文件通道
			out = fo.getChannel();// 得到对应的文件通道
			
			// 文件复制
			return size == in.transferTo(0, in.size(), out) ;
			
		} catch (IOException e) {
			e.printStackTrace();
			
			System.out.println("zhouwei >> <<<<<生成备份文件失败>>>>>");
		} finally {
			
			try {
				
				fi.close();
				in.close();
				fo.close();
				out.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return false ;
	}
}
