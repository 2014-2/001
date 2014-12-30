package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.binary.StringUtils;
import org.zw.android.framework.util.DateUtils;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.adapter.AlertListAdapter;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.entity.AlertInfo;
import com.crtb.tunnelmonitor.entity.AlertList;
import com.crtb.tunnelmonitor.entity.CrtbUser;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.utils.AlertManager;
import com.crtb.tunnelmonitor.utils.AlertUtils;
import com.crtb.tunnelmonitor.utils.CrtbUtils;
import com.crtb.tunnelmonitor.widget.CrtbProgressOverlay;

/**
 * 开始测量
 * 
 * YX:整理Case、添加上一个断面、下一个断面
 */
public class WarningActivity extends WorkFlowActivity {

	protected static final String TAG = "WarningActivity: ";
	private List<String> sectionChainPrefix;
	private int curSectionIndex;
	private int sectionCount;
	private StringSort stringSort = new StringSort();
	private String sectionType;
	private List<AlertInfo> curAlerts;
	private LinearLayout test_bottom_layout;
	private boolean isFirstEnter = true;
	private HashMap<String,Warning> warings;
	private Warning curWarning;
	private String projectPrefix;
	private ListView listview;
	private RelativeLayout warningMenu;
	private RelativeLayout mHandleCompleteView;
	private RadioGroup mDealWayRadios;
	// private RadioButton mDealWayBtnDiscard;
	// private RadioButton mDealWayBtnAsFirst;
	private RadioButton mDealWayBtnCorrection;
	private RadioButton mDealWayBtnRebury;
	// private RadioButton mDealWayBtnNormal;

	private RadioButton[] mRadioBtns;

	private EditText mCorrectionView;
	private TextView mCorrectionUnitView;
	private EditText mWarningRemarkView;

	private TextView baojing, yixiao;
	private TextView warningSignalTV, warningPointNumTV, warningStateTV, warningValueTV, warningDateTV, warningMessageTV, warningDealWayTV, oldDateMileageTV, oldDateListNumTV, oldDatePointTV;
	private TextView currentValue,rockGrade,alertLimit,noDealAlertValue,recommandValue;
	private Button /* normalCalBtn, discardBtn, asFirstLineBtn, */correctionBtn, reburyBtn, handlingDetailBtn, completeOkBtn, completeCancelBtn;
	private View oldChooseView;
	private int clickedItem;
	private AlertListAdapter adapter;
	private int mAlertNum;
	private int mHandledAlertNum;
	protected int mCheckedRaidoId;
	private int mUserType = CrtbUser.LICENSE_TYPE_DEFAULT;
	private View.OnClickListener mBtnOnClickListener;
	
	private CrtbProgressOverlay progressOverlay;
	private boolean isInited = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_warning_sort);
		super.onCreate(savedInstanceState);
		try {
			initClickListener();
			initHandlingView();
			initWaringListView();
			initSectionOperationView();
			initOther();
			//refreshData(sectionType);
		} catch (Exception e) {
			Log.e(Constant.LOG_TAG_ACTIVITY,TAG+e.getMessage());
		}
	}
	
	@Override  
    public void onWindowFocusChanged(boolean hasFocus) {  
        if (hasFocus && !isInited)  {  
        	isInited = true;
        	showProgressOverlay();
        	refreshData(sectionType);
        }  
    }  
	
	private void initClickListener() {
		mBtnOnClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				releaseChooseEffect();

				switch (view.getId()) {
				// case R.id.normal:
				// case R.id.discard_btn:
				// case R.id.as_first_line:
				// case R.id.rebury:
				case R.id.warning_show_correction:
					addCorrection(view);
					break;
				case R.id.warning_show_handling_detail:
					showHandlingDetail();
					break;
				case R.id.complete_ok:
					finishHandling();
					break;
				case R.id.complete_cancel:
					cancelHandling();
					break;
				case R.id.test_bnt_left_section:
					preSection();
					break;
				case R.id.test_bnt_next_section:
					nextSection();
					break;
				}
			}
		};
	}
	
	@Override
	public void onBackPressed() {
		//YX 判断处理页面
		if (mHandleCompleteView != null && mHandleCompleteView.getVisibility() == View.VISIBLE) {
			cancelHandling();
			releaseChooseEffect();
		} 
		//YX 判断ListView上的菜单
		else if (warningMenu != null && warningMenu.getVisibility() == View.VISIBLE) {
			releaseChooseEffect();
		} 
		//YX 触发父类的事件
		else{
			super.onBackPressed();
		}
	}
	
	public void initWaringListView() {
		// rela = (LinearLayout) findViewById(R.id.rela);
		// normalCalBtn = (Button) findViewById(R.id.normal);
		// setBtnClickListener(normalCalBtn);
		// discardBtn = (Button) findViewById(R.id.discard_btn);
		// setBtnClickListener(discardBtn);
		// asFirstLineBtn = (Button) findViewById(R.id.as_first_line);
		// setBtnClickListener(asFirstLineBtn);
		// reburyBtn = (Button) findViewById(R.id.rebury);
		// setBtnClickListener(reburyBtn);
		correctionBtn = (Button) findViewById(R.id.warning_show_correction);
		setBtnClickListener(correctionBtn);
		handlingDetailBtn = (Button) findViewById(R.id.warning_show_handling_detail);
		setBtnClickListener(handlingDetailBtn);
		adapter = new AlertListAdapter(this, curAlerts);
		warningMenu = (RelativeLayout) findViewById(R.id.warning_show_menu);
		listview = (ListView) findViewById(R.id.warning_show_listView);
		listview.setDividerHeight(1);
		listview.setAdapter(adapter);
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (mUserType != CrtbUser.LICENSE_TYPE_REGISTERED) {
					Toast.makeText(getApplicationContext(), "预警处理对非注册用户不可用!", Toast.LENGTH_LONG).show();
					return true;
				}

				if (oldChooseView != null) {
					oldChooseView.setBackgroundResource(R.color.warning_bg);
				}

				view.setBackgroundResource(R.color.lightyellow);
				clickedItem = position;
				warningMenu.setVisibility(View.VISIBLE);
				oldChooseView = view;
				return true;
			}
		});

		warningMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				warningMenu.setVisibility(View.GONE);
				if (oldChooseView != null)
					oldChooseView.setBackgroundResource(R.color.warning_bg);
			}
		});
	}
	
	private void initOther(){
		baojing = (TextView) findViewById(R.id.warning_show_rizhi);
		yixiao = (TextView) findViewById(R.id.warning_show_yixiaojing);
		setTopbarTitle("断面名称");
		sectionType = getIntent().getStringExtra(Constant.WARNING_TYPE);
		mUserType = AppCRTBApplication.getInstance().getCurUserType();
		projectPrefix = CrtbUtils.getSectionPrefix();
		progressOverlay = new CrtbProgressOverlay(this, CrtbUtils.getProgressLayout(this));
	}
	
	private void initHandlingView() {
		warningSignalTV = (TextView) findViewById(R.id.warning_signal);
		warningPointNumTV = (TextView) findViewById(R.id.warning_point_num);
		warningStateTV = (TextView) findViewById(R.id.warning_state);
		warningValueTV = (TextView) findViewById(R.id.warning_value);
		warningDateTV = (TextView) findViewById(R.id.warning_date);
		warningMessageTV = (TextView) findViewById(R.id.warning_message);
		warningDealWayTV = (TextView) findViewById(R.id.warning_deal_way);
		oldDateMileageTV = (TextView) findViewById(R.id.old_date_mileage);
		oldDateListNumTV = (TextView) findViewById(R.id.old_date_list_num);
		oldDatePointTV = (TextView) findViewById(R.id.old_date_point);
		rockGrade = (TextView)findViewById(R.id.rock_grade);
		alertLimit = (TextView)findViewById(R.id.alert_limit);
		currentValue = (TextView)findViewById(R.id.current_value);
		noDealAlertValue = (TextView)findViewById(R.id.no_deal_alert_value);
		recommandValue = (TextView)findViewById(R.id.recommand_value);
		
		completeOkBtn = (Button) findViewById(R.id.complete_ok);
		setBtnClickListener(completeOkBtn);
		completeCancelBtn = (Button) findViewById(R.id.complete_cancel);
		setBtnClickListener(completeCancelBtn);
		
		mHandleCompleteView = (RelativeLayout) findViewById(R.id.complete_warning_rl);
		mDealWayRadios = (RadioGroup) mHandleCompleteView.findViewById(R.id.radio_group);
		// mDealWayBtnDiscard = (RadioButton)
		// mDealWayRadios.findViewById(R.id.radio_button_void);
		// mDealWayBtnAsFirst = (RadioButton)
		// mDealWayRadios.findViewById(R.id.radio_button_first);
		mDealWayBtnCorrection = (RadioButton) mDealWayRadios.findViewById(R.id.radio_button_add);
		//mDealWayBtnRebury = (RadioButton) mDealWayRadios.findViewById(R.id.radio_button_rebury);
		// mDealWayBtnNormal = (RadioButton)
		// mDealWayRadios.findViewById(R.id.radio_button_normal);
		mDealWayRadios.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				mCheckedRaidoId = checkedId;
				if (mCorrectionView != null) {
					mCorrectionView.setEnabled(checkedId == mDealWayBtnCorrection.getId());
				}
			}
		});

		mRadioBtns = new RadioButton[] {/*
										 * mDealWayBtnNormal,
										 * mDealWayBtnDiscard,
										 * mDealWayBtnAsFirst,
										 * mDealWayBtnRebury, 
										 */mDealWayBtnCorrection};
		mWarningRemarkView = (EditText) mHandleCompleteView.findViewById(R.id.warning_remark);

		mCorrectionUnitView = (TextView) mHandleCompleteView.findViewById(R.id.correction_unit);
		mCorrectionView = (EditText) mHandleCompleteView.findViewById(R.id.add_edit);
		mCorrectionView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (curAlerts == null) {
					return;
				}
				AlertInfo alert = curAlerts.get(clickedItem);
				if (alert != null) {
					boolean isSV = AlertUtils.isSpeed(alert.getUType());
					Editable e = mCorrectionView.getText();
					double correction = (double) 0;
					if (e != null && e.length() > 0) {
						String cstr = e.toString();
						if (cstr != null && !cstr.trim().endsWith("-") && !cstr.equals(".")) {
							correction = Double.valueOf(cstr);
						}
					}
					if (warningValueTV != null) {
						double realCorrection = correction;
						if (isSV) {
							realCorrection = correction / AlertUtils.getDeltaTime(curAlerts.get(clickedItem));
						}
						warningValueTV.setText("累计超限: " + String.format("%1$.1f", CrtbUtils.formatDouble(alert.getUValue(), 1)) + AlertUtils.getAlertValueUnit(alert.getUType()));
					}
					if (mWarningRemarkView != null) {
						mWarningRemarkView.setText(getString(R.string.remark_correction, correction));
					}
				}
			}
		});

	}

	private void initSectionOperationView() {
		findViewById(R.id.test_bnt_left_section).setOnClickListener(mBtnOnClickListener);
		findViewById(R.id.test_bnt_next_section).setOnClickListener(mBtnOnClickListener);
		test_bottom_layout = (LinearLayout)findViewById(R.id.test_bottom_layout);
	}

	private void showProgressOverlay() {
		progressOverlay.showProgressOverlay("正在加载预警信息，请稍候");
	}

	private void preSection() {
		if(sectionCount == 0){
			return;
		}
		if(sectionCount == 1){
			Toast.makeText(WarningActivity.this, "已经是第一个断面", Toast.LENGTH_SHORT).show();
			return;
		}
		if (curSectionIndex < 1) {
			curSectionIndex = sectionCount - 1;
		} else {
			--curSectionIndex;
		}
		refreshBySectionWrapper();
	}

	private void nextSection() {
		if(sectionCount == 0){
			return;
		}
		if(sectionCount == 1){
			Toast.makeText(WarningActivity.this, "已经是最后一个断面", Toast.LENGTH_SHORT).show();
			return;
		}
		if (curSectionIndex > sectionCount - 2) {
			curSectionIndex = 0;
		} else {
			++curSectionIndex;
		}
		refreshBySectionWrapper();
	}

	private void refreshNum() {
		progressOverlay.hideProgressOverlay();
		refreshBySectionWrapper();
	}

	private void refreshBySectionWrapper() {
		if (sectionCount < 1 || curSectionIndex < 0 || sectionCount < curSectionIndex + 1 || sectionCount == 0) {
			return;
		}
		
		String secGuid = sectionChainPrefix.get(curSectionIndex).trim();
		curWarning = warings.get(secGuid);
		curAlerts = curWarning.getAlerts();
		if(!curWarning.computed){
			showProgressOverlay();
			new GetComputeValueTask().execute();
		} else {
			refreshBySection();
		}
	}

	private void refreshBySection() {
		progressOverlay.hideProgressOverlay();
		if(curAlerts == null){
			return;
		}
		
		if (curAlerts.size() > 0) {
			adapter.refreshData(curAlerts);
			String sectionName = curAlerts.get(0).getXinghao();
			setTopbarTitle(sectionName + " (" + (curSectionIndex + 1) + "/" + sectionCount + ")");
		}
		mAlertNum = adapter == null ? 0 : adapter.getCount();
		mHandledAlertNum = adapter == null ? 0 : adapter.getHandledCount();
		baojing.setText("报警日志：(" + mAlertNum + ")");
		yixiao.setText("已消警：(" + mHandledAlertNum + ")");		
	}

	private void releaseChooseEffect() {
		warningMenu.setVisibility(View.GONE);
		if (oldChooseView != null) {
			oldChooseView.setBackgroundResource(R.color.warning_bg);
		}
	}

	public void setBtnClickListener(final Button btn) {
		btn.setOnClickListener(mBtnOnClickListener);
	}

	private boolean handleAlert() {
		boolean isBreak = false;

		AlertInfo ai = (AlertInfo) adapter.getItem(clickedItem);
		if (ai == null) {
			return true;
		}

		boolean isRebury = false;

		int curStatus = ai.getAlertStatus();
		float correction = 0f;

		if (mCorrectionView != null && mCorrectionView.getVisibility() == View.VISIBLE) {
			Editable e = mCorrectionView.getText();
			if (e != null && e.length() > 0) {
				correction = Float.valueOf(e.toString());
			}
		}

		int alertId = ai.getAlertId();
		int dataStatus = Constant.POINT_DATASTATUS_NONE;
		/*
		 * if (mCheckedRaidoId == mDealWayBtnDiscard.getId()) { Log.d(TAG,
		 * "Handling way: discard data"); dataStatus =
		 * AlertUtils.POINT_DATASTATUS_DISCARD; } else if (mCheckedRaidoId ==
		 * mDealWayBtnAsFirst.getId()) { Log.d(TAG,
		 * "Handling way: As First line"); dataStatus =
		 * AlertUtils.POINT_DATASTATUS_AS_FIRSTLINE; } else
		 */if (mCheckedRaidoId == mDealWayBtnCorrection.getId()) {
			Log.d(TAG, "Handling way: Correction");
			dataStatus = Constant.POINT_DATASTATUS_CORRECTION;
		}/*
		 * else if (mCheckedRaidoId == mDealWayBtnNormal.getId()) { Log.d(TAG,
		 * "Handling way: Normal"); dataStatus =
		 * AlertUtils.POINT_DATASTATUS_NORMAL; }
		 */else if (mCheckedRaidoId == mDealWayBtnRebury.getId()) {
			Log.d(TAG, "Handling way: Rebury");
			isRebury = true;
			dataStatus = Constant.POINT_DATASTATUS_CORRECTION;
		} else if (correction != 0) {
			Log.d(TAG, "Handling way: Correction");
			// If no radio button is selected and correction is input, treat as
			// correction
			dataStatus = Constant.POINT_DATASTATUS_CORRECTION;
		}

		int alertStatus = 0;// TODO : may also be 2?, if so,
							// 需要将mBtnOnClickListener中deal_with_btn也要和complete_btn一样的逻辑

		int handling = dataStatus;

		String info = mWarningRemarkView.getText().toString();
		if (info.trim().equals("")) {
			Toast.makeText(WarningActivity.this, "请录入报警原因及销警措施", Toast.LENGTH_LONG).show();
			isBreak = false;
			return isBreak;
		}

		Log.d(TAG, "handleAlert 处理内容：" + handling);
		new AlertManager().handleAlert(alertId, dataStatus, isRebury, correction, curStatus, alertStatus, handling, info, new Date(System.currentTimeMillis()), ai.getRockGrade(),
				new AlertManager.HandleFinishCallback() {

					@Override
					public void onFinish() {
						String sectionName = sectionChainPrefix.get(curSectionIndex).trim();
						refreshData(sectionName);
					}
				});
		isBreak = true;
		return isBreak;
	}

	private void setOpertationViewVisibility(boolean state){
		if(state){
			test_bottom_layout.setVisibility(View.VISIBLE);
			mHandleCompleteView.setVisibility(View.GONE);
		} else {
			test_bottom_layout.setVisibility(View.GONE);
			mHandleCompleteView.setVisibility(View.VISIBLE);
		}
	}
	
	private void addCorrection(View view){
		if (curAlerts != null) {
			AlertInfo alert = curAlerts.get(clickedItem);
			if (alert != null) {

				if (alert.getAlertStatus() == 0) {// "已消警"
					Toast.makeText(WarningActivity.this, "已消警", Toast.LENGTH_LONG).show();
					return;
				}
				
//				if (CrossSectionStopSurveyingDao.defaultDao().getSectionStopState(alert.getSectionId())) {
//					CrtbUtils.showDialogWithYes(WarningShowActivity.this, "报警处理", "断面已经封存，不能再进行处理!");
//					return;
//				}
				
				if (!AlertUtils.hasUnhandledPreviousWarningData(alert)) {
					CrtbUtils.showDialogWithYes(WarningActivity.this, "报警处理", "该监测位置的往期预警必须已关闭，才能处理本期预警!");
					return;
				}

				if (alert.getAlertStatus() == 0) {// "已消警"
					Toast.makeText(WarningActivity.this, "已消警", Toast.LENGTH_LONG).show();
					return;
				}
				RawSheetIndex sheet = RawSheetIndexDao.defaultDao().queryOneByGuid(alert.getSheetId());
				String date = DateUtils.toDateString(sheet.getCreateTime(), DateUtils.DATE_TIME_FORMAT);

				mHandleCompleteView.setVisibility(View.VISIBLE);
				warningSignalTV.setText(alert.getXinghao());
				// YX sub-1
				String pntType = alert.getPntType();
				if (!pntType.startsWith("A") && !pntType.startsWith("S")) {
					pntType = "" + (Integer.valueOf(pntType) - 1);
				}
				warningPointNumTV.setText("点位：" + pntType);
				warningStateTV.setText("状态：" + alert.getAlertStatusMsg());
				warningValueTV.setText("累计超限：" + String.format("%1$.1f", CrtbUtils.formatDouble(alert.getUValue(), 1)) + AlertUtils.getAlertValueUnit(alert.getUType()));
                initPendixInfo(alert);				
				warningDateTV.setText(alert.getDate());
				warningMessageTV.setText(alert.getUTypeMsg());
				warningDealWayTV.setText(alert.getChuliFangshi());
				oldDateMileageTV.setText(Html.fromHtml("<font color=\"#0080ee\">里程: </font>" + alert.getXinghao()));
				oldDateListNumTV.setText(Html.fromHtml("<font color=\"#0080ee\">记录单号: </font>" + date));
				oldDatePointTV.setText(Html.fromHtml("<font color=\"#0080ee\">测点: </font>" + pntType));
				mCorrectionView.setText(String.valueOf(alert.getCorrection()));				
				int raidoId = 0;
				String remark = "";
				switch (view.getId()) {
				case R.id.warning_show_correction:
					raidoId = mDealWayBtnCorrection.getId();
					remark = getString(R.string.remark_correction, alert.getCorrection());
					break;
				// case R.id.normal:
				// raidoId = mDealWayBtnNormal.getId();
				// remark = getString(R.string.deal_way_normal);
				// break;
				// case R.id.discard_btn:
				// raidoId = mDealWayBtnDiscard.getId();
				// remark = getString(R.string.deal_way_void);
				// break;
				// case R.id.as_first_line:
				// raidoId = mDealWayBtnAsFirst.getId();
				// remark = getString(R.string.deal_way_first);
				// break;
				// case R.id.rebury:
				// raidoId = mDealWayBtnRebury.getId();
				// remark = getString(R.string.deal_way_rebury);
				// break;
				}

				if (raidoId != 0) {
					for (RadioButton b : mRadioBtns) {
						boolean selected = b.getId() == raidoId;
						b.setVisibility(selected ? View.VISIBLE : View.GONE);
						b.setChecked(selected);
					}
					boolean isCorrection = raidoId == mDealWayBtnCorrection.getId();
					mCorrectionUnitView.setVisibility(isCorrection ? View.VISIBLE : View.GONE);
					mCorrectionView.setVisibility(isCorrection ? View.VISIBLE : View.GONE);
					mCheckedRaidoId = raidoId;
				}

				if (mWarningRemarkView != null) {
					mWarningRemarkView.setText(remark);
				}
				setOpertationViewVisibility(false);
			}
		}
	}
	
	/**
	 * 获取本次变形的值
	 */
	private float getCurrentValue(AlertInfo alert){
		AlertList alertList = new AlertList();
		alertList.setUValue(alert.getUValue());
		alertList.setOriginalDataId(alert.getOriginalDataID());
		alertList.setCrossSectionId(alert.getSectionId());
		alertList.setPntType(alert.getPntType());
		alertList.setId(alert.getAlertId());
		double currentValue = AlertUtils.getRightCorrection(alertList, sectionType);
		currentValue = CrtbUtils.formatDouble(currentValue, 1);
		return (float)currentValue;
	}
	
	private void showHandlingDetail(){		
		if (curAlerts != null) {
			AlertInfo alert = curAlerts.get(clickedItem);
			Intent i = new Intent(WarningActivity.this, HandlingDetailsActivity.class);
			i.putExtra(HandlingDetailsActivity.EXTRA_ALERT_ID, alert.getAlertId());
			startActivity(i);
		}
	}
	
	private void finishHandling() {
		if (mCheckedRaidoId == mDealWayBtnCorrection.getId()
			// || mCheckedRaidoId == mDealWayBtnDiscard.getId()
		    // || mCheckedRaidoId == mDealWayBtnAsFirst.getId() ||
		    // || mCheckedRaidoId == mDealWayBtnNormal.getId()
			   || mCheckedRaidoId == mDealWayBtnRebury.getId()) {
			if (!handleAlert()) {
				return;
			}
		} 
		setOpertationViewVisibility(true);
	}
	
	private void cancelHandling() {
		mCheckedRaidoId = 0;
		setOpertationViewVisibility(true);
	}

	private void refreshData(String param) {
		showProgressOverlay();
		new RefreshTask().execute(param);
	}

	class RefreshTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			
			if(!isFirstEnter){
				String secName = params[0]; 
				curWarning = warings.get(secName);
				String secGuid = curWarning.getSectionGuid();
				curWarning.setComputed(false);
				curWarning.setAlerts(AlertUtils.getAlertInfoListBySectionGuid(secGuid));
			} else{
				isFirstEnter = false;
				warings = new HashMap<String,Warning>();
				sectionChainPrefix = new ArrayList<String>();
				
				ArrayList<AlertInfo> allAlerts = AlertUtils.getAlertInfoListBySectionType(sectionType);
				
				if(allAlerts != null && allAlerts.size() > 0){
					String secGuid;
					for(AlertInfo alert : allAlerts){
						Warning warnItem = null;
						secGuid = alert.getXinghao().trim();
						if (!sectionChainPrefix.contains(secGuid)) {
							sectionChainPrefix.add(secGuid);
						}
						if(!warings.containsKey(secGuid)){
							warnItem = new Warning();
							warings.put(secGuid, warnItem);
							warnItem.setSectionGuid(alert.getSectionId());
						} else{
							warnItem = warings.get(secGuid);
						}
						warnItem.addAlert(alert);
					}
					curSectionIndex = 0;
					curWarning = warings.get(sectionChainPrefix.get(0));
					sectionCount = sectionChainPrefix.size();
					Collections.sort(sectionChainPrefix, stringSort);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			refreshNum();
		}
	}

	class GetComputeValueTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			//获取预警详情
			for (AlertInfo alert : curAlerts) {
				AlertUtils.getAlertDetailInfo(alert);
			}
			curWarning.computed = true;
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			refreshBySection();
		}
	}

	class StringSort implements Comparator<String> {

		@Override
		public int compare(String lhs, String rhs) {
			double l = 0;
			double r = 0;
			String [] lArray;
			String [] rArray;
			try {
				lhs = lhs.replace(projectPrefix, "");
				lhs = lhs.replace("+",",");
				lArray = lhs.split(",");				
				l = Double.valueOf(lArray[0])*1000 + Double.valueOf(lArray[1]);
				
				rhs = rhs.replace(projectPrefix, "");
				rhs = rhs.replace("+",",");
				rArray = rhs.split(",");
				r = Double.valueOf(rArray[0])*1000 + Double.valueOf(rArray[1]);
				
			} catch (Exception e) {
				Log.e(Constant.LOG_TAG_ACTIVITY, TAG + "StringSort:" + e.getMessage());
			}
			if ((l - r) > 0){
				return 1;
			} else if(r == l){
				return 0;
			} else {
				return -1;
			}
		}
	}
	
	class Warning {
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
		public List<AlertInfo> getAlerts() {
			return alerts;
		}
		public void setAlerts(List<AlertInfo> alerts) {
			if(alerts == null){
				alerts = new ArrayList<AlertInfo>();
			}
			this.alerts = alerts;
		}
		
		public void addAlert(AlertInfo alert) {
			if(alerts == null){
				alerts = new ArrayList<AlertInfo>();
			}
			alerts.add(alert);
		}
		private boolean computed;
		private String sectionGuid;
		private List<AlertInfo> alerts;
	}
		
	private void initPendixInfo(AlertInfo alert){
		boolean isSpeed = false;
		String unit = AlertUtils.getAlertValueUnit(alert.getUType());
		double intervalFactor = 0.1;
		String rockGradeStr = alert.getRockGrade();
		rockGrade.setText(Html.fromHtml("<font color=\"#0080ee\">围岩等级: </font>" + rockGradeStr));
		int rockLimit = Constant.LEI_JI_OFFSET_LEVEL_BASE[CrtbUtils.getRockgrade(rockGradeStr)];
		alertLimit.setText(Html.fromHtml("<font color=\"#0080ee\">围岩阈值: </font>" + rockLimit + "毫米"));
		double originalValue = 0;
		if(AlertUtils.isSpeed(alert.getUType())){
			originalValue = alert.getOriginalSulvAlertValue();
			noDealAlertValue.setText(Html.fromHtml("<font color=\"#ff0000\">处理前速率: </font>" + originalValue + unit));
			isSpeed = true;
		} else {
			originalValue = alert.getOriginalLeiJiAlertValue();
			noDealAlertValue.setText(Html.fromHtml("<font color=\"#ff0000\">处理前累计: </font>" + originalValue + unit));
		}
		
		float currentChangedValue = getCurrentValue(alert);
		currentValue.setText(Html.fromHtml("<font color=\"#ff0000\">本次变形量: </font>" + currentChangedValue + "毫米"));
		
		double safeValue = rockLimit - intervalFactor; 
		double recommand =0;
		if (originalValue < 0){
			recommand = originalValue + safeValue;
		} else {
			recommand = originalValue - safeValue;
		}
		recommand *= -1;
		
		recommand = CrtbUtils.formatDouble(recommand, 1);
		if(isSpeed){
			recommand = 0;
		}
		recommandValue.setText(Html.fromHtml("<font color=\"#ff0000\">理论修正量: </font>" + recommand + "毫米"));
	}
}