package com.crtb.tunnelmonitor.dao.impl.v2;

import com.crtb.tunnelmonitor.entity.TunnelCrossSectionExIndex;

public class TunnelCrossSectionExIndexDao extends AbstractDao<TunnelCrossSectionExIndex> {

	private static TunnelCrossSectionExIndexDao _instance;

	private TunnelCrossSectionExIndexDao() {

	}

	public static TunnelCrossSectionExIndexDao defaultDao() {

		if (_instance == null) {
			_instance = new TunnelCrossSectionExIndexDao();
		}

		return _instance;
	}
	
}
