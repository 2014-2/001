package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;
import org.zw.android.framework.util.StringUtils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crtb.tunnelmonitor.CommonObject;
import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.activity.WarningUploadActivity.WarningUploadData;
import com.crtb.tunnelmonitor.entity.AlertInfo;
import com.crtb.tunnelmonitor.entity.MenuSystemItem;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.WaringUploadDataContainer;
import com.crtb.tunnelmonitor.infors.UploadWarningEntity;
import com.crtb.tunnelmonitor.task.WarningDataManager;
import com.crtb.tunnelmonitor.task.WarningDataManager.WarningLoadListener;
import com.crtb.tunnelmonitor.utils.CrtbUtils;
import com.crtb.tunnelmonitor.widget.CrtbProgressOverlay;
import com.crtb.tunnelmonitor.widget.WarningSortBySectionSubsidenceListView;
import com.crtb.tunnelmonitor.widget.WarningSortBySectionTunnelListView;

@InjectLayout(layout=R.layout.activity_warning_sort_by_section)
public class WarningShowSortBySectionActivity extends WorkFlowActivity implements OnPageChangeListener {
	
	public static final int TAB_SECTION			= 0 ;
	public static final int TAB_SUBSIDENCE		= 1 ;
	
	@InjectView(id=R.id.vPager)
	private ViewPager mPager;
	
	@InjectView(id=R.id.cursor)
	private ImageView cursor;
	
	private ArrayList<View> list = new ArrayList<View>();
	
	private TextView tabLeft, tabRight;// 页卡头标
	
	@InjectView(layout=R.layout.tunnel_section_layout)
	private LinearLayout leftLayout ;
	
	@InjectView(id=R.id.listView4,parent="leftLayout")
	private WarningSortBySectionTunnelListView mSectionTunnelList ;
	
	@InjectView(layout=R.layout.sub_section_layout)
	private LinearLayout rightLayout ;
	
	@InjectView(id=R.id.listView4,parent="rightLayout")
	private WarningSortBySectionSubsidenceListView mSectionSubsidenceList ;
	
	private int currIndex = 0;// 当前页卡编号
	int disPlayWidth, offSet;
	Bitmap b;
	
	private HashMap<String,SectionWarning> subWarnings;
	private HashMap<String,SectionWarning> tunnelWarnings;
	private String curSectionGuid;
	private String curSectionType;
	
	private int refreshCount = 0;
	
	private CrtbProgressOverlay progressOverlay;
	private boolean isInited = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		InjectCore.injectUIProperty(this);
		setTopbarTitle("预警上传");
		loadViewPager();
		
		mSectionTunnelList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				TunnelCrossSectionIndex bean = mSectionTunnelList.getItem(position);
				showListActionMenu("预警上传", new String[] { "打开" } , bean);
			}
		}) ;
		
		mSectionSubsidenceList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				
				SubsidenceCrossSectionIndex bean = mSectionSubsidenceList.getItem(position);
				showListActionMenu("预警上传", new String[] { "打开" } , bean);
			}
		}) ;
		
		// clear
		CommonObject.remove(SectionNewActivity.KEY_NEW_TUNNEL_SECTION_OBJECT);
		CommonObject.remove(SectionNewSubsidenceActivity.KEY_NEW_SUBSIDENCE_SECTION_OBJECT);
		
		progressOverlay = new CrtbProgressOverlay(this, CrtbUtils.getProgressLayout(this));
	}
	
	@Override  
    public void onWindowFocusChanged(boolean hasFocus) {  
        if (hasFocus && !isInited)  {  
        	isInited = true;
        	progressOverlay.showProgressOverlay("正在加载断面列表，请稍候");
    		new RefreshTask().execute();
        }  
    }  
	
	@Override
	protected void onListItemSelected(Object bean, int position, String menu) {
		
		if(bean == null || menu == null){
			return ;
		}
		
		WaringUploadDataContainer container = new WaringUploadDataContainer();
		
		// 隧道内断面
		if(bean instanceof TunnelCrossSectionIndex){
			curSectionType = "TUNNEL";
			TunnelCrossSectionIndex section = (TunnelCrossSectionIndex)bean ;
			curSectionGuid = section.getGuid();
			SectionWarning warning = tunnelWarnings.get(curSectionGuid);
			container.setCurSectionGuid(section.getGuid());
			container.setWaringDataList(warning.getWaringUploadList());
			
		} 
		// 地表下沉断面
		else if(bean instanceof SubsidenceCrossSectionIndex){
			curSectionType = "SUB";
			SubsidenceCrossSectionIndex section = (SubsidenceCrossSectionIndex)bean ;
			curSectionGuid = section.getGuid();
			SectionWarning warning = subWarnings.get(curSectionGuid);
			container.setCurSectionGuid(section.getGuid());
			container.setWaringDataList(warning.getWaringUploadList());
		}
		
		Intent uploader = new Intent(WarningShowSortBySectionActivity.this,WarningUploadActivity.class);
		CommonObject.putObject(WaringUploadDataContainer.KEY, container);
		startActivity(uploader);		
	}
	
	public interface OperationCallBack{
		public void done(boolean state);
	}

	private void loadViewPager(){
		
		tabLeft 	= (TextView) findViewById(R.id.text1);
		tabRight 	= (TextView) findViewById(R.id.text2);
		
		disPlayWidth = mDisplayMetrics.widthPixels ;
		b = BitmapFactory.decodeResource(this.getResources(), R.drawable.heng);
		offSet = ((disPlayWidth / 4) - b.getWidth() / 2);
		
		ViewGroup.LayoutParams lp = cursor.getLayoutParams() ;
		lp.width = disPlayWidth >> 1 ;
		lp.height = 4 ;
		cursor.setLayoutParams(lp);
		
		//
		list.add(leftLayout);
		list.add(rightLayout);
		
		tabLeft.setOnClickListener(new MyOnClickListener(TAB_SECTION));
		tabRight.setOnClickListener(new MyOnClickListener(TAB_SUBSIDENCE));
		
		PagerAdapter pa = new PagerAdapter() {

			@Override
			public void destroyItem(View arg0, int arg1, Object arg2) {
				((ViewPager) arg0).removeView((View) list.get(arg1));

			}

			@Override
			public void finishUpdate(View arg0) {

			}

			@Override
			public int getCount() {
				return list.size();
			}

			@Override
			public Object instantiateItem(View views, int index) {
				((ViewPager) views).addView((View) list.get(index));
				return (View) list.get(index);
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public void restoreState(Parcelable arg0, ClassLoader arg1) {

			}

			@Override
			public Parcelable saveState() {
				return null;
			}

			@Override
			public void startUpdate(View arg0) {

			}
		};
		
		mPager.setAdapter(pa);
		mPager.setCurrentItem(TAB_SECTION);
		mPager.setOnPageChangeListener(this);
	}
	
	@Override
	protected void onSystemMenuClick(MenuSystemItem menu) {
		
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		int single = (int) (b.getWidth() + offSet * 2);
		TranslateAnimation ta = new TranslateAnimation(currIndex * single,
				single * arg0, 0, 0);
		ta.setFillAfter(true);
		ta.setDuration(200);
		cursor.startAnimation(ta);
		currIndex = arg0;
	}
	
	
	public class MyOnClickListener implements View.OnClickListener {
		
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	public OnClickListener tv_Listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			
			int single = (int) (b.getWidth() + offSet * 2);
			
			switch (v.getId()) {
			case R.id.t1:
				
				mPager.setCurrentItem(0);
				if (currIndex != 0) {
					TranslateAnimation ta = new TranslateAnimation(
							(currIndex * single), 0, 0, 0);
					ta.setFillAfter(true);
					ta.setDuration(200);
					cursor.startAnimation(ta);
				}
				
				currIndex = 0;
				
				mSectionTunnelList.onResume() ;
				
				break;
			case R.id.t2:
				mPager.setCurrentItem(1);
				
				if (currIndex != 1) {
					TranslateAnimation ta = new TranslateAnimation(currIndex
							* single, single, 0, 0);
					ta.setFillAfter(true);
					ta.setDuration(200);
					cursor.startAnimation(ta);
				}
				
				currIndex = 1;
				
				mSectionSubsidenceList.onResume() ;
				
				break;
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		if(curSectionGuid != null && StringUtils.isEmpty(curSectionGuid)){
			Object data  = CommonObject.findObject(WarningUploadActivity.CURRENT_EDIT_SECTION);
			if(data != null){
				CommonObject.remove(WarningUploadActivity.CURRENT_EDIT_SECTION);
			}
			List<WarningUploadData> list = (List<WarningUploadData>)data;
			
			if(curSectionType.equals("TUNNEL")){
				tunnelWarnings.get(curSectionGuid).setWarings(list);
			} else if(curSectionType.equals("SUB")){
				subWarnings.get(curSectionGuid).setWarings(list);
			}
		}
	}
	
	private void initDataBySectionType(final String sectionType) {
		WarningDataManager dataManager = new WarningDataManager();
		dataManager.loadDataSortBySectionType(sectionType, new WarningLoadListener() {
			@Override
			public void done(List<UploadWarningEntity> uploadDataList) {
				// 构造上传数据
				
				if(sectionType.equals("SUB")){
					subWarnings = new HashMap<String,SectionWarning>();
				} else if(sectionType.equals("TUNNEL")){
					tunnelWarnings = new HashMap<String,SectionWarning>();
				}
				ArrayList<String> sectionGuids = new ArrayList<String>();
				for (UploadWarningEntity uploadWarningData : uploadDataList) {

					WarningUploadData warningData = new WarningUploadData();
					warningData.setUploadWarningData(uploadWarningData);
					warningData.setChecked(false);

					AlertInfo alert = uploadWarningData.getAlertInfo();
					SectionWarning waring = null;
					String sectionGuid = alert.getSectionId();
					if(sectionType.equals("SUB")){
						if (subWarnings.containsKey(sectionGuid)) {
							waring = subWarnings.get(sectionGuid);
						} else {
							waring = new SectionWarning();
							subWarnings.put(sectionGuid, waring);
							sectionGuids.add(sectionGuid);
						}						
					} else if(sectionType.equals("TUNNEL")){
						if (tunnelWarnings.containsKey(sectionGuid)) {
							waring = tunnelWarnings.get(sectionGuid);
						} else {
							waring = new SectionWarning();
							tunnelWarnings.put(sectionGuid, waring);
							sectionGuids.add(sectionGuid);
						}	
					}
					waring.addWarning(warningData);
				}
				refreshData(sectionType,sectionGuids);
			}
		});
	}
	
	private void refreshData(String sectionType,ArrayList<String> sectionGuids) {
		if(sectionType.equals("SUB")){
			mSectionSubsidenceList.loadData(sectionGuids);
		} else if(sectionType.equals("TUNNEL")){
			mSectionTunnelList.loadData(sectionGuids);
		}
		if(refreshCount < 2){
			refreshCount++;
		} 
		if (refreshCount == 2) {
			progressOverlay.hideProgressOverlay();
			refreshCount = 0;
		}
	}
		
	class RefreshTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			initDataBySectionType("TUNNEL");
			initDataBySectionType("SUB");
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}

	class SectionWarning {
		public boolean isComputed() {
			return computed;
		}
		public void setComputed(boolean computed) {
			this.computed = computed;
		}
		public String getSectionGuid() {
			return sectionGuid;
		}
		public void setSectionGuid(String sectionGuid) {
			this.sectionGuid = sectionGuid;
		}
		public List<WarningUploadData> getWaringUploadList() {
			return warnData;
		}
		public void setWarings(List<WarningUploadData> warnData) {
			if(warnData == null){
				warnData = new ArrayList<WarningUploadData>();
			}
			this.warnData = warnData;
		}
		
		public void addWarning(WarningUploadData warning) {
			if(warnData == null){
				warnData = new ArrayList<WarningUploadData>();
			}
			warnData.add(warning);
		}
		private boolean computed;
		private String sectionGuid;
		private List<WarningUploadData> warnData;
	}

	public class SectionInfo {
		private String setcionGuid;
		
	}
	
	
	
}

