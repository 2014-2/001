package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;

import android.util.Log;

import com.crtb.tunnelmonitor.entity.SurveyerInformation;

public class SurveyerInformationDao extends AbstractDao<SurveyerInformation> {

	private static SurveyerInformationDao _instance ;
	
	private SurveyerInformationDao(){
		
	}
	
	public static SurveyerInformationDao defaultDao(){
		
		if(_instance == null){
			_instance	= new SurveyerInformationDao() ;
		}
		
		return _instance ;
	}
	
	@Override
	public int insert(SurveyerInformation bean) {
		
		if(bean == null){
			return DB_EXECUTE_FAILED ;
		}
		
		final IAccessDatabase db = getDefaultDb() ;
		
		if(db == null){
			
			Log.e("AbstractDao", "zhouwei : insert db is null");
			
			return DB_EXECUTE_FAILED ;
		}
		
		return db.saveObject(bean) > -1 ? DB_EXECUTE_SUCCESS : DB_EXECUTE_FAILED;
	}

	@Override
	public int update(SurveyerInformation bean) {
		
		if(bean == null){
			return DB_EXECUTE_FAILED ;
		}
		
		final IAccessDatabase db = getDefaultDb();
		
		if(db == null){
			
			Log.e("AbstractDao", "zhouwei : update db is null");
			
			return DB_EXECUTE_FAILED ;
		}
		
		return db.updateObject(bean) > -1 ? DB_EXECUTE_SUCCESS : DB_EXECUTE_FAILED;
	}

	@Override
	public int delete(SurveyerInformation bean) {
		
		if(bean == null){
			return DB_EXECUTE_FAILED ;
		}
		
		final IAccessDatabase db = getDefaultDb();
		
		if(db == null){
			
			Log.e("AbstractDao", "zhouwei : delete db is null");
			
			return DB_EXECUTE_FAILED ;
		}
		
		return db.deleteObject(bean) > -1 ? DB_EXECUTE_SUCCESS : DB_EXECUTE_FAILED;
	}

	public void deleteAll(){
		
		final IAccessDatabase mDatabase = getDefaultDb();
		
		if(mDatabase == null){
			return ;
		}
		
		mDatabase.deleteAll(SurveyerInformation.class);
	}
	
	public SurveyerInformation querySurveyerByName(String name){
		
		final IAccessDatabase mDatabase = getDefaultDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from SurveyerInformation where surveyerName = ?" ;
		
		return mDatabase.queryObject(sql, new String[]{name}, SurveyerInformation.class);
	}
	
	public List<SurveyerInformation> queryAllSurveyerInformation(){
		
		final IAccessDatabase mDatabase = getDefaultDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from SurveyerInformation" ;
		
		return mDatabase.queryObjects(sql, SurveyerInformation.class);
	}

    public int getRowIdByCertificateID(String certificateID) {
        int id = -1;
        if (certificateID != null) {
            final IAccessDatabase mDatabase = getDefaultDb();

            if (mDatabase != null) {
                String sql = "select * from SurveyerInformation where CertificateID = ?";
                String[] args = new String[] { certificateID };
                SurveyerInformation obj = mDatabase.queryObject(sql, args,
                        SurveyerInformation.class);
                if (obj != null) {
                    id = obj.getId();
                }
            }
        }

        return id;
    }
    
    public SurveyerInformation querySurveyerBySheetGuid(String sheetGuid){
		String TAG = "CrtbWebService";
		SurveyerInformation surveyer = null;
		String surveyerName = "";
		String certificationId = "";
		Boolean existInServer = false;

		if (sheetGuid != null) {
			final IAccessDatabase mDatabase = getCurrentDb();

			if (mDatabase != null) {
				String sql = "select * from SurveyerInformation where ProjectID = ?";
				String[] args = new String[] { sheetGuid };
				surveyer = mDatabase.queryObject(sql, args,
						SurveyerInformation.class);
			}
		}

		if(surveyer != null){
			surveyerName = surveyer.getSurveyerName();
			certificationId = surveyer.getCertificateID();
			if (surveyerName != null && certificationId != null) {
				Log.i(TAG, "RawSheetGuid=" + sheetGuid + " SurveyerName="
						+ surveyerName + " certificationId=" + certificationId);

				final IAccessDatabase mDatabaseDefault = getDefaultDb();
				if (mDatabaseDefault != null) {
					String sql = "select * from SurveyerInformation where SurveyerName = ? and CertificateID = ?";
					String[] args = new String[] { surveyerName,
							certificationId };
					surveyer = mDatabaseDefault.queryObject(sql, args,
							SurveyerInformation.class);
					if (surveyer != null) {
						existInServer = true;
					}
				}
			}
		}

		if (!existInServer) {
			Log.i(TAG, "Surveyer not exist in Server");
			surveyer = null;
		}

    	 return surveyer;
    }
    
}
