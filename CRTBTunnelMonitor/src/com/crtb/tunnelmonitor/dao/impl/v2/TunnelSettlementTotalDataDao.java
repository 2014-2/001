package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;

import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;

/**
 * 全站仪
 * 
 * @author zhouwei
 *
 */
public class TunnelSettlementTotalDataDao extends AbstractDao<TunnelSettlementTotalData> {

	private static TunnelSettlementTotalDataDao _instance ;
	
	private TunnelSettlementTotalDataDao(){
		
	}
	
	public static TunnelSettlementTotalDataDao defaultDao(){
		
		if(_instance == null){
			_instance	= new TunnelSettlementTotalDataDao() ;
		}
		
		return _instance ;
	}
	
	public List<TunnelSettlementTotalData> queryAllRawSheetIndex() {
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from TunnelSettlementTotalData";
		
		return mDatabase.queryObjects(sql, TunnelSettlementTotalData.class);
	}

    /**
     * 查询 本次测量(MEASNo)之前的所有相同断面和相同测点类型的测点信息
     * 
     * @param chainageId
     *            断面里程ID
     * @param pntType
     *            测点类型
     * @param MEASNo
     *            本次测量是第几次测量
     * @return 查询到的测点信息List
     */
    public List<TunnelSettlementTotalData> queryInfoBeforeMEASNo(int chainageId, String pntType,
            int MEASNo) {
    	
    	final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
        String sql = "select * from TunnelSettlementTotalData where chainageId=" + chainageId
                + " AND pntType=" + pntType + " AND MEASNo < " + String.valueOf(MEASNo)
                + " order by MEASNo ASC";
        
        return mDatabase.queryObjects(sql, TunnelSettlementTotalData.class);
    }

    public TunnelSettlementTotalData queryOppositePointOfALine(TunnelSettlementTotalData point1,
            String point2Type) {
    	
    	final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
        TunnelSettlementTotalData point2 = null;
        String sql = "select * from TunnelSettlementTotalData where chainageId="
                // 同一个断面
                + point1.getChainageId()
                // 同一次测量
                + " AND MEASNo=" + point1.getMEASNo()
                + " AND pntType=" + point2Type;

        List<TunnelSettlementTotalData> list = mDatabase.queryObjects(sql,
                TunnelSettlementTotalData.class);

        if (list != null && list.size() == 1) {
            point2 = list.get(0);
        }

        return point2;
    }
}
