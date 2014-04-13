package com.crtb.tunnelmonitor.widget;

import org.zw.android.framework.ioc.InjectLayout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionInfo;

/**
 * 
 * @author zhouwei
 *
 */
public class CrtbRecordSubsidenceSectionInfoAdapter extends CrtbEntityAdapter<SubsidenceCrossSectionInfo> {

	protected CrtbRecordSubsidenceSectionInfoAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		return null;
	}

	@InjectLayout(layout=R.layout.item_record_tunnel_section_layout)
	class HolderView {
		
	}
}
