package com.crtb.tunnelmonitor.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

/**
 * System Menu
 * 
 * @author zhouwei
 *
 */
@SuppressLint("ViewConstructor")
public final class CrtbInputDrowHint extends PopupWindow {
	
	private TextView  	mContentView ;
	private String  	mPrifex = "" ;

	public CrtbInputDrowHint(Context owner,ViewGroup root,int width,int height) {
		super(root,width, height);
		
		mContentView = (TextView)root.findViewById(R.id.hint_textview);
		
		ProjectIndex pro = ProjectIndexDao.defaultWorkPlanDao().queryEditWorkPlan() ;
		
		if(pro != null){
			mPrifex	= pro.getChainagePrefix() ;
		}
	}
	
	public void show(View token,String content) {
		
		String value = null ;
		
		try{
			value	= CrtbUtils.formatSectionName(mPrifex,Float.valueOf(content)) ;
		}catch(Exception e){
			e.printStackTrace() ;
			value = content ;
		}
		
		mContentView.setText(value);

		if (!isShowing()) {
			showAsDropDown(token,0,-5);
		}
	}
	
	public void onTouchEvent(MotionEvent event){
		
		Rect outRect = new Rect() ;
		getContentView().getHitRect(outRect);
		
		int dx = (int)event.getX() ;
		int dy = (int)event.getY() ;
		
		if(!outRect.contains(dx, dy)){
			dismiss() ;
		}
	}
}
