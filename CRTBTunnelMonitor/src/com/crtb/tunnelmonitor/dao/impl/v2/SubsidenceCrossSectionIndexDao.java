package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.impl.ExecuteAsyncTaskImpl;

import android.util.Log;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.AppHandler;
import com.crtb.tunnelmonitor.BaseAsyncTask;
import com.crtb.tunnelmonitor.entity.CrtbUser;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;

/**
 * 地表下沉断面DAO
 * 
 * @author zhouwei
 * 
 */
public final class SubsidenceCrossSectionIndexDao extends AbstractDao<SubsidenceCrossSectionIndex> {

    private static SubsidenceCrossSectionIndexDao _instance;

    private SubsidenceCrossSectionIndexDao() {

    }

    public static SubsidenceCrossSectionIndexDao defaultDao() {

        if (_instance == null) {
            _instance = new SubsidenceCrossSectionIndexDao();
        }

		return _instance;
	}

    public int insertOrUpdate(SubsidenceCrossSectionIndex bean) {
        if (bean == null) {
            return -1;
        }
        SubsidenceCrossSectionIndex obj = querySectionIndexByChainage(bean.getChainage());
        if (obj != null) {
            update(bean);
            return obj.getID();
        } else {
            return insert(bean);
        }
    }

    @Override
    public int insert(SubsidenceCrossSectionIndex bean) {

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

            String sql = "select * from SubsidenceCrossSectionIndex limit ?,?";

            final IAccessDatabase mDatabase = getCurrentDb();

            List<SubsidenceCrossSectionIndex> list = mDatabase.queryObjects(
                    sql,
                    new String[] { String.valueOf(0),
                            String.valueOf(sectionCountLimit) },
                    SubsidenceCrossSectionIndex.class);

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
				
				final IAccessDatabase mDatabase = getCurrentDb();
				
				if(mDatabase == null){
					sendMessage(MSG_QUERY_SUBSIDENCE_SECTION_FAILED);
					return ;
				}
				
				String sql = "select * from SubsidenceCrossSectionIndex ORDER BY Chainage ASC" ;
				
				List<SubsidenceCrossSectionIndex> list = mDatabase.queryObjects(sql, SubsidenceCrossSectionIndex.class) ;
				
				SubsidenceTotalDataDao dao = SubsidenceTotalDataDao.defaultDao() ;
				
				if(list != null){
					
					for(SubsidenceCrossSectionIndex section : list){
						section.setHasTestData(dao.checkSectionTestData(section.getGuid()));
					}
				}
				
				sendMessage(MSG_QUERY_SUBSIDENCE_SECTION_SUCCESS,list);
			}
			
		}) ;
	}
	
    public List<SubsidenceCrossSectionIndex> queryAllSection(){

        final IAccessDatabase mDatabase = getCurrentDb();

        if(mDatabase == null){
            return null ;
        }

        String sql = "select * from SubsidenceCrossSectionIndex" ;

        return mDatabase.queryObjects(sql, SubsidenceCrossSectionIndex.class) ;
    }

    public SubsidenceCrossSectionIndex querySectionIndexByGuid(String guid){

        final IAccessDatabase mDatabase = getCurrentDb();

        if(mDatabase == null){
            return null ;
        }

        String sql = "select * from SubsidenceCrossSectionIndex where Guid = ?" ;

        return mDatabase.queryObject(sql,new String[]{guid}, SubsidenceCrossSectionIndex.class) ;
    }

    public SubsidenceCrossSectionIndex querySectionIndexByChainage(double chainage) {
        final IAccessDatabase mDatabase = getCurrentDb();
        
        if(mDatabase == null){
            return null ;
        }
        
        String sql = "select * from SubsidenceCrossSectionIndex where Chainage = ?" ;
        
        return mDatabase.queryObject(sql,new String[]{String.valueOf(chainage)}, SubsidenceCrossSectionIndex.class) ;
    }

    public List<SubsidenceCrossSectionIndex> querySectionByIds(String rowIds) {
    	
        final IAccessDatabase mDatabase = getCurrentDb();
        
        if (mDatabase == null) {
            return null;
        }
        
        String sql = "select * from SubsidenceCrossSectionIndex where ID IN (" + rowIds + ")";
        return mDatabase.queryObjects(sql, SubsidenceCrossSectionIndex.class);
    }
    
	public List<SubsidenceCrossSectionIndex> querySectionByGuids(String guids) {
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
		String sql = "select * from SubsidenceCrossSectionIndex where Guid IN (" + sb.toString() + ")";
		return mDatabase.queryObjects(sql, SubsidenceCrossSectionIndex.class);
	}

    public SubsidenceCrossSectionIndex querySectionById(int id) {
        final IAccessDatabase mDatabase = getCurrentDb();
        if (mDatabase == null) {
            return null;
        }
        String sql = "select * from SubsidenceCrossSectionIndex where ID=?";
        String[] args = new String[] { String.valueOf(id) };
        return mDatabase.queryObject(sql, args, SubsidenceCrossSectionIndex.class);
    }

    public SubsidenceCrossSectionIndex querySectionByGuid(String guid) {
        final IAccessDatabase mDatabase = getCurrentDb();
        if (mDatabase == null) {
            return null;
        }
        String sql = "select * from SubsidenceCrossSectionIndex where Guid=?";
        String[] args = new String[] { guid };
        return mDatabase.queryObject(sql, args, SubsidenceCrossSectionIndex.class);
    }
}
