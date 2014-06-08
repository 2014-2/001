
package com.crtb.tunnelmonitor.dao.impl.v2;

import com.crtb.tunnelmonitor.entity.CrownSettlementARCHING;

public class CrownSettlementARCHINGDao extends AbstractDao<CrownSettlementARCHING> {

    private static CrownSettlementARCHINGDao _instance;

    private CrownSettlementARCHINGDao() {

    }

    public static CrownSettlementARCHINGDao defaultDao() {

        if (_instance == null) {
            _instance = new CrownSettlementARCHINGDao();
        }

        return _instance;
    }
}
