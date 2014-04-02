package com.sxlc.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import com.sxlc.activity.WorkNewActivity.MyOnClickListener;
import com.sxlc.adapter.SubsidenceCrossSectionInfoAdapter;
import com.sxlc.adapter.TunnelCrossSectionInfoAdapter;
import com.sxlc.adapter.WorkAdapter;
import com.sxlc.common.Constant;
import com.sxlc.dao.impl.SubsidenceCrossSectionDaoImpl;
import com.sxlc.dao.impl.TunnelCrossSectionDaoImpl;
import com.sxlc.entity.SubsidenceCrossSectionInfo;
import com.sxlc.entity.TunnelCrossSectionInfo;
import com.sxlc.entity.WorkInfos;
import com.sxlc.entity.list_infos;
import com.sxlc.utils.SelectPicPopupWindow;

/**
 * 断面主页
 * 
 * @author 代世明 创建时间: 2014-3-20 上午9:44:18
 * @version 1.0
 * @since JDK 1.6
 * 
 */
public class SectionActivity extends Activity implements OnPageChangeListener {
	
	private OnClickListener itemsOnClick;
	private SelectPicPopupWindow menuWindow;
	private View vie;
	private ListView listView,listView1;
	private List<TunnelCrossSectionInfo> infos;
	private List<SubsidenceCrossSectionInfo> infos1;
	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	private ImageView cursor;// 动画图片
	private TextView t1, t2;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	double disPlayWidth, offSet;
	Bitmap b;
	ArrayList<View> list = null;
	private LinearLayout fil;
	private LinearLayout fil2;
	/***/
	private Intent intent;
	/***/
	private int yemian;
	private TunnelCrossSectionInfoAdapter adapter;
	private SubsidenceCrossSectionInfoAdapter adapter1;
	private int iListPos1 = -1,iListPos2 = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_section);
		initUI();
		InitImageView();
		initPager();
	}

	// 初始数据
	public void setdata1() {
		CRTBTunnelMonitor CurApp = ((CRTBTunnelMonitor)getApplicationContext());
		WorkInfos CurW = CurApp.GetCurWork();
		if(CurW == null)
		{
			return;
		}
		infos = CurW.GetTunnelCrossSectionInfoList();
		boolean bLoadDB = true;
		if(infos!=null)
		{
			if(infos.size()>0)
			{
				bLoadDB = false;
			}
		}
		if(bLoadDB)
		{
			if(infos == null)
			{
				infos = new ArrayList<TunnelCrossSectionInfo>();
			}
			TunnelCrossSectionDaoImpl impl = new TunnelCrossSectionDaoImpl(this,CurW.getProjectName());
			impl.GetTunnelCrossSectionList(infos);
			CurW.SetTunnelCrossSectionInfoList(infos);
			CurApp.UpdateWork(CurW);
		}
	}

	public void setdata2() {
		CRTBTunnelMonitor CurApp = ((CRTBTunnelMonitor)getApplicationContext());
		WorkInfos CurW = CurApp.GetCurWork();
		if(CurW == null)
		{
			return;
		}
		infos1 = CurW.getScsiList();
		boolean bLoadDB = true;
		if(infos1!=null)
		{
			if(infos1.size()>0)
			{
				bLoadDB = false;
			}
		}
		if(bLoadDB)
		{
			if(infos1 == null)
			{
				infos1 = new ArrayList<SubsidenceCrossSectionInfo>();
			}
			SubsidenceCrossSectionDaoImpl impl1 = new SubsidenceCrossSectionDaoImpl(this, CurW.getProjectName());
			impl1.GetSubsidenceCrossSectionList(infos1);
			CurW.setScsiList(infos1);
			CurApp.UpdateWork(CurW);
		}
	}
	// 初始化
	public void initUI() {
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
//		fil = (LinearLayout) findViewById(R.id.fil);
//		fil2 = (LinearLayout) findViewById(R.id.fil2);
		cursor = (ImageView) findViewById(R.id.cursor);
		mPager = (ViewPager) findViewById(R.id.vPager);
		// 点击事件
		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t1.setOnClickListener(tv_Listener);
		t2.setOnClickListener(tv_Listener);
	}

	public void InitImageView() {
		Display dis = this.getWindowManager().getDefaultDisplay();
		disPlayWidth = dis.getWidth();
		b = BitmapFactory.decodeResource(this.getResources(), R.drawable.heng);
		offSet = ((disPlayWidth / 4) - b.getWidth() / 2);
		list = new ArrayList<View>();
		LayoutInflater li = LayoutInflater.from(SectionActivity.this);
		list.add(li.inflate(R.layout.layout_3, null));
		list.add(li.inflate(R.layout.layout_5, null));
	}

	public void initPager() {
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
			public Object instantiateItem(View arg0, int arg1) {
				((ViewPager) arg0).addView((View) list.get(arg1), 0);
				if (arg1 == 2) {
					yemian = 2;
				}
				return (View) list.get(arg1);
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
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(this);
		/** 隧道内断面 */
		Layout1();
		/** 地表下沉断面 */
		Layout2();
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
	
	/**
	 * 头标点击监听
	 */
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
				break;
			default:
				break;
			}
		}
	};

	public void Layout1() {
		/** 隧道内断面界面的控件 */
		/** List集合中存儲的是View,获取界面上的控件,就List.get(0),0就是集合中第一个界面,1就是集合中第二个界面 */
		listView = (ListView) list.get(0).findViewById(R.id.listView4);

		setdata1();
		adapter = new TunnelCrossSectionInfoAdapter(SectionActivity.this, infos);
		listView.setAdapter(adapter);
		// listview的行点击
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				iListPos1 = position;
				// 对话框的选项
				//CharSequence items[] = { "打开", "编辑", "导出", "删除" };
				// 实例化对话
				new AlertDialog.Builder(SectionActivity.this)
						.setItems(/*items*/Constant.SectionRowClickItems, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								CRTBTunnelMonitor CurApp = ((CRTBTunnelMonitor)getApplicationContext());
								TunnelCrossSectionInfo item = (TunnelCrossSectionInfo)listView.getItemAtPosition(iListPos1);
								switch (which) {
								case 0:// 编辑
									intent = new Intent(SectionActivity.this,
											SectionNewActivity.class);
									intent.putExtra(Constant.Select_SectionRowClickItemsName_Name,
											Double.toString(item.getChainage()));
									SectionActivity.this.startActivityForResult(intent,0);
									//startActivity(intent);
									break;
								case 1:// 删除
									WorkInfos Curw = CurApp.GetCurWork();
									TunnelCrossSectionDaoImpl impl = new TunnelCrossSectionDaoImpl(SectionActivity.this,Curw.getProjectName());
									int iRet = impl.DeleteSection(item.getId());
									switch (iRet) {
									case 0:
										Toast.makeText(SectionActivity.this, "删除失败", 3000).show();
										break;
									case 1:
										Curw.DelTunnelCrossSectionInfo(item);
										CurApp.UpdateWork(Curw);
										adapter.notifyDataSetChanged();
										Toast.makeText(SectionActivity.this, "删除成功", 3000).show();
										break;
									case -1:
										Toast.makeText(SectionActivity.this, "删除的断面中存在数据,不可删除", 3000).show();
										break;
									default:
										break;
									}
									break;
								default:
									break;
								}

							}
						})
						.setCancelable(false)
						.show().setCanceledOnTouchOutside(true);// 显示对话框
				return true;
			}
		});
	}

	public void Layout2() {
		/** 隧道内断面界面的控件 */
		/** List集合中存儲的是View,获取界面上的控件,就List.get(1),0就是集合中第一个界面,1就是集合中第二个界面 */
		listView1 = (ListView) list.get(1).findViewById(R.id.listView4);

		setdata2();
		adapter1 = new SubsidenceCrossSectionInfoAdapter(SectionActivity.this, infos1);
		listView1.setAdapter(adapter1);
		// listview的行点击
		listView1.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				iListPos2 = position;
				new AlertDialog.Builder(SectionActivity.this)
						.setItems(/*items*/Constant.SectionRowClickItems, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								CRTBTunnelMonitor CurApp = ((CRTBTunnelMonitor)getApplicationContext());
								SubsidenceCrossSectionInfo item = (SubsidenceCrossSectionInfo)listView1.getItemAtPosition(iListPos2);
								switch (which) {
								case 0:// 编辑
									intent = new Intent(SectionActivity.this,
											SectionEditActivity.class);
									intent.putExtra(Constant.Select_SectionRowClickItemsName_Name,
											Double.toString(item.getChainage()));
									SectionActivity.this.startActivityForResult(intent,0);
									//startActivity(intent);
									break;
								case 1:// 删除
									WorkInfos Curw = CurApp.GetCurWork();
									SubsidenceCrossSectionDaoImpl impl = new SubsidenceCrossSectionDaoImpl(SectionActivity.this,Curw.getProjectName());
									int iRet = impl.DeleteSubsidenceCrossSection(item.getId());
									switch (iRet) {
									case 0:
										Toast.makeText(SectionActivity.this, "删除失败", 3000).show();
										break;
									case 1:
										Curw.DelSubsidenceCrossSectionInfo(item);
										CurApp.UpdateWork(Curw);
										adapter1.notifyDataSetChanged();
										Toast.makeText(SectionActivity.this, "删除成功", 3000).show();
										break;
									case -1:
										Toast.makeText(SectionActivity.this, "删除的断面中存在数据,不可删除", 3000).show();
										break;
									default:
										break;
									}
									break;
								default:
									break;
								}

							}
						})
						.setCancelable(false)
						.show().setCanceledOnTouchOutside(true);// 显示对话框
				return true;
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			// TODO Auto-generated method stub
			if (keyCode == 82) {
				vie = new View(this);
				int num = 2;
				menuWindow = new SelectPicPopupWindow(this, itemsOnClick,2,currIndex);
				menuWindow.showAtLocation(vie, Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0);
			}
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				this.finish();
			}
		}
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (resultCode) {
		case RESULT_OK:
		{
			int iSel = data.getExtras().getInt(Constant.Select_SectionRowClickItemsName_Name);
			switch (iSel) {
			case 1:
				adapter.notifyDataSetChanged();
				break;
			case 2:
				adapter1.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}
			break;

		default:
			break;
		}
	}

}

