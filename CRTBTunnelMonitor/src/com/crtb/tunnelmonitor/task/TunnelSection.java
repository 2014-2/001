package com.crtb.tunnelmonitor.task;

import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;

public class TunnelSection extends Section {
	private TunnelCrossSectionIndex mSection;
	
	public void setSection(TunnelCrossSectionIndex section) {
        mSection = section;
    }

    public TunnelCrossSectionIndex getSection() {
        return mSection;
    }

	@Override
	protected boolean isUpload() {
		boolean result = false;
		if ("2".equals(mSection.getInfo())) {
			result = true;
		}
		return result;
	}

	@Override
	protected int getRowId() {
		return (mSection != null) ? mSection.getID() : -1;
	}

}
