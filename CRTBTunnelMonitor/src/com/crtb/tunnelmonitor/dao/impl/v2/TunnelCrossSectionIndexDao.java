package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.db.core.SQLiteParamUtils;
import org.zw.android.framework.impl.ExecuteAsyncTaskImpl;

import android.text.TextUtils;
import android.util.Log;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.AppHandler;
import com.crtb.tunnelmonitor.BaseAsyncTask;
import com.crtb.tunnelmonitor.entity.CrtbUser;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;

/**
 * 隧道内断面DAO
 * 
 * @author zhouwei
 *
 */
public final class TunnelCrossSectionIndexDao extends AbstractDao<TunnelCrossSectionIndex> {

	private static TunnelCrossSectionIndexDao _instance ;
	
	private TunnelCrossSectionIndexDao(){
		
	}
	
	public static TunnelCrossSectionIndexDao defaultDao(){
		
		if(_instance == null){
			_instance	= new TunnelCrossSectionIndexDao() ;
		}
		
		return _instance ;
	}

    public int insertOrUpdate(TunnelCrossSectionIndex bean) {
        if (bean == null) {
            return -1;
        }
        TunnelCrossSectionIndex obj = querySectionIndexByChainage(bean.getChainage());
        if (obj != null) {
            update(bean);
            return -1;
        } else {
            int result = insert(bean);
            if (result != DB_EXECUTE_SUCCESS) {
            	return -1;
            } else {
                return bean.getID();
            }
        }
    }

    @Override
    public int insert(TunnelCrossSectionIndex bean) {

        int sectionCountLimit = 0;

        int userType = AppCRTBApplication.getInstance().getCurUserType();

        if (userType == CrtbUser.LICENSE_TYPE_DEFAULT) {
            Log.d(TAG, "未授权用户, 不能创建断面！");
            return DB_EXECUTE_FAILED;
        } else if (userType == CrtbUser.LICENSE_TYPE_TRIAL) {
            sectionCountLimit = TRIAL_USER_MAX_SECTION_COUNT;
        } else if (userType == CrtbUser.LICENSE_TYPE_REGISTERED) {
            sectionCountLimit = -1;
        }

        // 非注册用户
        if (sectionCountLimit != -1) {

            String sql = "select * from TunnelCrossSectionIndex limit ?,?";

            final IAccessDatabase mDatabase = getCurrentDb();

            List<TunnelCrossSectionIndex> list = mDatabase.queryObjects(sql,
                    new String[] { String.valueOf(0), String.valueOf(sectionCountLimit) },
                    TunnelCrossSectionIndex.class);

            if (list != null && list.size() >= sectionCountLimit) {
                Log.e(TAG, "error : 试用版用户, 不能保存" + sectionCountLimit + "个以上的断面");
                return 100;
            }
        }

        return super.insert(bean);
    }

	public void queryAllSection(AppHandler handler){
		
		ExecuteAsyncTaskImpl.defaultSyncExecutor().executeTask(new BaseAsyncTask(handler) {
			
			@Override
			public void process() {
				
				List<TunnelCrossSectionIndex> list = queryAllSection() ;
				
				if(list != null){
					sendMessage(MSG_QUERY_SECTION_SUCCESS, list);
				}
			}
		}) ;
	}
	
	public List<TunnelCrossSectionIndex> queryAllSection(){
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null;
		}
		
		String sql = "select * from TunnelCrossSectionIndex ORDER BY Chainage ASC" ;
		
		return mDatabase.queryObjects(sql, TunnelCrossSectionIndex.class) ;
	}
	
	/**
	 * 得到使用指定开挖方法的断面
	 * 
	 * @param excavateMethod
	 * @return
	 */
	public List<TunnelCrossSectionIndex> querySectionByExcavationMethod(int excavateMethod){
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null || excavateMethod < 0){
			return null;
		}
		
		String sql = "select * from TunnelCrossSectionIndex where ExcavateMethod = ?" ;
		
		return mDatabase.queryObjects(sql, SQLiteParamUtils.toParamemter(excavateMethod),TunnelCrossSectionIndex.class) ;
	}
	
	public List<TunnelCrossSectionIndex> queryUnUploadSections(){
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from TunnelCrossSectionIndex where info = '1'" ;
		
		return mDatabase.queryObjects(sql, TunnelCrossSectionIndex.class) ;
	}
	
	public TunnelCrossSectionIndex querySectionIndexByGuid(String guid){
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from TunnelCrossSectionIndex where Guid = ?" ;
		
		return mDatabase.queryObject(sql,new String[]{guid}, TunnelCrossSectionIndex.class) ;
	}

    public TunnelCrossSectionIndex querySectionIndexByChainage(double chainage) {

        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from TunnelCrossSectionIndex where Chainage = ?";

        return mDatabase.queryObject(sql, new String[] { String.valueOf(chainage) },
                TunnelCrossSectionIndex.class);
    }

    public TunnelCrossSectionIndex querySectionById(int id) {
        final IAccessDatabase mDatabase = getCurrentDb();
        if (mDatabase == null) {
            return null;
        }
        String sql = "select * from TunnelCrossSectionIndex where ID=?";
        String[] args = new String[] { String.valueOf(id) };
        return mDatabase.queryObject(sql, args, TunnelCrossSectionIndex.class);
    }
    
    public TunnelCrossSectionIndex querySectionByGuid(String guid) {
        final IAccessDatabase mDatabase = getCurrentDb();
        if (mDatabase == null) {
            return null;
        }
        String sql = "select * from TunnelCrossSectionIndex where Guid=?";
        String[] args = new String[] { guid };
        return mDatabase.queryObject(sql, args, TunnelCrossSectionIndex.class);
    }

    public List<TunnelCrossSectionIndex> querySectionByIds(String rowIds) {
		final IAccessDatabase mDatabase = getCurrentDb();
		if (mDatabase == null) {
			return null;
		}
		String sql = "select * from TunnelCrossSectionIndex where ID IN (" + rowIds +")";
		return mDatabase.queryObjects(sql, TunnelCrossSectionIndex.class);
	}
    
	public List<TunnelCrossSectionIndex> querySectionByGuids(String guids) {
		if (TextUtils.isEmpty(guids)) {
			return null;
		} 
		final IAccessDatabase mDatabase = getCurrentDb();
		if (mDatabase == null) {
			return null;
		}
		String[] guidInfo = guids.split(",");
		StringBuilder sb = new StringBuilder();
		for (String guid : guidInfo) {
			sb.append("\'").append(guid).append("\'").append(",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		String sql = "select * from TunnelCrossSectionIndex where Guid IN (" + sb.toString() + ")";
		return mDatabase.queryObjects(sql, TunnelCrossSectionIndex.class);
	}
}
