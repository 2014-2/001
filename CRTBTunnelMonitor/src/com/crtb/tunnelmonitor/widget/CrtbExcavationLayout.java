package com.crtb.tunnelmonitor.widget;

import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;
import org.zw.android.framework.util.StringUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.BaseActivity;
import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.ExcavateMethodDao;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionParameter;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogList;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogList.OnMenuItemClick;
import com.crtb.tunnelmonitor.utils.ExcavateMethodUtil;
import com.crtb.tunnelmonitor.widget.CrtbExcavationView.DRAW_TYPE;

@InjectLayout(layout=R.layout.custom_excavation_layout)
public class CrtbExcavationLayout extends LinearLayout implements OnClickListener{
	
	@InjectView(id=R.id.work_btn_queding,onClick="this")
	private Button bntConfirm;
	
	@InjectView(id=R.id.work_btn_quxiao,onClick="this")
	private Button bntCancel;
	
	@InjectView(id=R.id.section_exca_template)
	private Spinner excaTemplate ;
	
	@InjectView(id=R.id.section_exca_name)
	private EditText excaName ;
	
	@InjectView(id=R.id.img_fangfa)
	private CrtbExcavationView excaView ; // 图形
	
	@InjectView(id=R.id.section_exca_gd_count)
	private Spinner excaGdPoint ;
	
	@InjectView(id=R.id.section_exca_line_pcount)
	private Spinner excaLinePoints ;
	
	@InjectView(id=R.id.bnt_increase_line,onClick="this")
	private Button bntIncreaseLine ;
	
	@InjectView(id=R.id.bnt_delete_line,onClick="this")
	private Button bntDeleteLine ;
	
	@InjectView(id=R.id.line_list_view)
	private LinearLayout mLineContainer;
	
	@InjectView(id=R.id.img_fangfa)
	private CrtbExcavationView customView ;
	
	private int customType ;
	private List<TestLine> beans = new ArrayList<TestLine>();
	private List<View> testViews = new ArrayList<View>();
	private int gdPoints ;
	private int linePoints ;
	
	private ExcavateMethodDao dao ;
	private LayoutInflater mInflater ;
	private BaseActivity mActivity ;

	public CrtbExcavationLayout(Context context) {
		this(context, null);
	}
	
	public CrtbExcavationLayout(final Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setOrientation(VERTICAL) ;
		
		mInflater	= LayoutInflater.from(context);
		mActivity	= (BaseActivity) context ;
		
		InjectCore.injectUIProperty(this);
		
		customType	= 7 ; // CD法为5，CRD法为6，双侧壁导坑法为7。
		gdPoints	= 1 ;
		linePoints	= 2 ; // 2 对
		testViews.clear() ;
		beans.clear() ;
		dao	= ExcavateMethodDao.defaultDao() ;
		
		// 默认
		customView.setDrawType(DRAW_TYPE.DRAW_TYPE_PAIR);
		customView.removeAllLine() ;
		customView.setPointNumber(2);
		
		// 模板
		ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(context,
				R.array.exca_template,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		excaTemplate.setAdapter(adapter);
		
		excaTemplate.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				// 测线对数
				int lresid = R.array.exca_lines_counts ;
				customView.removeAllLine() ;
				
				// CD法
				if(position == 0){
					customType	= 5 ;
					lresid = R.array.exca_lines_counts_cd_or_cdr ;
					customView.setDrawType(DRAW_TYPE.DRAW_TYPE_CD);
					customView.removeAllLine() ;
					customView.setPointNumber(4);
				} 
				//CRD
				else if(position == 1){
					customType	= 6 ;
					lresid = R.array.exca_lines_counts_cd_or_cdr ;
					customView.setDrawType(DRAW_TYPE.DRAW_TYPE_CD);
					customView.removeAllLine() ;
					customView.setPointNumber(4);
				}
				// 双侧壁法
				else if(position == 2){
					customType	= 7 ;
					lresid = R.array.exca_lines_counts ;
					customView.setDrawType(DRAW_TYPE.DRAW_TYPE_PAIR);
					customView.removeAllLine() ;
					customView.setPointNumber(2);
				} 

				// 测线点对数
				ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(context, lresid,
						android.R.layout.simple_spinner_item);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				excaLinePoints.setAdapter(adapter);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		// 拱顶个数
		adapter = ArrayAdapter.createFromResource(context,
				R.array.exca_gd_counts, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		excaGdPoint.setAdapter(adapter);
		
		// 拱顶选择
		excaGdPoint.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				// 1,2,3 个
				gdPoints	= position + 1 ;
				
				if(gdPoints == 1){
					customView.addFlag(CrtbExcavationView.FLAG_A1);
					customView.clearFlag(CrtbExcavationView.FLAG_A2);
					customView.clearFlag(CrtbExcavationView.FLAG_A3);
				} else if(gdPoints == 2){
					customView.addFlag(CrtbExcavationView.FLAG_A1);
					customView.addFlag(CrtbExcavationView.FLAG_A2);
					customView.clearFlag(CrtbExcavationView.FLAG_A3);
				} else if(gdPoints == 3){
					customView.addFlag(CrtbExcavationView.FLAG_A1);
					customView.addFlag(CrtbExcavationView.FLAG_A2);
					customView.addFlag(CrtbExcavationView.FLAG_A3);
				}
				
				// 重置测线
				resetLines();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		// 测线对数(默认为双侧壁法)
		adapter = ArrayAdapter.createFromResource(context, R.array.exca_lines_counts,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		excaLinePoints.setAdapter(adapter);
		
		excaLinePoints.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				// 2.4.6.8
				//int ps = (position + 1) * 2 ;
				
				//YX 获取测线的点对数
				try {
					linePoints = Integer.valueOf(((TextView)parent.getChildAt(0)).getText().toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// 重置测线
				resetLines();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}

	private void resetLines(){
		
		// 清空点对数
		customView.removeAllLine() ;
		
		// 设置测点
		customView.setPointNumber(linePoints);
		
		beans.clear() ;
		
		TestLine bean 		= new TestLine() ;
		bean.lineName 		= "S1" ;
		bean.lineStartPoint = "1" ;
		bean.lineEndPoint	= "" ;
		
		beans.add(bean);
		
		createLineViews() ;
	}
	
	private void showText(String msg){
		Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show() ;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.work_btn_quxiao :
			mActivity.finish() ;
			break ;
		case R.id.work_btn_queding :
			
			String name = excaName.getEditableText().toString().trim() ;
			
			if(StringUtils.isEmpty(name)){
				showText("开挖方法名不能为空");
				return ;
			}
			
			if(!ExcavateMethodUtil.checkExcavateMethodName(name)){
				showText("已经存在同名的开挖方法");
				return ;
			}
			
			if(testViews.isEmpty()){
				showText("请添加测线");
				return ;
			}
			
			for(TestLine line : beans ){
				
				if(StringUtils.isEmpty(line.lineStartPoint)
						|| StringUtils.isEmpty(line.lineEndPoint)){
					showText("测线点不能为空");
					return ;
				}
			}
			
			TunnelCrossSectionParameter obj = new TunnelCrossSectionParameter() ;
			obj.setExcavateMethod(dao.getExcavateMethodValue());
			obj.setMethodName(name);
			obj.setCrownPointNumber(gdPoints); // 拱顶
			obj.setSurveyLinePointNumber(linePoints); // 测线点对数
			obj.setSurveyLineNumber(testViews.size()); // 测线条数
			
			// 测线格式
			StringBuilder str = new StringBuilder() ;
			for(int index = 0 ; index < testViews.size() ; index++){
				
				View item 	= testViews.get(index);
				TestLine o 	= (TestLine)item.getTag() ;
				
				if(index > 0){
					str.append("/");
				}
				
				str.append("S");
				str.append(index + 1);
				str.append(",");
				
				str.append(o.lineStartPoint);
				str.append(",");
				str.append(o.lineEndPoint);
			}
			
			obj.setSurveyLinePointName(str.toString()); // 测线格式
			obj.setType(customType);
			
			if(dao.insert(obj) > 0){
				showText("保存成功");
				mActivity.finish() ;
			} else {
				showText("保存失败");
			}
			
			break ;
		case R.id.bnt_increase_line :
			
			if(beans.size() >= Constant.TEST_LINE_MAX){
				showText("最多只能有"+Constant.TEST_LINE_MAX+"条测线");
				return;
			}
			TestLine bean 		= new TestLine() ;
			bean.lineName 		= "" ;
			bean.lineStartPoint = findPoint() ;
			bean.lineEndPoint 	= "" ;
			beans.add(bean);
			
			createLineViews();
			
			break ;
		case R.id.bnt_delete_line :
			removeLine();
			break ;
		}
	}
	
	private void createLineViews(){
		
		mLineContainer.removeAllViews() ;
		testViews.clear() ;
		
		for(int index = 0 ; index < beans.size() ; index++){
			
			TestLine bean = beans.get(index);
			
			bean.lineName 		= "S" + (index + 1) ;
			
			if(bean.lineStartPoint == null){
				bean.lineStartPoint = "1" ;
			}
			
			if(bean.lineEndPoint == null){
				bean.lineEndPoint 	= "2" ;
			}
			
			View v = createTestLineView(bean);
			v.setTag(bean);
			testViews.add(v);
			
			if(index > 0){
				View l = v.findViewById(R.id.line);
				l.setVisibility(View.VISIBLE);
			}
			
			mLineContainer.addView(v);
		}
	}
	
	private void updateTestLineView(){
		
		customView.removeAllLine() ;
		
		for(int index = 0 ; index < testViews.size() ; index++){
			
			View v 			= testViews.get(index) ;
			TestLine bean 	= (TestLine)v.getTag() ;
			
			TextView name 	= (TextView) v.findViewById(R.id.item_line_name);
			Button start 	= (Button) v.findViewById(R.id.item_line_start);
			Button end 		= (Button) v.findViewById(R.id.item_line_end);
			
			bean.lineName	= "S" + (index+1);
			name.setText(bean.lineName);
			start.setText(bean.lineStartPoint);
			end.setText(bean.lineEndPoint);
			
			customView.addLine(bean.lineStartPoint, bean.lineEndPoint);
		}
	}
	
	private String findPoint(){
		
		String[] ps = getTestLineStartPoint(null);
		
		for(String p : ps){
			
			boolean find = false ;
			
			for(TestLine t : beans){
				if(t.lineStartPoint.equals(p)){
					find = true ;
					break ;
				}
			}
			
			if(!find) return p ;
		}
		
		return "" ;
	}
	
	public void removeLine(){
		
		if(testViews.size() == 1){
			showText("至少要包含一条测线");
			return ;
		}
		
		List<View> rv = new ArrayList<View>();
		
		for(int index = 0 ; index < testViews.size() ; index++){
			
			View v 			= testViews.get(index) ;
			TestLine bean 	= (TestLine)v.getTag() ;
			CheckBox check 	= (CheckBox) v.findViewById(R.id.item_line_check);
			
			if(check.isChecked()){
				rv.add(v);
				beans.remove(bean);
				mLineContainer.removeView(v);
			}
		}
		
		// 删除View
		testViews.removeAll(rv);
		
		// 更新line
		updateTestLineView() ;
	}
	
	private View createTestLineView(final TestLine bean){
		
		View v = mInflater.inflate(R.layout.item_custom_line_layout, null);
		
		v.setTag(bean);
		
		TextView name 	= (TextView) v.findViewById(R.id.item_line_name);
		Button start 	= (Button) v.findViewById(R.id.item_line_start);
		Button end 		= (Button) v.findViewById(R.id.item_line_end);
		
		name.setText(bean.lineName);
		start.setText(bean.lineStartPoint);
		end.setText(bean.lineEndPoint);
		
		// 起点
		start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String[] points = getTestLineStartPoint(bean);
				
				CrtbDialogList<String> sd = new CrtbDialogList<String>(getContext(), points, null);
				sd.show() ;
				
				sd.setMenuItemClick(new OnMenuItemClick<String>() {

					@Override
					public void onItemClick(String obj, int position,String item) {
						
						// 用于检测的临时线
						TestLine temp = new TestLine() ;
						temp.lineStartPoint = item ;
						temp.lineEndPoint	= bean.lineEndPoint ;
						
						if(beans.contains(temp)){
							showText("不能存在相同的测线");
							return ;
						}
						
						// 起点
						bean.lineStartPoint = item ;
						
						updateTestLineView() ;
					}
				}) ;
			}
		}) ;
		
		// 终点
		end.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String[] points = getTestLineEndPoint(bean);
				
				CrtbDialogList<String> sd = new CrtbDialogList<String>(mActivity, points, null);
				sd.show() ;
				
				sd.setMenuItemClick(new OnMenuItemClick<String>() {

					@Override
					public void onItemClick(String obj, int position,String item) {
						
						// 用于检测的临时线
						TestLine temp = new TestLine() ;
						temp.lineStartPoint = bean.lineStartPoint ;
						temp.lineEndPoint	= item ;
						
						if(beans.contains(temp)){
							showText("不能存在相同的测线");
							return ;
						}
						
						// 终点
						bean.lineEndPoint = item ;
						
						updateTestLineView() ;
					}
				}) ;
			}
		}) ;
		
		return v ;
	}
	
	private String[] getTestLineStartPoint(TestLine bean){
		
		List<String> pc = new ArrayList<String>();
		
		if(gdPoints == 1){
			pc.add("A1");
		} else if(gdPoints == 2){
			pc.add("A1");
			pc.add("A2");
		} else if(gdPoints == 3){
			pc.add("A1");
			pc.add("A2");
			pc.add("A3");
		}
		
		for(int i = 0 ; i < linePoints ; i++){
			
			int pos = i * 2 ;
			pc.add(String.valueOf(pos + 1));
			pc.add(String.valueOf(pos + 2));
		}
		
		// 是否存在结束点
		if(bean != null && !StringUtils.isEmpty(bean.lineEndPoint)){
			pc.remove(bean.lineEndPoint);
		}
		
		String[] points = new String[pc.size()];
		pc.toArray(points);
		
		return points ;
	}
	
	private String[] getTestLineEndPoint(TestLine bean){
		
		String[] points 	= getTestLineStartPoint(bean);
		List<String> eps 	= new ArrayList<String>();
		
		for(String str : points){
			eps.add(str);
		}
		
		// 删除起点
		if(!StringUtils.isEmpty(bean.lineStartPoint)){
			eps.remove(bean.lineStartPoint);
		}
		
		String[] array = new String[eps.size()] ;
		eps.toArray(array);
		
		return array ;
	}

	private class TestLine {
		
		String lineName ;
		String lineStartPoint ;
		String lineEndPoint ;
		
		public TestLine(){
			lineName		= "" ;
			lineStartPoint	= "" ;
			lineEndPoint	= "" ;
		}
		
		@Override
		public boolean equals(Object o) {
			
			return (lineStartPoint.equals(((TestLine)o).lineStartPoint) && lineEndPoint.equals(((TestLine)o).lineEndPoint)) || 
					(lineStartPoint.equals(((TestLine)o).lineEndPoint) && lineEndPoint.equals(((TestLine)o).lineStartPoint));
		}
	}
}
