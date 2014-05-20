
package com.crtb.tunnelmonitor.dao.impl.v2;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.db.core.SQLiteParamUtils;
import org.zw.android.framework.util.StringUtils;

import android.content.Context;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.entity.CrtbUser;

/**
 * 权限认证
 * 1. 注册默认账号
 * 2. 注册授权账号
 * 3. 查询当前工作面
 * 4. 查询所有工作面
 * 
 * @author zhouwei
 */
public final class CrtbLicenseDao extends AbstractDefaultDBDao<CrtbUser> {

    static final String TAG = "CrtbLicenseDao";

    private static CrtbLicenseDao _instance;

    private String mDefaultUsername;
    private CrtbUser mCrtbUser;

    private CrtbLicenseDao() {

    }

    public static CrtbLicenseDao defaultDao() {

        if (_instance == null) {
            _instance = new CrtbLicenseDao();
        }

        return _instance;
    }

    /**
     * 注册默认用户
     * 
     * @param context
     */
    public static void registDefaultUser(Context context) {

        String username = context.getPackageName();

        CrtbLicenseDao dao = defaultDao();
        dao.mDefaultUsername = username;
        IAccessDatabase db = dao.getDefaultDb();

        if (db == null) {
            return;
        }

        String sql = "select * from CrtbUser where username = ? ";

        CrtbUser user = db
                .queryObject(sql, SQLiteParamUtils.toParamemter(username), CrtbUser.class);

        if (user == null) {

            user = new CrtbUser();
            user.setUsername(username);
            user.setUsertype(CrtbUser.LICENSE_TYPE_DEFAULT);
            user.setLicense("123456789");
            user.setVersionLowLimit(0);
            user.setVersionHighLimit(0);

            db.saveObject(user);
        }
    }

    /**
     * 注册用户
     * 
     * @param context
     * @param username
     * @param license
     * @return
     */
    public int registLicense(Context context, String username, int userType, int versionLow,
            int versionHigh, String license) {

        IAccessDatabase db = getDefaultDb();

        if (db == null || StringUtils.isEmpty(username) || StringUtils.isEmpty(license)) {
            return DB_EXECUTE_FAILED;
        }

        String sql = "select * from CrtbUser where username = ? ";
        int error = -1;

        CrtbUser user = db.queryObject(sql, new String[] { username }, CrtbUser.class);

        if (user == null) {
            user = new CrtbUser();
            user.setUsername(username);
            user.setUsertype(userType);
            user.setVersionLowLimit(versionLow);
            user.setVersionHighLimit(versionHigh);
            user.setLicense(license);
            error = db.saveObject(user);
        } else {
            user.setUsername(username);
            user.setLicense(license);
            user.setUsertype(userType);
            user.setVersionLowLimit(versionLow);
            user.setVersionHighLimit(versionHigh);
            error = db.updateObject(user);
        }

        AppCRTBApplication.getInstance().setCurUser(user);
        return error > 0 ? DB_EXECUTE_SUCCESS : DB_EXECUTE_FAILED;
    }

    /**
     * 查询当前用户
     * 
     * @return
     */
    public CrtbUser queryCrtbUser() {

//        if (mCrtbUser == null
//                || (mDefaultUsername != null && mDefaultUsername.equals(mCrtbUser.getUsername()))) {

            IAccessDatabase db = getDefaultDb();

            if (db == null) {
                return null;
            }

            // 查询注册用户
            String sql = "select * from CrtbUser where username <> ? ";

            String[] args = SQLiteParamUtils.toParamemter(mDefaultUsername);

            // 注册用户
            CrtbUser user = db.queryObject(sql, args, CrtbUser.class);

            if (user != null) {
                mCrtbUser = user;
            }

            // 默认注册用户
            else {
                sql = "select * from CrtbUser where username = ? ";
                mCrtbUser = db.queryObject(sql, args, CrtbUser.class);
            }
//        }

        return mCrtbUser;
    }

}
