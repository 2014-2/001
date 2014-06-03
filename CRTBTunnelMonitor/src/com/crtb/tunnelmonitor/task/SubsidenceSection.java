package com.crtb.tunnelmonitor.task;

import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;

public class SubsidenceSection extends Section {
	 private SubsidenceCrossSectionIndex mSection;
	 
     public void setSection(SubsidenceCrossSectionIndex section) {
         mSection = section;
     }

     public SubsidenceCrossSectionIndex getSection() {
         return mSection;
     }

	@Override
	protected boolean isUpload() {
		boolean result = false;
		if ( 2 == mSection.getUploadStatus()) {
			result = true;
		}
		return result;
	}
	
	@Override
	protected int getRowId() {
		return (mSection != null) ? mSection.getID() : -1;
	}

}
