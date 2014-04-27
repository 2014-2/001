package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.impl.ExecuteAsyncTaskImpl;

import android.util.Log;

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
	
	@Override
	public int insert(SubsidenceCrossSectionIndex bean) {
		
		final CrtbUser user = CrtbLicenseDao.defaultDao().queryCrtbUser() ;
		
		// 非注册用户
		if(user.getUsertype() == CrtbUser.LICENSE_TYPE_DEFAULT){
			
			String sql = "select * from SubsidenceCrossSectionIndex limit ?,?" ;
			
			final IAccessDatabase mDatabase = getCurrentDb();
			
			List<SubsidenceCrossSectionIndex> list = mDatabase.queryObjects(sql, new String[]{String.valueOf(0),String.valueOf(11)},SubsidenceCrossSectionIndex.class) ;
			
			if(list != null && list.size() >= MAX_SECTION_COUNT){
				Log.e(TAG, "error : 非注册用户,不能保存10个以上的断面");
				return 100 ;
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
				
				String sql = "select * from SubsidenceCrossSectionIndex" ;
				
				List<SubsidenceCrossSectionIndex> list = mDatabase.queryObjects(sql, SubsidenceCrossSectionIndex.class) ;
				
				SubsidenceTotalDataDao dao = SubsidenceTotalDataDao.defaultDao() ;
				
				if(list != null){
					
					for(SubsidenceCrossSectionIndex section : list){
						section.setHasTestData(dao.checkSectionTestData(section.getID()));
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

    public SubsidenceCrossSectionIndex querySectionIndex(String id){

        final IAccessDatabase mDatabase = getCurrentDb();

        if(mDatabase == null){
            return null ;
        }

        String sql = "select * from SubsidenceCrossSectionIndex where ID = ?" ;

        return mDatabase.queryObject(sql,new String[]{id}, SubsidenceCrossSectionIndex.class) ;
    }

    public List<SubsidenceCrossSectionIndex> querySectionByIds(String rowIds) {
    	
        final IAccessDatabase mDatabase = getCurrentDb();
        
        if (mDatabase == null) {
            return null;
        }
        
        String sql = "select * from SubsidenceCrossSectionIndex where ID IN (" + rowIds + ")";
        return mDatabase.queryObjects(sql, SubsidenceCrossSectionIndex.class);
    }
}
