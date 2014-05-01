package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.impl.ExecuteAsyncTaskImpl;

import android.util.Log;

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
            return obj.getID();
        } else {
            return insert(bean);
        }
    }

	@Override
	public int insert(TunnelCrossSectionIndex bean) {
		
		final CrtbUser user = CrtbLicenseDao.defaultDao().queryCrtbUser() ;
		
		// 非注册用户
		if(user.getUsertype() == CrtbUser.LICENSE_TYPE_DEFAULT){
			
			String sql = "select * from TunnelCrossSectionIndex limit ?,?" ;
			
			final IAccessDatabase mDatabase = getCurrentDb();
			
			List<TunnelCrossSectionIndex> list = mDatabase.queryObjects(sql, new String[]{String.valueOf(0),String.valueOf(11)},TunnelCrossSectionIndex.class) ;
			
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
		
		String sql = "select * from TunnelCrossSectionIndex order by Chainage ASC" ;
		
		return mDatabase.queryObjects(sql, TunnelCrossSectionIndex.class) ;
	}
	
	public List<TunnelCrossSectionIndex> queryUnUploadSections(){
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from TunnelCrossSectionIndex where info = '1'" ;
		
		return mDatabase.queryObjects(sql, TunnelCrossSectionIndex.class) ;
	}
	
	public TunnelCrossSectionIndex querySectionIndex(String id){
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from TunnelCrossSectionIndex where ID = ?" ;
		
		return mDatabase.queryObject(sql,new String[]{id}, TunnelCrossSectionIndex.class) ;
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

	public List<TunnelCrossSectionIndex> querySectionByIds(String rowIds) {
		final IAccessDatabase mDatabase = getCurrentDb();
		if (mDatabase == null) {
			return null;
		}
		String sql = "select * from TunnelCrossSectionIndex where ID IN (" + rowIds +")";
		return mDatabase.queryObjects(sql, TunnelCrossSectionIndex.class);
	}
}
