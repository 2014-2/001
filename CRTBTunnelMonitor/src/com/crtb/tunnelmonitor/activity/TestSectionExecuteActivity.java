package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;
import org.zw.android.framework.util.DateUtils;
import org.zw.android.framework.util.StringUtils;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.ListView;

import com.crtb.tssurveyprovider.Coordinate3D;
import com.crtb.tssurveyprovider.ISurveyProvider;
import com.crtb.tssurveyprovider.TSSurveyProvider;
import com.crtb.tunnelmonitor.AppConfig;
import com.crtb.tunnelmonitor.AppHandler;
import com.crtb.tunnelmonitor.CommonObject;
import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertHandlingInfoDao;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertListDao;
import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceTotalDataDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.entity.AlertList;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;
import com.crtb.tunnelmonitor.entity.SurveyerInformation;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogConnecting;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogDelete;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogDelete.IButtonOnClick;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogHint;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogTest;
import com.crtb.tunnelmonitor.utils.AlertUtils;
import com.crtb.tunnelmonitor.utils.AlertUtils.OffsetLevel;
import com.crtb.tunnelmonitor.utils.SectionInterActionManager;
import com.crtb.tunnelmonitor.utils.Time;
import com.crtb.tunnelmonitor.widget.SlidingLayout;

/**
 * 开始测量
 * 
 * @author YaoXu
 */
@InjectLayout(layout = R.layout.activity_testrecord_execute)
public class TestSectionExecuteActivity extends WorkFlowActivity implements
		View.OnClickListener {

	private ArrayList<String> crownList;
	private ArrayList<String> pointList;
	private int excavateMethodType;
	private int sectionType;
	private boolean isFirstEnter;

	private ArrayList<String> pointTypeNameList = new ArrayList<String>();

	private static boolean DEBUG;// DEBUG FLAG, set to true to generate
										// fake data

	// 测量列表
	public static final String KEY_TEST_RAWSHEET_LIST = "_key_test_rawsheet_list";
	static final int MSG_ERROR_CONNECT = 1;
	static final int MSG_TEST_ERROR = 2;
	static final int MSG_TEST_SUCCESS = 3;
	static final int MSG_ERROR_NOT_TEST = 4;
	static final int MSG_ERROR_NOT_RESET = 5;
	static final int MSG_ERROR_TS = 6;

	@InjectView(id = R.id.test_bottom_layout)
	private LinearLayout mBottomLayout;

	@InjectView(id = R.id.test_bnt_left_section)
	private RelativeLayout mLeftBnt;

	@InjectView(id = R.id.test_bnt_next_section)
	private RelativeLayout mNextBnt;

	@InjectView(id = R.id.test_container_layout)
	private LinearLayout mContainerLayout;

	@InjectView(id = R.id.test_point_List)
	private ListView testPointListView;

	private ArrayAdapter<String> testPointListAdapter;

	@InjectView(id = R.id.left_container)
	private View left_container;

	@InjectView(id = R.id.test_container_scroll)
	private ScrollView test_container_scroll;

	@InjectView(id = R.id.slidinglayout)
	private SlidingLayout slidingLayout;

	@InjectView(id = R.id.choose_test_point_list)
	private TextView choose_test_point_list;

	private int rawSheetIndex;// 当前测量单索引
	private RawSheetIndex rawSheetBean;// 当前测量单
	private SurveyerInformation surveyer; // 测量人员
	private List<RawSheetIndex> rawSheets; // 测量数据
	private boolean rawSheetCanTest; // 是否能够测量
	private ShowInfo showInfo ;
	private int sectionIndex = 0;
	private int sectionCount;
	private TunnelCrossSectionIndex tunnelSection;
	private SubsidenceCrossSectionIndex subsidenceSection;
	private final List<TunnelCrossSectionIndex> tunnelSectionList = new ArrayList<TunnelCrossSectionIndex>();
	private final List<SubsidenceCrossSectionIndex> subsidenceSectionList = new ArrayList<SubsidenceCrossSectionIndex>();

	// 测量临时数据
	private final List<TunnelSettlementTotalData> tempTunnelData = new ArrayList<TunnelSettlementTotalData>();
	private final List<SubsidenceTotalData> tempSubsidenceData = new ArrayList<SubsidenceTotalData>();

	private CrtbDialogConnecting connectDialog;
	// 测点view
	private List<TestPointHolder> TestViewHolder = new ArrayList<TestPointHolder>();

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// add by wei.zhou
		InjectCore.injectUIProperty(this);
		
		DEBUG = Constant.getStationDebug();

		Object bean = CommonObject.findObject(KEY_TEST_RAWSHEET_LIST);

		rawSheetIndex = 0;
		rawSheetBean = null;
		tempTunnelData.clear();
		tempSubsidenceData.clear();
		TestViewHolder.clear();

		// 测量数据
		if (bean instanceof List<?>) {
			rawSheets = (List<RawSheetIndex>) bean;
			rawSheetBean = rawSheets.get(rawSheetIndex);
			surveyer = RawSheetIndexDao.defaultDao()
					.querySurveyerBySheetIndexGuid(rawSheetBean.getGuid());
		}
		try {
			initTestPointInfo();
			loadRawSheetIndexInfo(rawSheetBean);
		} catch (Exception e) {
			showText(e.getMessage());
		}
	}

	private void initTestPointInfo() {
		testPointListAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, pointTypeNameList);
		testPointListView.setAdapter(testPointListAdapter);

		slidingLayout.setScrollEvent(left_container); // 将监听滑动事件绑定在left_container上

		// YX 滚动选择的测点到当前位置
		slidingLayout.setLayoutLoadedEvent(new MyLayoutFirstLoadEvent());

		testPointListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				scrollTestViewHolderToTop(position);
			}
		});
		left_container.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				slidingLayout.scrollToRightLayout();
			}
		});

	}

	/**
	 * 清理数据
	 */
	private void clearData() {
		mContainerLayout.removeAllViews();
		pointTypeNameList.clear();
		TestViewHolder.clear();
		isFirstEnter = true;
	}

	/**
	 * 加载记录单信息
	 * 
	 * @param bean
	 *            记录单数据
	 */
	private void loadRawSheetIndexInfo(RawSheetIndex bean) {
		rawSheetCanTest = false;

		if (bean == null) {
			showText("没有测量数据");
			return;
		}

		sectionType = rawSheetBean.getCrossSectionType();

		rawSheetCanTest = RawSheetIndexDao.defaultDao().isNewestRawSheetIndex(
				bean);
		int type = bean.getCrossSectionType();
		String sectionIds = bean.getCrossSectionIDs();

		if (StringUtils.isEmpty(sectionIds)) {
			showText("没有断面信息");
		} else {

			String[] ids = sectionIds.split(",");

			// 加载断面信息
			loadSections(ids, type);
		}
	}

	private void loadSections(String[] ids, int sectionType) {

		if (sectionType == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL) {
			TunnelCrossSectionIndexDao dao = TunnelCrossSectionIndexDao
					.defaultDao();
			for (String id : ids) {
				TunnelCrossSectionIndex bean = dao.querySectionIndexByGuid(id);
				if (bean != null) {
					tunnelSectionList.add(bean);
				}
			}
			sectionCount = tunnelSectionList.size();
			if (sectionCount > 0) {
				tunnelSection = tunnelSectionList.get(0);
			}
		} else if (sectionType == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES) {
			SubsidenceCrossSectionIndexDao dao = SubsidenceCrossSectionIndexDao
					.defaultDao();
			for (String id : ids) {
				SubsidenceCrossSectionIndex bean = dao
						.querySectionIndexByGuid(id);
				if (bean != null) {
					subsidenceSectionList.add(bean);
				}
			}
			sectionCount = subsidenceSectionList.size();
			if (sectionCount > 0) {
				subsidenceSection = subsidenceSectionList.get(0);
			}
		}

		if (sectionCount > 0) {
			sectionIndex = 0;
			mLeftBnt.setOnClickListener(this);
			mNextBnt.setOnClickListener(this);
			loadSectionTestData();
		}

	}

	/**
	 * 滚动测点界面到最上面
	 * 
	 * @param position
	 *            选择的测点的position
	 */
	private void scrollTestViewHolderToTop(int position) {
		String pntType = "";
		int accumulateHeight = 0;
		if (pointTypeNameList != null) {
			if (position > -1 && position < pointTypeNameList.size()) {
				pntType = pointTypeNameList.get(position);
			}
		}

		if (TestViewHolder != null && pntType != null) {
			accumulateHeight = 0;
			for (TestPointHolder holder : TestViewHolder) {

				if (holder.type.equals(pntType)) {
					test_container_scroll.scrollTo(0, accumulateHeight);
					if (isFirstEnter) {
						isFirstEnter = false;
						return;
					}
					slidingLayout.scrollToRightLayout();
					break;
				}
				accumulateHeight += holder.mItemView.getMeasuredHeight();
			}
		}
	}

	/**
	 * 首次加载事件，用于初始化时，显示某一面（左菜单，右内容）的数据
	 * 
	 * @author xu
	 * 
	 */
	private class MyLayoutFirstLoadEvent implements
			SlidingLayout.LayouFirstLoadedEvent {
		@Override
		public void callback() {
			slidingLayout.scrollToRightLayout();
		}
	}

	private TestPointHolder createTunnelTestPointView(
			final TunnelSettlementTotalData bean,
			final TestPointHolder holder1, final String type, String typeName,
			String suffix) {

		final TestPointHolder holder = new TestPointHolder();
		View view = InjectCore.injectOriginalObject(holder);
		holder.mItemView = view;

		// 添加到列表
		holder.sheetGuid = rawSheetBean.getGuid();
		holder.sectionGuid = tunnelSection.getGuid();
		holder.type = type;
		TestViewHolder.add(holder);
		mContainerLayout.addView(view);

		// 从已经测量的数据中查找
		if (bean != null) {
			// 存在的测量数据
			String coord = bean.getCoordinate() != null ? bean.getCoordinate() : "";
			refreshCoordinateData(coord, bean.getSurveyTime(), holder);
		}

		// 测点类型
		if (StringUtils.isEmpty(typeName)) {
			holder.mPointType.setText(type);
		} else {
			if (StringUtils.isEmpty(suffix)) {
				holder.mPointType.setText(typeName);
			} else {
				holder.mPointType.setText(typeName + "-" + suffix);
			}
		}
		
		String typeNameShow = (String) holder.mPointType.getText();
		pointTypeNameList.add(typeNameShow);

		// 测量
		holder.mPointTestBnt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (bean != null && bean.getUploadStatus() == 2) {
					showText("数据已经上传,不能测量");
					return;
				}

				// 测量
				connectSurveyProvider(holder, holder1, type,RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL);
			}
		});

		// 使测量清除按钮不可用
		if (bean == null || bean.getUploadStatus() == 2 || !rawSheetCanTest) {
			holder.mPointResetBnt.setEnabled(false);
		}

		// 清除
		holder.mPointResetBnt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!rawSheetCanTest) {
					mHanlder.sendMessage(MSG_ERROR_NOT_RESET);
					return;
				}

				final TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao
						.defaultDao();

				TunnelSettlementTotalData data = dao.queryTunnelTotalData(
						rawSheetBean.getGuid(), tunnelSection.getGuid(), type);

				if (data == null) {
					showText("该测点不存在观测数据!");
					return;
				}

				if (data.getUploadStatus() == 2) {
					showText("数据已经上传,不能删除!");
					return;
				}

				CrtbDialogDelete delete = new CrtbDialogDelete(
						TestSectionExecuteActivity.this,
						R.drawable.ic_warnning, "执行该操作将删除该点的观测数据，无法恢复!");

				delete.setButtonClick(new IButtonOnClick() {

					@Override
					public void onClick(int id) {

						if (id == CrtbDialogDelete.BUTTON_ID_CONFIRM) {

							String sheetId = rawSheetBean.getGuid();
							String chainageid = tunnelSection.getGuid();

							TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao
									.defaultDao();
							TunnelSettlementTotalData obj = dao
									.queryTunnelTotalData(sheetId, chainageid,
											type);

							if (obj != null) {

								// dao.reset(obj);
								dao.delete(obj);

								// 删除预警
								deleteTunnelAlertInfo(sheetId, chainageid,
										obj.getGuid());

								holder.mPointX.setText("");
								holder.mPointY.setText("");
								holder.mPointZ.setText("");
								holder.mPointTime.setText("");
								holder.warringLayout.removeAllViews();
								holder.warringLayout
										.setVisibility(View.INVISIBLE);
								holder.mPointResetBnt.setEnabled(false);
							}
						}
					}
				});

				delete.show();
			}
		});

		return holder;
	}

	/**
	 * 删除隧道内预警信息
	 */
	private void deleteTunnelAlertInfo(String sheetId, String chainageid,
			String guid) {

		if (sheetId == null || chainageid == null || guid == null) {
			return;
		}

		List<AlertList> als = AlertListDao.defaultDao().queryByOrigionalDataId(
				String.valueOf(sheetId), chainageid, guid);

		if (als != null && als.size() > 0) {
			for (AlertList al : als) {
				if (al.getUploadStatus() != 2) {
					int alId = al.getId();
					AlertHandlingInfoDao.defaultDao().deleteByAlertId(alId);
					AlertListDao.defaultDao().deleteById(alId);
				}
			}
		}
	}

	/**
	 * 删除地表下沉预警
	 * 
	 * @param sheetId
	 * @param chainageid
	 * @param guid
	 */
	private void deleteSubsidenceAlertInfo(String sheetId, String chainageid,
			String guid) {

		if (sheetId == null || chainageid == null || guid == null) {
			return;
		}

		List<AlertList> als = AlertListDao.defaultDao().queryByOrigionalDataId(
				sheetId, chainageid, guid);

		if (als != null && als.size() > 0) {

			for (AlertList al : als) {
				if (al.getUploadStatus() != 2) {
					int alId = al.getId();
					AlertHandlingInfoDao.defaultDao().deleteByAlertId(alId);
					AlertListDao.defaultDao().deleteById(alId);
				}
			}
		}
	}

	// 异步测量
	private void connectSurveyProvider(final TestPointHolder holder,
			final TestPointHolder holde1, final String type,
			final int sectionType) {

		// 暂时去掉优化
		/*
		 * ExecuteAsyncTaskImpl.defaultSyncExecutor().executeTask(new
		 * BaseAsyncTask(mHanlder){
		 * 
		 * @Override public void process() { } }) ;
		 */

		if (!rawSheetCanTest) {
			mHanlder.sendMessage(MSG_ERROR_NOT_TEST);
			return;
		}

		if (rawSheetBean != null && rawSheetBean.getUploadStatus() == 2) {
			CrtbDialogHint hint = new CrtbDialogHint(this,
					R.drawable.ic_warnning, "数据已上传，不能重测!");
			hint.show();
			return;
		}

		String x, y, z;

		if (!DEBUG) {

			ISurveyProvider ts = TSSurveyProvider.getDefaultAdapter();

			if (ts == null) {
				mHanlder.sendMessage(MSG_ERROR_CONNECT);
				return;
			}

			Coordinate3D point = new Coordinate3D(null);

			int nret = ts.GetCoord(0, 0, point);

			if (nret != 1) {
				mHanlder.obtainMessage(MSG_ERROR_TS, nret, 0).sendToTarget();
				return;
			}

			if (point.N == 0d || point.E == 0d || point.H == 0d) {
				mHanlder.sendMessage(MSG_ERROR_CONNECT);
				return;
			}

			x = String.format("%1$.4f", point.N);
			y = String.format("%1$.4f", point.E);
			z = String.format("%1$.4f", point.H);

		} else {

			/*
			 * x = String.format("%1$.4f", X - COUNT * 0.05); y =
			 * String.format("%1$.4f", Y - COUNT * 0.05); z =
			 * String.format("%1$.4f", Z - COUNT * 0.05); COUNT++;
			 */

			// for debug
			CrtbDialogTest.Callback callback = new CrtbDialogTest.Callback() {

				@Override
				public void callback(double xx, double yy, double zz,
						String time) {

					TestInfo info = new TestInfo();
					info.holder = holder;
					info.holder1 = holde1;
					info.type = type;// 测点类型
					info.sectionType = sectionType;// 断面类型

					info.x = String.format("%1$.4f", xx);
					info.y = String.format("%1$.4f", yy);
					;
					info.z = String.format("%1$.4f", zz);
					info.time = time;

					mHanlder.sendMessage(MSG_TEST_SUCCESS, info);
				}
			};

			CrtbDialogTest diglog = new CrtbDialogTest(
					TestSectionExecuteActivity.this, callback);
			diglog.show();

			return;
		}

		String time = Time.getDateEN();

		TestInfo info = new TestInfo();
		info.holder = holder;
		info.holder1 = holde1;
		info.type = type;// 测点类型
		info.sectionType = sectionType;// 断面类型

		info.x = x;
		info.y = y;
		info.z = z;
		info.time = time;

		mHanlder.sendMessage(MSG_TEST_SUCCESS, info);
	}

	@Override
	protected AppHandler getHandler() {
		return new AppHandler(this) {

			@Override
			protected void dispose(Message msg) {

				switch (msg.what) {
				case MSG_TASK_START:

					if (connectDialog == null) {
						connectDialog = new CrtbDialogConnecting(
								TestSectionExecuteActivity.this);
					}

					if (!connectDialog.isShowing()) {
						connectDialog.showDialog("正在从全站仪获取数据!");
					}
					break;
				case MSG_TASK_END:

					if (connectDialog != null) {
						connectDialog.dismiss();
					}

					break;
				case MSG_ERROR_CONNECT:
					showText("请先连接全站仪");
					break;
				case MSG_TEST_ERROR:
					showText("测量失败");
					break;
				case MSG_ERROR_NOT_TEST:
					showText("该记录不是最新记录单,不能测量");
					break;
				case MSG_ERROR_NOT_RESET:
					showText("该记录不能被删除");
					break;
				case MSG_ERROR_TS:
					showText(AppConfig.getTSErrorCode(msg.arg1));
					break;
				case MSG_TEST_SUCCESS:

					TestInfo info = (TestInfo) msg.obj;
					
					info.holder.mPointX.setText(info.x);
					info.holder.mPointY.setText(info.y);
					info.holder.mPointZ.setText(info.z);
					info.holder.mPointTime.setText(info.time);
					
					// 按钮可用
					if (info.holder != null) {
						info.holder.mPointResetBnt.setEnabled(true);
					}
					showInfo = new ShowInfo();
					showInfo.holder = info.holder;

					// 隧道内断面
					if (info.sectionType == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL) {

						TunnelSettlementTotalData obj = new TunnelSettlementTotalData();
						// DTMS DBDesign_V1.7.9_20140604 - 修改追踪表.xlsx
						// obj.setStationId(AppCRTBApplication.getInstance().getCurUsedStationId());
						obj.setStationId("");
						// DTMS DBDesign_V1.7.9_20140604 - 修改追踪表.xlsx
						obj.setChainageId(tunnelSection.getGuid());
						// DTMS DBDesign_V1.7.9_20140604 - 修改追踪表.xlsx
						obj.setSheetId(rawSheetBean.getGuid());
						obj.setPntType(info.type); // 测量点类型
						obj.setSurveyorID(surveyer.getCertificateID());// 测量人员id
						// DTMS DBDesign_V1.7.9_20140604 - 修改追踪表.xlsx
						// obj.setInfo("1");
						obj.setUploadStatus(1);

						TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao
								.defaultDao();

						obj.setCoordinate(info.x + "," + info.y + "," + info.z);
						obj.setSurveyTime(DateUtils.toDate(info.time,
								DateUtils.DATE_TIME_FORMAT));

						int err = TunnelSettlementTotalDataDao.DB_EXECUTE_FAILED;
						boolean update = false;
						// 存在的测量点信息
						TunnelSettlementTotalData old = dao
								.queryTunnelTotalData(rawSheetBean.getGuid(),
										tunnelSection.getGuid(), info.type);
						if (old != null) {
							// UPDATE
							obj.setMEASNo(1);
							obj.setID(old.getID());
							obj.setDataStatus(old.getDataStatus());
							err = dao.update(obj);
							update = true;
							showInfo.tunnelOld = old;
							showInfo.tunnelCur = obj;
						} else {
							// INSERT NEW
							obj.setDataStatus(0);
							obj.setMEASNo(1); // 始终为1
							err = dao.insert(obj);
							update = false;
							showInfo.tunnelCur = obj;
						}

						if (err == TunnelSettlementTotalDataDao.DB_EXECUTE_SUCCESS) {

							// 保存测量数据
							// tempTunnelData.add(obj);

							// 删除隧道内断面预警
							deleteTunnelAlertInfo(rawSheetBean.getGuid(),
									tunnelSection.getGuid(),
									old != null ? old.getGuid() : obj.getGuid());

//							if (info.holder != null) {
//								info.holder.warringLayout.removeAllViews();
//							}
//
//							if (info.holder1 != null) {
//								info.holder1.warringLayout.removeAllViews();
//							}

							// YX 保存预警时判断点位信息
							TunnelSettlementTotalData p1 = null, p2 = null;
							if (crownList.contains(info.type)) {
								doWarning(info.holder, info.holder1,AlertUtils.getPointSubsidenceExceedMsg(obj, false,tunnelSection.getROCKGRADE()));
							} else if (pointList.contains(info.type)) {
								int index = pointList.indexOf(info.type);
								int p1Index;
								int p2Index;
								if (index % 2 == 0) {
									p1Index = index;
									p2Index = index + 1;
								} else {
									p1Index = index - 1;
									p2Index = index;
								}
								p1 = dao.queryTunnelTotalData(rawSheetBean.getGuid(),tunnelSection.getGuid(),pointList.get(p1Index));
								p2 = dao.queryTunnelTotalData(rawSheetBean.getGuid(),tunnelSection.getGuid(),pointList.get(p2Index));
								if (p1 != null && p2 != null) {
									doWarningLine(tunnelSection.getGuid(),info.type, p1, p2, false);
								}
							}
							showText((update ? "更新" : "保存") + "成功");
						} else {
							showText((update ? "更新" : "保存") + "失败");
						}
					}
					// 地表下沉
					else {

						SubsidenceTotalDataDao dao = SubsidenceTotalDataDao
								.defaultDao();

						// 存在的测量点信息
						SubsidenceTotalData old = dao.querySubsidenceTotalData(
								rawSheetBean.getGuid(),
								subsidenceSection.getGuid(), info.type);

						final SubsidenceTotalData obj = new SubsidenceTotalData();
						// DTMS DBDesign_V1.7.9_20140604 - 修改追踪表.xlsx
						// obj.setStationId(AppCRTBApplication.getInstance().getCurUsedStationId());
						obj.setStationId("");
						// DTMS DBDesign_V1.7.9_20140604 - 修改追踪表.xlsx
						obj.setChainageId(subsidenceSection.getGuid());
						// DTMS DBDesign_V1.7.9_20140604 - 修改追踪表.xlsx
						obj.setSheetId(rawSheetBean.getGuid());
						obj.setPntType(info.type); // 测量点类型
						obj.setSurveyorID(surveyer.getCertificateID());// 测量人员id

						obj.setCoordinate(info.x + "," + info.y + "," + info.z);
						obj.setSurveyTime(DateUtils.toDate(info.time,
								DateUtils.DATE_TIME_FORMAT));
						obj.setUploadStatus(1);// 表示该测点未上传
						int err = SubsidenceTotalDataDao.DB_EXECUTE_FAILED;
						boolean update = false;

						if (old != null) {
							// UPDATE
							// obj.setMEASNo(old.getMEASNo());// 测量次数始终为1
							obj.setMEASNo(1);
							obj.setID(old.getID());
							obj.setDataStatus(old.getDataStatus());
							err = dao.update(obj);
							update = true;
							showInfo.subOld = old;
							showInfo.subCur = obj;
						} else {
							// INSERT NEW
							obj.setDataStatus(0);
							obj.setMEASNo(1);// 测量次数始终为1
							err = dao.insert(obj);
							update = false;
							showInfo.subCur = obj;
						}

						if (err == SubsidenceTotalDataDao.DB_EXECUTE_SUCCESS) {

							// 保存临时数据
							// tempSubsidenceData.add(obj);

							// 删除下沉预警
							deleteSubsidenceAlertInfo(rawSheetBean.getGuid(),
									subsidenceSection.getGuid(),
									old != null ? old.getGuid() : obj.getGuid());

//							if (info.holder != null) {
//								info.holder.warringLayout.removeAllViews();
//							}
//
//							if (info.holder1 != null) {
//								info.holder1.warringLayout.removeAllViews();
//							}

							doWarning(info.holder, info.holder1,
									AlertUtils.getPointSubsidenceExceedMsg(obj,
											false,subsidenceSection.getROCKGRADE()));
							showText((update ? "更新" : "保存") + "成功");

						} else {
							showText((update ? "更新" : "保存") + "失败");
						}
					}

					break;
				}
			}

		};
	}

	private void doWarningLine(String sectionGuid, String type,
			TunnelSettlementTotalData p1, TunnelSettlementTotalData p2,
			boolean readOnly) {

		if (sectionGuid == null || type == null) {
			return;
		}

		OffsetLevel[] list = AlertUtils
				.getLineConvergenceExceedMsg(p1, p2, readOnly,tunnelSection.getROCKGRADE());

		if (list == null || list.length == 0) {
			return;
		}

		TestPointHolder view1 = null, view2 = null;
		
		// YX 获取点的类型
		String point1 = p1.getPntType();
		String point2 = p2.getPntType();
		
		String sheetid = rawSheetBean.getGuid();

		for (TestPointHolder view : TestViewHolder) {

			if (view.sheetGuid.equals(sheetid)
					&& sectionGuid.equals(view.sectionGuid)) {

				if (view.type.equals(point1)) {
					view1 = view;
				} else if (view.type.equals(point2)) {
					view2 = view;
				}
				if(view1 != null && view2 != null){
					break;
				}
			}
		}

		// 处理超限
		doWarning(view1, view2, list);
	}

	private void doWarning(TestPointHolder view1, TestPointHolder view2,
			OffsetLevel[] list) {

		if (list == null || list.length == 0) {
			return; 
		}
		
		if (list[Constant.LEI_JI_INDEX].IsLargerThanMaxValue) {
			checkLeijiWarnningBiggerThanMax();
			return;
		}

		if (view1 != null) {
			view1.warringLayout.removeAllViews();
			view1.warringLayout.setVisibility(View.INVISIBLE);
		}

		if (view2 != null) {
			view2.warringLayout.removeAllViews();
			view2.warringLayout.setVisibility(View.INVISIBLE);
		}

		for (OffsetLevel msg : list) {

			TextView tv = new TextView(this);
			TextView tv2 = new TextView(this);

			tv.setTextColor(msg.TextColor);
			tv.setTextSize(12);

			tv2.setTextColor(msg.TextColor);
			tv2.setTextSize(12);

			if (!StringUtils.isEmpty(msg.Content)) {

				tv.setText(msg.Content);
				tv2.setText(msg.Content);

				if (view1 != null) {
					view1.warringLayout.setVisibility(View.VISIBLE);
					view1.warringLayout.addView(tv);
				}

				if (view2 != null) {
					view2.warringLayout.setVisibility(View.VISIBLE);
					view2.warringLayout.addView(tv2);
				}
			}
		}
	}

	private TestPointHolder createSubsidenceTestPointView(
			final SubsidenceTotalData bean, final String type) {

		final TestPointHolder holder = new TestPointHolder();

		View view = InjectCore.injectOriginalObject(holder);

		// 测点类型
		holder.mPointType.setText(type);
		holder.mItemView = view;
		pointTypeNameList.add(type);

		// 添加到列表
		holder.sheetGuid = rawSheetBean.getGuid();
		holder.sectionGuid = subsidenceSection.getGuid();
		holder.type = type;
		TestViewHolder.add(holder);
		mContainerLayout.addView(view);

		if (bean != null) {

			String coord = bean.getCoordinate() != null ? bean.getCoordinate()
					: "";

			// 默认值
			String[] str = coord.split(",");
			if (str.length == 3) {
				holder.mPointX.setText(str[0]);
				holder.mPointY.setText(str[1]);
				holder.mPointZ.setText(str[2]);
			}

			Date t = bean.getSurveyTime();

			// 测试时间
			holder.mPointTime.setText(t != null ? DateUtils.toDateString(t,
					DateUtils.DATE_TIME_FORMAT) : "");
		}

		// 测量
		holder.mPointTestBnt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (bean != null && bean.getUploadStatus() == 2) {
					showText("数据已经上传,不能测量");
					return;
				}

				// 测量
				connectSurveyProvider(holder, null, type,
						RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES);
			}
		});

		// 使测量清除按钮不可用
		if (bean == null || bean.getUploadStatus() == 2 || !rawSheetCanTest) {
			holder.mPointResetBnt.setEnabled(false);
		}

		// 清除
		holder.mPointResetBnt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!rawSheetCanTest) {
					mHanlder.sendMessage(MSG_ERROR_NOT_RESET);
					return;
				}

				SubsidenceTotalDataDao dao = SubsidenceTotalDataDao
						.defaultDao();

				SubsidenceTotalData data = dao.querySubsidenceTotalData(
						rawSheetBean.getGuid(), subsidenceSection.getGuid(),
						type);

				if (data == null) {
					showText("该测点不存在观测数据!");
					return;
				}

				if (data.getUploadStatus() == 2) {
					showText("数据已经上传,不能删除!");
					return;
				}

				CrtbDialogDelete delete = new CrtbDialogDelete(
						TestSectionExecuteActivity.this,
						R.drawable.ic_warnning, "执行该操作将删除该点的观测数据，无法恢复!");

				delete.setButtonClick(new IButtonOnClick() {

					@Override
					public void onClick(int id) {

						if (id == CrtbDialogDelete.BUTTON_ID_CONFIRM) {

							SubsidenceTotalDataDao dao = SubsidenceTotalDataDao
									.defaultDao();
							String sheetId = rawSheetBean.getGuid();
							String chainageid = subsidenceSection.getGuid();
							SubsidenceTotalData obj = dao
									.querySubsidenceTotalData(sheetId,
											chainageid, type);

							if (obj != null) {

								// dao.reset(obj);
								dao.delete(obj);

								// 删除预警
								deleteSubsidenceAlertInfo(sheetId, chainageid,
										obj.getGuid());

								holder.mPointX.setText("");
								holder.mPointY.setText("");
								holder.mPointZ.setText("");
								holder.mPointTime.setText("");
								holder.warringLayout.removeAllViews();
								holder.warringLayout
										.setVisibility(View.INVISIBLE);
								holder.mPointResetBnt.setEnabled(false);
							}
						}
					}
				});

				delete.show();

			}
		});

		return holder;
	}

	/**
	 * 加载测量数据
	 */
	private void loadSectionTestData() {
		clearData();
		generateSectionTestData();
		testPointListAdapter.notifyDataSetChanged();
		scrollTestViewHolderToTop(0);
	}

	/**
	 * 生成断面的测量数据，并添加到界面上
	 */
	private void generateSectionTestData() {
		if (sectionType == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL) {

			TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao
					.defaultDao();
			excavateMethodType = tunnelSection.getExcavateMethod();

			TestPointHolder holder1 = null;
			TestPointHolder holder = null;

			if (excavateMethodType < 0) {
				showText("无效开挖类型: " + excavateMethodType);
				return;
			}

			// title
			setTopbarTitle(tunnelSection.getSectionName() + " ("
					+ (sectionIndex + 1) + "/" + tunnelSectionList.size() + ")");

			TunnelSettlementTotalData bean = null;
			TunnelSettlementTotalData p1 = null, p2 = null;

			// YX 测量时，根据断面开挖方法，获取测点信息
			SectionInterActionManager sectionInterActionManager = new SectionInterActionManager(
					excavateMethodType);
			crownList = sectionInterActionManager.getCrownPointListByNumber();
			pointList = sectionInterActionManager.getSurveyPointNameByNumber();

			// 获取拱顶列表
			if (crownList != null) {
				for (String crown : crownList) {
					bean = dao.queryTunnelTotalData(rawSheetBean.getGuid(),
							tunnelSection.getGuid(), crown);
					holder = createTunnelTestPointView(bean, null, crown,crown, "");
					if (bean != null) {
						doWarning(holder, null,AlertUtils.getPointSubsidenceExceedMsg(bean,true,tunnelSection.getROCKGRADE()));
					}
				}
			}

			if (pointList != null) {

				// 获取测线的点位列表
				int count = pointList.size();
				String pointName = "";
				for (int i = 0; i < count; i++) {
					pointName = pointList.get(i);
					bean = dao.queryTunnelTotalData(rawSheetBean.getGuid(),tunnelSection.getGuid(), pointName);
					holder1 = createTunnelTestPointView(bean, null, pointName, pointName, "");

					if (i % 2 == 0) { // 测线的第一个点
						p1 = bean;
					} else { // 沿线的第二个点
						p2 = bean;
						if (p1 != null && p2 != null) {
							doWarningLine(tunnelSection.getGuid(),pointList.get(i - 1), p1, p2, true);
						}

					}

				}
			}
		}
		// 地表下沉断面测量---临时值
		else if (sectionType == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES) {

			int size = subsidenceSection.getSurveyPnts();

			// title
			setTopbarTitle(subsidenceSection.getSectionName() + " ("
					+ (sectionIndex + 1) + "/" + subsidenceSectionList.size()
					+ ")");

			SubsidenceTotalDataDao dao = SubsidenceTotalDataDao.defaultDao();
			SubsidenceTotalData bean = null;

			for (int id = 0; id < size; id++) {

				String index = String.valueOf(id + 1);

				bean = dao.querySubsidenceTotalData(rawSheetBean.getGuid(),
						subsidenceSection.getGuid(), index);

				TestPointHolder item = createSubsidenceTestPointView(bean,
						index);

				// 预警信息
				if (bean != null) {
					doWarning(item, null,
							AlertUtils.getPointSubsidenceExceedMsg(bean, true,subsidenceSection.getROCKGRADE()));
				}
			}
		}
	}

	/**
	 * 上一个断面
	 */
	private void preSection() {
		if (rawSheetBean == null) {
			return;
		}
		// 当前测量单类型
		sectionType = rawSheetBean.getCrossSectionType();

		if (sectionType == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL) {
			if (sectionIndex <= 0) {
				showText("已经是第一个断面");
				return;
			} else {
				sectionIndex--;
				tunnelSection = tunnelSectionList.get(sectionIndex);
			}
		} else if (sectionType == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES) {
			if (sectionIndex <= 0) {
				showText("已经是第一个断面");
				return;
			} else {
				sectionIndex--;
				subsidenceSection = subsidenceSectionList.get(sectionIndex);
			}
		}
		loadSectionTestData();
	}

	/**
	 * 下一个断面
	 */
	private void nextSection() {
		if (rawSheetBean == null) {
			return;
		}

		// 当前测量单类型
		sectionType = rawSheetBean.getCrossSectionType();

		if (sectionType == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL) {
			if (sectionIndex >= sectionCount - 1) {
				showText("已经是最后一个断面");
				return;
			} else {
				sectionIndex++;
				tunnelSection = tunnelSectionList.get(sectionIndex);
			}
		} else if (sectionType == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES) {

			// 最后一个断面
			if (sectionIndex >= sectionCount - 1) {
				showText("已经是最后一个断面");
				return;
			} else {
				sectionIndex++;
				subsidenceSection = subsidenceSectionList.get(sectionIndex);
			}
		}
		loadSectionTestData();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.test_bnt_left_section:
			preSection();
			break;
		case R.id.test_bnt_next_section:
			nextSection();
			break;
		}
	}

	@InjectLayout(layout = R.layout.test_record_point_layout)
	final class TestPointHolder {

		@InjectView(id = R.id.test_point_type)
		TextView mPointType;

		@InjectView(id = R.id.test_point_x)
		TextView mPointX;

		@InjectView(id = R.id.test_point_y)
		TextView mPointY;

		@InjectView(id = R.id.test_point_z)
		TextView mPointZ;

		@InjectView(id = R.id.test_point_time)
		TextView mPointTime;

		@InjectView(id = R.id.bnt_test)
		Button mPointTestBnt;

		@InjectView(id = R.id.bnt_reset)
		Button mPointResetBnt;

		@InjectView(id = R.id.test_warring_layout)
		LinearLayout warringLayout;

		// view
		View mItemView;

		// 记录单guid
		String sheetGuid;
		// 断面guid
		String sectionGuid;
		// 测点类型
		String type;
	}

	private void checkLeijiWarnningBiggerThanMax(){
		String coordinate = "";
		Date surveyTime = null;
		if(showInfo.tunnelCur != null){
			//更新回原来的数据
			if(showInfo.tunnelOld != null){
				TunnelSettlementTotalDataDao.defaultDao().update(showInfo.tunnelOld);
				coordinate = showInfo.tunnelOld.getCoordinate();
				surveyTime = showInfo.tunnelOld.getSurveyTime();
			} 
			//删除新测量的数据
			else{
				TunnelSettlementTotalDataDao.defaultDao().delete(showInfo.tunnelCur);
				coordinate = showInfo.tunnelCur.getCoordinate();
				surveyTime = showInfo.tunnelCur.getSurveyTime();
			}
		} else if (showInfo.subCur != null){
			//更新回原来的数据
			if(showInfo.subOld != null){
				SubsidenceTotalDataDao.defaultDao().update(showInfo.subOld);
				coordinate = showInfo.subOld.getCoordinate();
				surveyTime = showInfo.subOld.getSurveyTime();
			} 
			//删除新测量的数据
			else{
				SubsidenceTotalDataDao.defaultDao().delete(showInfo.subCur);
				coordinate = showInfo.subCur.getCoordinate();
				surveyTime = showInfo.subCur.getSurveyTime();
			}
		}
		refreshCoordinateData(coordinate,surveyTime,showInfo.holder);
		showText("累计超限大于3000毫米不能保存，请重新测量");
	}
	
	private void refreshCoordinateData(String coordinate,Date surveyTime,TestPointHolder holder){
		// 存在的测量数据
		String coord = coordinate != null ? coordinate : "";

		// 默认值
		String[] str = coord.split(",");
		if (str.length == 3) {
			holder.mPointX.setText(str[0]);
			holder.mPointY.setText(str[1]);
			holder.mPointZ.setText(str[2]);
		}

		// 测试时间
		holder.mPointTime.setText(surveyTime != null ? DateUtils.toDateString(surveyTime,DateUtils.DATE_TIME_FORMAT) : "");
	}
	
	final class TestInfo {

		TestPointHolder holder1;
		TestPointHolder holder;
		String type;
		int sectionType;

		String x;
		String y;
		String z;
		String time;
	}

    class ShowInfo{
		public TestPointHolder holder;
		public TunnelSettlementTotalData tunnelOld;
		public TunnelSettlementTotalData tunnelCur;
		public SubsidenceTotalData subOld;
		public SubsidenceTotalData subCur;
    }
}