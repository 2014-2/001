
package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;

import com.crtb.tunnelmonitor.entity.SiteProjectMapping;

public class SiteProjectMappingDao extends AbstractDao<SiteProjectMapping> {

    private static SiteProjectMappingDao _instance;

    private SiteProjectMappingDao() {

    }

    public static SiteProjectMappingDao defaultDao() {

        if (_instance == null) {
            _instance = new SiteProjectMappingDao();
        }

        return _instance;
    }

    public void insertOrUpdate(int projectId, int workSiteId) {
    	SiteProjectMapping mapping = queryOneByProjectId(projectId);
    	if (mapping != null) {
    		mapping.setWorkSiteId(workSiteId);
    		update(mapping);
    	} else {
    		mapping = new SiteProjectMapping();
    		mapping.setProjectId(projectId);
    		mapping.setWorkSiteId(workSiteId);
    		insert(mapping);
    	}
    }
    
    public List<SiteProjectMapping> queryAllMapping() {
        final IAccessDatabase mDatabase = getCurrentDb();
        if (mDatabase == null) {
            return null;
        }
        String sql = "select * from SiteProjectMapping order by ID ASC";
        return mDatabase.queryObjects(sql, SiteProjectMapping.class);
    }

    public SiteProjectMapping queryOneByWorkSiteId(int workSiteId) {
        final IAccessDatabase mDatabase = getCurrentDb();
        if (mDatabase == null) {
            return null;
        }
        String sql = "select * from SiteProjectMapping where workSiteId = ?";
        String[] args = new String[] { String.valueOf(workSiteId) };
        return mDatabase.queryObject(sql, args, SiteProjectMapping.class);
    }
    
    public SiteProjectMapping queryOneByProjectId(int projectId) {
        final IAccessDatabase mDatabase = getCurrentDb();
        if (mDatabase == null) {
            return null;
        }
        String sql = "select * from SiteProjectMapping where projectId = ?";
        String[] args = new String[] { String.valueOf(projectId) };
        return mDatabase.queryObject(sql, args, SiteProjectMapping.class);
    }
}
