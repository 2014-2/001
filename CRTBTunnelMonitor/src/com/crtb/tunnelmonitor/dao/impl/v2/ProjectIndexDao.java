package com.crtb.tunnelmonitor.dao.impl.v2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.impl.ExecuteAsyncTaskImpl;
import org.zw.android.framework.util.StringUtils;

import android.content.Context;
import android.util.Log;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.AppConfig;
import com.crtb.tunnelmonitor.AppHandler;
import com.crtb.tunnelmonitor.AppPreferences;
import com.crtb.tunnelmonitor.BaseAsyncTask;
import com.crtb.tunnelmonitor.MessageDefine;
import com.crtb.tunnelmonitor.entity.CrtbProject;
import com.crtb.tunnelmonitor.entity.CrtbUser;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.utils.CrtbDbFileUtils;

/**
 * 工程信息DAO(工作面)
 * 
 * @author zhouwei
 *
 */
public final class ProjectIndexDao extends AbstractDao<ProjectIndex> {
	
	static String TAG = "ProjectIndexDao" ;
	
	private static ProjectIndexDao _instance ;

	private ProjectIndexDao(){
		
	}
	
	public static ProjectIndexDao defaultWorkPlanDao(){
		
		if(_instance == null){
			_instance	= new ProjectIndexDao() ;
		}
		
		return _instance ;
	}
	
	public List<ProjectIndex> queryAllWorkPlan(){
		
		final IAccessDatabase mDatabase = getDefaultDb() ;
		final CrtbUser	user = CrtbLicenseDao.defaultDao().queryCrtbUser() ;
		
		if(user == null){
			Log.e("AbstractDao", "zhouwei : crtb user is null");
			return null ;
		}
		
		String sql = "select * from CrtbProject" ;
		
		List<CrtbProject> list = mDatabase.queryObjects(sql, CrtbProject.class);
		
		if(list == null){
			return null ;
		}
		
		List<ProjectIndex> temp = new ArrayList<ProjectIndex>();
		
		for(CrtbProject p : list){
			
			ProjectIndex pro = new ProjectIndex() ;
			
			pro.setId(p.getProjectId());
			pro.setProjectName(p.getProjectName());
			pro.setCreateTime(p.getCreateTime());
			pro.setStartChainage(p.getStartChainage());
			pro.setEndChainage(p.getEndChainage());
			pro.setLastOpenTime(p.getLastOpenTime());
			pro.setInfo(p.getInfo());
			
			pro.setChainagePrefix(p.getChainagePrefix());
			
			pro.setGDLimitVelocity(p.getGDLimitVelocity());
			pro.setGDLimitTotalSettlement(p.getGDLimitTotalSettlement());
			
			pro.setDBLimitVelocity(p.getDBLimitVelocity());
			pro.setDBLimitTotalSettlement(p.getDBLimitTotalSettlement());
			
			pro.setSLLimitTotalSettlement(p.getSLLimitTotalSettlement());
			pro.setSLLimitVelocity(p.getSLLimitVelocity());
			
			pro.setConstructionFirm(p.getConstructionFirm());
			pro.setLimitedTotalSubsidenceTime(p.getLimitedTotalSubsidenceTime());
			
			temp.add(pro);
		}
		
		return temp ;
	}
	
	/**
	 * 得到当前工作面: 异步调用(流程: 数据库文件解密->打开数据库->查询工作面)
	 * @return
	 */
	public ProjectIndex queryEditWorkPlan(){
		
		// 当前项目名称
		final IAccessDatabase db = getCurrentDb();
		
		if(db == null){
			
			Log.e("AbstractDao", "zhouwei : 数据库null");
			
			return null ;
		}
		
		String sql = "select * from ProjectIndex" ;
		
		return db.queryObject(sql, null,ProjectIndex.class);
	}
	
	public String getCurrentWorkDbPath(){
		
		// 当前项目名称
		final IAccessDatabase db = getCurrentDb();

		if (db == null) {
			return null;
		}
		
		return db.getDatabasePath() ;
	}
	
	public void removeProjectIndex(ProjectIndex bean){
		
		String dbName = getDbUniqueName(bean.getProjectName());
		
		String name = AppPreferences.getPreferences().getCurrentProject() ;
		
		if(name.equals(dbName)){
			AppPreferences.getPreferences().removeCurrentProject();
		}
	}
	
	/**
	 * 备份数据库
	 * 
	 * @param context
	 * @param handler
	 */
	public final void backupAllDb(final Context context,AppHandler handler){
		
		ExecuteAsyncTaskImpl.defaultSyncExecutor().executeTask(new BaseAsyncTask(handler) {

			@Override
			public void process() {
				
				// 关闭当前数据库
				String dbName = AppPreferences.getPreferences().getCurrentProject();
				if(!StringUtils.isEmpty(dbName)){
					
					// 对应的数据库名称与缓存数据库
					dbName 			= CrtbDbFileUtils.getDbName(dbName);
					String dbTemp 	= getDbUniqueTempName(dbName);
					
					// 关闭文件并重新加密
					String[] info = CrtbDbFileUtils.closeDbAndEncrypt(dbName,mFramework.getDatabaseByName(dbTemp),false);
					
					if(info == null){
						sendMessage(MessageDefine.MSG_BACKUP_HINT, dbName + "备份失败");
					}
				}
				
				// 备份所有加密文件
				List<File> fs = CrtbDbFileUtils.getLocalDbFiles(context);
				
				for(File f : fs){
					
					String fn = f.getName() ;
					
					// 数据库后缀名称
					if(fn.length() > 3){
						
						String suffix = fn.substring(fn.length() - 3, fn.length());
						
						if(suffix.equals(AppConfig.DB_SUFFIX)){
							
							// 备份文件名
							dbName = fn.substring(0, fn.lastIndexOf(AppConfig.DB_SUFFIX));
							String src = f.getAbsolutePath() ;
							String des = CrtbDbFileUtils.getExternalBackupAllPath(dbName);
							
							// 文件拷贝
							if(!CrtbDbFileUtils.fileCopy(new File(src), new File(des))){
								sendMessage(MessageDefine.MSG_BACKUP_HINT, dbName + "备份失败");
							}
						}
					}
				}
				
				sendMessage(MessageDefine.MSG_BACKUP_SUCCESS);
			}
			
		});
	}
	
	// 打开工作面
	public String openProjectIndex(String projectName){
		
		if(StringUtils.isEmpty(projectName)){
			return "不能打开无效工作面" ;
		}
		
		// 需要打开的数据库
		/////////////// 数据库名称
		final String dbName 	= projectName;
		
		// 临时数据库名称
		final String tempName 	= getDbUniqueTempName(dbName);
		
		// 数据库是否存在
		IAccessDatabase db 		= mFramework.getDatabaseByName(tempName) ;
		
		// 如果数据库已经打开过，直接返回(避免重复打开数据库)
		if(db != null){
			System.out.println("zhouwei >> 数据库文件已经打开 ");
			return null;
		}
		
		// 打开数据库
		String[] info = CrtbDbFileUtils.openDbFile(dbName);

		if (info == null) {
			return "数据库解密失败";
		}
		
		// 打开对应的数据库
		db = mFramework.openDatabaseByName(tempName, 0) ;
		
		if(db == null){
			System.out.println("zhouwei ERROR : openProjectIndex-> 打开数据库失败: " + dbName);
			return "打开数据库失败" ;
		}
		
		/////////////////////// 关闭上一个工作面 /////////////////////////
		// 上一个工作面的名称
		String lastName = AppPreferences.getPreferences().getCurrentProject();
		
		if (!StringUtils.isEmpty(lastName)) {
			
			lastName = CrtbDbFileUtils.getDbName(lastName);
			
			if(!lastName.equals(dbName)){
				closeCurrentDb() ;
			}
		}
		
		/////////////////////// 设置当前工作面  /////////////////////////
		AppPreferences.getPreferences().putCurrentProject(dbName);
		
		return null ;
	}
	
	public boolean hasWorkPlan(){
		
		List<ProjectIndex> list = queryAllWorkPlan() ;
		
		return list != null && list.size() > 0 ;
	}

    public boolean hasExport() {
        int userType = AppCRTBApplication.getInstance().getCurUserType();
        return userType == CrtbUser.LICENSE_TYPE_DEFAULT ? false
                : hasWorkPlan();
    }
    
    // 导入数据库
	public final int importDb(ProjectIndex obj){
		
		final CrtbUser user = CrtbLicenseDao.defaultDao().queryCrtbUser() ;
		
		if(user == null || obj == null){
			return DB_EXECUTE_FAILED ;
		}

        int limitProject 	= 0;
        int userType = AppCRTBApplication.getInstance().getCurUserType();
        
        if (userType == CrtbUser.LICENSE_TYPE_DEFAULT) {
            Log.d(TAG, "未授权用户, 不能创建工作面！");
            return DB_EXECUTE_FAILED;
        } else if (userType == CrtbUser.LICENSE_TYPE_TRIAL) {
        	limitProject = TRIAL_USER_MAX_PROJECTINDEX_COUNT;
        } else if (userType == CrtbUser.LICENSE_TYPE_REGISTERED) {
        	limitProject = -1;
        }

		// 非注册用户工作面判断
		if (limitProject != -1) {
			
			String sql = "select * from CrtbProject" ;
			
			List<CrtbProject> list = getDefaultDb().queryObjects(sql, CrtbProject.class);
			
			if(list != null && list.size() >= limitProject){
				Log.e(TAG, "error : 试用版用户,不能保存" + limitProject + "个以上的工作面");
				return 100 ;
			}
		}
		
		// 保存到管理数据库
		
		CrtbProject pro = new CrtbProject() ;
		pro.setUsername(user.getUsername());
		
		// 第一次保存工作面对应的数据库名称
		pro.setDbName(obj.getProjectName());
		
		pro.setProjectId(obj.getId());
		pro.setProjectName(obj.getProjectName());
		pro.setCreateTime(obj.getCreateTime());
		pro.setStartChainage(obj.getStartChainage());
		pro.setEndChainage(obj.getEndChainage());
		pro.setLastOpenTime(obj.getLastOpenTime());
		pro.setInfo(obj.getInfo());
		
		pro.setChainagePrefix(obj.getChainagePrefix());
		
		pro.setGDLimitVelocity(obj.getGDLimitVelocity());
		pro.setGDLimitTotalSettlement(obj.getGDLimitTotalSettlement());
		
		pro.setSLLimitVelocity(obj.getSLLimitVelocity());
		pro.setSLLimitTotalSettlement(obj.getSLLimitTotalSettlement());
		
		pro.setDBLimitVelocity(obj.getDBLimitVelocity());
		pro.setDBLimitTotalSettlement(obj.getDBLimitTotalSettlement());
		
		pro.setConstructionFirm(obj.getConstructionFirm());
		pro.setLimitedTotalSubsidenceTime(obj.getLimitedTotalSubsidenceTime());
		
		// 保存工作面到默认数据库中
		return getDefaultDb().saveObject(pro) > 0 ?  DB_EXECUTE_SUCCESS : DB_EXECUTE_FAILED ;
	}

	/**
	 * 新建工作面
	 * 
	 * @param bean
	 * @param mHandler
	 * @return
	 */
	public int insertNewProjectIndex(ProjectIndex bean) {
		
		final CrtbUser user = CrtbLicenseDao.defaultDao().queryCrtbUser() ;
		
		if(user == null){
			return DB_EXECUTE_FAILED ;
		}

        int limit = 0;
        int userType = AppCRTBApplication.getInstance().getCurUserType();
        if (userType == CrtbUser.LICENSE_TYPE_DEFAULT) {
            Log.d(TAG, "未授权用户, 不能创建工作面！");
            return DB_EXECUTE_FAILED;
        } else if (userType == CrtbUser.LICENSE_TYPE_TRIAL) {
            limit = TRIAL_USER_MAX_PROJECTINDEX_COUNT;
        } else if (userType == CrtbUser.LICENSE_TYPE_REGISTERED) {
            limit = -1;
        }

        // 非注册用户
        if (limit != -1) {

            String sql = "select * from CrtbProject where username = ?";

            List<CrtbProject> list = getDefaultDb().queryObjects(sql,
                    new String[] { user.getUsername() }, CrtbProject.class);

            if (list != null && list.size() >= limit) {
                Log.e(TAG, "error : 试用版用户,不能保存" + limit + "个以上的工作面");
                return 100;
            }
        }
        
        // 保存工作面(临时文件)
        String dbName 	= bean.getProjectName() ;
        String dbTemp	= getDbUniqueTempName(dbName) ;
        
        // 生成新的数据库文件
		final IAccessDatabase db = mFramework.openDatabaseByName(dbTemp, 0);
		
		if(db == null){
			return DB_EXECUTE_FAILED;
		}
		
		// 初始化表
		loadAllTable(db);
		
		// 保存工作面
		int code = db.saveObject(bean) ;
		
		// 保存到对应的数据库
		if(code > 0){
			
			ProjectIndex obj = db.queryObject("select * from ProjectIndex where ProjectName = ? ", new String[]{dbName}, ProjectIndex.class) ;
			
			CrtbProject pro = new CrtbProject() ;
			pro.setUsername(user.getUsername());
			
			// 第一次保存工作面对应的数据库名称
			pro.setDbName(dbName);
			
			pro.setProjectId(obj.getId());
			pro.setProjectName(dbName);
			pro.setCreateTime(obj.getCreateTime());
			pro.setStartChainage(obj.getStartChainage());
			pro.setEndChainage(obj.getEndChainage());
			pro.setLastOpenTime(obj.getLastOpenTime());
			pro.setInfo(obj.getInfo());
			
			pro.setChainagePrefix(obj.getChainagePrefix());
			
			pro.setGDLimitVelocity(obj.getGDLimitVelocity());
			pro.setGDLimitTotalSettlement(obj.getGDLimitTotalSettlement());
			
			pro.setSLLimitVelocity(obj.getSLLimitVelocity());
			pro.setSLLimitTotalSettlement(obj.getSLLimitTotalSettlement());
			
			pro.setDBLimitVelocity(obj.getDBLimitVelocity());
			pro.setDBLimitTotalSettlement(obj.getDBLimitTotalSettlement());
			
			pro.setConstructionFirm(obj.getConstructionFirm());
			pro.setLimitedTotalSubsidenceTime(obj.getLimitedTotalSubsidenceTime());
			
			code = getDefaultDb().saveObject(pro) ;
			
			// 保存失败
			if(code < 0){
				Log.e(TAG, "error :保存工作面失败 ");
				db.execute("delete from ProjectIndex where Id = ", new String[]{String.valueOf(obj.getId())});
				return DB_EXECUTE_FAILED ;
			}
			
			// 关闭数据库文件，并生成加密文件
			String[] info = closeDatabase(dbName);
			
			if(info != null){
				return DB_EXECUTE_SUCCESS ;
			}
		}
		
		return DB_EXECUTE_FAILED ;
	}

	/**
	 * 更新工作面
	 * 
	 * @param bean
	 * @param mHandler
	 * @return
	 */
	@Override
	public int update(ProjectIndex bean) {
		
		if(bean == null || bean.getProjectName() == null){
			return DB_EXECUTE_FAILED ;
		}

		// 更新工作面
        String dbName 	= bean.getProjectName() ;
        String dbTemp	= getDbUniqueTempName(dbName) ;
        
        // 打开数据库
        String error 	= openProjectIndex(dbName);
        
        if(error != null){
        	System.out.println("zhouwei >> ERROR : 数据库打开失败 ");
        	return DB_EXECUTE_FAILED ;
        }
        
        // 得到对应的数据库对象
        final IAccessDatabase db = mFramework.getDatabaseByName(dbTemp);
		
		if(db == null){
			return DB_EXECUTE_FAILED;
		}
        
		try{
			
			// 保存到对应的数据库
			if(db.updateObject(bean) > 0){
				
				String sql 		= "select * from CrtbProject where dbName = ? " ;
				CrtbProject pro = getDefaultDb().queryObject(sql, new String[]{dbName}, CrtbProject.class) ;
				
				pro.setCreateTime(bean.getCreateTime());
				pro.setStartChainage(bean.getStartChainage());
				pro.setEndChainage(bean.getEndChainage());
				pro.setLastOpenTime(bean.getLastOpenTime());
				pro.setInfo(bean.getInfo());
				
				pro.setChainagePrefix(bean.getChainagePrefix());
				
				pro.setGDLimitVelocity(bean.getGDLimitVelocity());
				pro.setGDLimitTotalSettlement(bean.getGDLimitTotalSettlement());
				
				pro.setSLLimitVelocity(bean.getSLLimitVelocity());
				pro.setSLLimitTotalSettlement(bean.getSLLimitTotalSettlement());
				
				pro.setDBLimitVelocity(bean.getDBLimitVelocity());
				pro.setDBLimitTotalSettlement(bean.getDBLimitTotalSettlement());
				
				pro.setConstructionFirm(bean.getConstructionFirm());
				pro.setLimitedTotalSubsidenceTime(bean.getLimitedTotalSubsidenceTime());
				
				int code = getDefaultDb().updateObject(pro) ;
				
				// 更新失败
				if(code < 0){
					Log.e(TAG, "error :更新工作面失败 ");
					return DB_EXECUTE_FAILED;
				}
				
				// 关闭数据库
				closeDatabase(dbName);
				
				return DB_EXECUTE_SUCCESS ;
			}
			
		} catch(Exception e){
			e.printStackTrace() ;
		}
	
		return DB_EXECUTE_FAILED ;
	}

	@Override
	public int delete(ProjectIndex bean) {
		
		if(bean == null || bean.getProjectName() == null){
			return DB_EXECUTE_FAILED ;
		}
		
		String proName	= bean.getProjectName() ;
		String dbTemp	= getDbUniqueTempName(proName) ;
		
		boolean error = getDefaultDb().execute("delete from CrtbProject where ProjectName = ? ", new String[]{proName}) ;
		
		if(error){
			System.out.println("zhouwei >> 删除工作面失败");
		}
		
		// 删除数据库缓存
		mFramework.removeDatabaseByName(dbTemp);
		
		// 删除原始文件
		String srcPath = CrtbDbFileUtils.getLocalDbPath(proName);
		File file = new File(srcPath);
		file.delete();
		
		// 临时文件
		srcPath = CrtbDbFileUtils.getLocalDbTempPath(proName);
		if(srcPath != null){
			file = new File(srcPath);
			file.delete();
		}
		
		// 删除备份文件
		
		return DB_EXECUTE_SUCCESS ;
	}
	
}
