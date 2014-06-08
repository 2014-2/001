
package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;

import com.crtb.tunnelmonitor.entity.WorkSiteIndex;

public class WorkSiteIndexDao extends AbstractDao<WorkSiteIndex> {
    private static WorkSiteIndexDao _instance;

    private WorkSiteIndexDao() {

    }

    public static WorkSiteIndexDao defaultDao() {
        if (_instance == null) {
            _instance = new WorkSiteIndexDao();
        }
        return _instance;
    }

    public List<WorkSiteIndex> queryAllWorkSite() {
        final IAccessDatabase mDatabase = getCurrentDb();
        if (mDatabase == null) {
            return null;
        }
        String sql = "select * from WorkSiteIndex order by ID ASC";
        return mDatabase.queryObjects(sql, WorkSiteIndex.class);
    }

    public WorkSiteIndex queryWorkSiteById(int id) {
        final IAccessDatabase mDatabase = getCurrentDb();
        if (mDatabase == null) {
            return null;
        }
        String sql = "select * from WorkSiteIndex where ID = ?";
        String[] args = new String[] { String.valueOf(id) };
        return mDatabase.queryObject(sql, args, WorkSiteIndex.class);
    }

    public WorkSiteIndex queryOneBySiteCode(String siteCode) {
        final IAccessDatabase mDatabase = getCurrentDb();
        if (mDatabase == null) {
            return null;
        }
        String sql = "select * from WorkSiteIndex where siteCode = ?";
        String[] args = new String[] { siteCode };
        return mDatabase.queryObject(sql, args, WorkSiteIndex.class);
    }

    public int insetOrUpdate(WorkSiteIndex bean) {
        int ret = -1;
        if (bean != null) {
            WorkSiteIndex obj = queryOneBySiteCode(bean.getSiteCode());
            if (obj != null) {
                update(bean);
                ret = obj.getID();
            } else {
                ret = insert(bean);
            }
        }
        return ret;
    }
}
