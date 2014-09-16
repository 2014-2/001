package com.crtb.tunnelmonitor.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CrtbExcavationView extends View {
	
	public static enum DRAW_TYPE {
		/** 双侧壁法*/DRAW_TYPE_PAIR, /** CD/CRD法 */ DRAW_TYPE_CD
	}
	
	public static final int 		FLAG_A1	= 1 << 0;
	public static final int 		FLAG_A2	= 1 << 1;
	public static final int 		FLAG_A3	= 1 << 2;
	
	private Context					mContext ;
	
	private Paint mPaint 			= new Paint() ;
	private int PADDING_TOP 		= 120 ;
	private int PW 					= 2 ;
	private int StrokeWidth			= 10 ;
	private int PointRadius			= 16 ;
	
	private int TEXT_SIZE_BIG		= 20 ;
	private int TEXT_SIZE_SMALL 	= 14 ;
	
	private final int Color_P		= Color.parseColor("#fab40a");
	
	// 点对数(双侧壁法)
	private int mPair 				= 4 ;
	private List<Point> points 		= new ArrayList<Point>();
	private List<Line> lines 		= new ArrayList<Line>();
	
	private final String[] GD_STR	= {"A1","A2","A3"} ;
	private int mFlag				= 0 ;
	private DRAW_TYPE mDrawType		= DRAW_TYPE.DRAW_TYPE_CD ;
	
	public CrtbExcavationView(Context context) {
		this(context, null);
	}

	public CrtbExcavationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mContext 		= context ;
		
		mPaint.setAntiAlias(true);
		
		PADDING_TOP		= dp2px(10);
		PW				= dp2px(1);
		StrokeWidth		= dp2px(2);
		PointRadius		= dp2px(4);
		
		TEXT_SIZE_BIG	= dp2px(14);
		TEXT_SIZE_SMALL	= dp2px(8);
		
		mPair			= 16 ;
		mFlag			= FLAG_A1 | FLAG_A2 | FLAG_A3 ;
		lines.clear() ;
	}
	
	private int dp2px(float value) {
		final float scale = mContext.getResources().getDisplayMetrics().densityDpi;
		return (int) (value * (scale / 160) + 0.5f);
	}
	
	public void setDrawType(DRAW_TYPE type){
		mDrawType	= type ;
	}
	
	public void setPointNumber(int number){
		
		if(number < 0 || number > 16 || number % 2 != 0){
			return ;
		}
		
		mPair	= number ;
		
		invalidate() ;
	}
	
	public void removeAllLine(){
		lines.clear() ;
		invalidate() ;
	}
	
	public void addLine(String start, String end){
		
		if(start == null || end == null || start.equals(end)) return ;
		
		Line l = new Line() ;
		l.start = start ;
		l.end  = end ;
		
		if(!lines.contains(l)){
			lines.add(l) ;
		}
		
		invalidate() ;
	}
	
	public void deleteLine(String start, String end){
		
		Line l = findLine(start,end);
		
		if(l != null) lines.remove(l);
		
		invalidate() ;
	}
	
	public Line findLine(String start, String end){
		
		if(start == null || end == null || start.equals(end)) return null;
		
		for(Line l : lines){
			
			if(l.start.equals(start) 
					&& l.end.equals(end)){
				return l ;
			}
		}
		
		return null ;
	}
	
	private Point findPoint(String name){
		
		for(Point p : points){
			if(p.name.equals(name)){
				return p ;
			}
		}
		
		return null ;
	}
	
	public void addFlag(int flag){
		mFlag |= flag ;
	}
	
	public void clearFlag(int flag){
		mFlag &=~flag ;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		// default color
		final int defColor = mPaint.getColor() ;
		
		// draw bg
		//canvas.drawColor(Color.parseColor("#c1ffcf"));
		canvas.drawColor(Color.parseColor("#ffffff"));
		
		if(mDrawType == DRAW_TYPE.DRAW_TYPE_PAIR){
			drawPair(canvas);
		} else if(mDrawType == DRAW_TYPE.DRAW_TYPE_CD){
			drawCRD(canvas);
		}
		
		// 绘制线
		for(Line l : lines){
			mPaint.setStyle(Style.FILL);
			mPaint.setStrokeWidth(StrokeWidth);
			mPaint.setColor(Color.parseColor("#e3d546"));
			
			Point start = findPoint(l.start);
			Point end 	= findPoint(l.end);
			
			if(start != null && end != null){
				canvas.drawLine(start.x,start.y,end.x,end.y,mPaint);
			}
		}
		
		// 大坐标线
		mPaint.setStrokeWidth(PW);
		mPaint.setColor(Color.BLACK);
		//canvas.drawLine(w >> 1, 0, w >> 1, h, mPaint);
		//canvas.drawLine(0, h >> 1, w, h >> 1, mPaint);
		
		// reset color
		mPaint.setColor(defColor);
	}
	
	private void drawCRD(Canvas canvas) {

		final int w = getWidth();
		final int h = getHeight();
		
		canvas.save() ;
		
		int irw = h - PADDING_TOP ; // 矩形宽度
		int irh = irw ;				// 矩形高度
		int irr = irw >> 1 ;		// 圆半径
		int ix = (w - irw) >> 1 ;	// 矩形开始位置
		int offy = h - irh * 3 / 4 ;// * 3 / 4;//(h - irh) >> 1 ;
		int loffx = 0 ;
		RectF iaf = new RectF(ix, offy, ix + irw, offy + irh);
		Point p = null ;
		points.clear() ;
		
		// 1. 圆环
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(StrokeWidth);
		mPaint.setColor(Color.BLACK);
		int startA = 160 ;
		canvas.drawArc(iaf, startA, 180 + ((180 - startA) * 2), !true, mPaint);
		
		// 线
		mPaint.setStyle(Style.FILL);
		mPaint.setStrokeWidth(StrokeWidth);
		mPaint.setColor(Color.BLACK);
		int xo = (int)(irr * Math.sin(radian(90 - (180 - startA))));
		int yo = (int)(irr * Math.cos(radian(90 - (180 - startA))));
		int yy = (int)(iaf.top + irr + yo - 1) ;
		canvas.drawLine(iaf.left + (irr - xo) - 1 , yy, iaf.right - (irr - xo) + 1 , iaf.top + irr + yo - 1, mPaint);
	
		// left 
		canvas.save() ;
		int lx 	= (int)(iaf.centerX() - (irr * Math.cos(radian(60)))); // 中心坐标之间的长度
		int ly 	= (int)(iaf.centerY() - (irr * Math.sin(radian(60)))); // 中心坐标之间的长度
		Rect lc = new Rect(lx - 1 + loffx, ly - 1, lx + irr + loffx, yy);
		canvas.clipRect(lc);
		
		RectF laf = new RectF(iaf.left - irr + loffx, iaf.top, iaf.right - irr + loffx, iaf.bottom);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(StrokeWidth * 2);
		mPaint.setColor(Color.BLACK);
		canvas.drawArc(laf, 220, 180, !true, mPaint);
		canvas.restore() ;
		
		// top
		canvas.save() ;
		int tx 	= (int)(irr * Math.cos(radian(30))); // 中心坐标之间的长度
		int ty 	= (int)(irr * Math.sin(radian(30))); // 中心坐标之间的长度
		Rect tc = new Rect((int)(iaf.centerX() - tx) - 2, (int)(iaf.centerY() - ty), (int)(iaf.centerX() + tx) + 2, yy);
		canvas.clipRect(tc);
		RectF taf = new RectF(iaf.left, iaf.top - irr , iaf.right, iaf.bottom - irr);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(StrokeWidth);
		mPaint.setColor(Color.BLACK);
		canvas.drawArc(taf, 0, 180, !true, mPaint);
		canvas.restore() ;
		
		int[] info = null ;
		// A1顶点(三角形)
		int a = (int) (irr * Math.sin(radian(56)));
		int b = (int) (irr * Math.cos(radian(56)));
		if ((mFlag & FLAG_A2) == FLAG_A2) {
			int a1x = (int) (iaf.left + (irr - b));
			int a1y = (int) (iaf.top + (irr - a));
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(Color_P);
			p = new Point();
			p.name = GD_STR[1];
			p.x = a1x;
			p.y = a1y;
			points.add(p);
			canvas.drawCircle(a1x, a1y, PointRadius, mPaint);
			mPaint.setColor(Color.BLACK);
			mPaint.setTextSize(TEXT_SIZE_BIG);
			info = getTextWidth(GD_STR[1], TEXT_SIZE_BIG);
			canvas.drawText(GD_STR[1], a1x - (info[0] << 1) - 20, a1y - (info[1] >> 1), mPaint);
		}
		
		// A2
		if((mFlag & FLAG_A1) == FLAG_A1){
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(Color_P);
			p		= new Point() ;
			p.name  = GD_STR[0] ;
			p.x		= iaf.centerX() ;
			p.y		= iaf.top ;
			points.add(p);
			canvas.drawCircle(iaf.centerX(), iaf.top, PointRadius, mPaint);
			mPaint.setColor(Color.BLACK);
			mPaint.setTextSize(TEXT_SIZE_BIG);
			info = getTextWidth(GD_STR[0], TEXT_SIZE_BIG);
			canvas.drawText(GD_STR[0], iaf.centerX() - (info[0] >> 1), iaf.top - (info[1] >> 1), mPaint);
		}
		
		// A3顶点
		if ((mFlag & FLAG_A3) == FLAG_A3) {
			int a2x = (int) (iaf.right - (irr - b));
			int a2y = (int) (iaf.top + (irr - a));
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(Color_P);
			p = new Point();
			p.name = GD_STR[2];
			p.x = a2x;
			p.y = a2y;
			points.add(p);
			canvas.drawCircle(a2x, a2y, PointRadius, mPaint);
			mPaint.setColor(Color.BLACK);
			mPaint.setTextSize(TEXT_SIZE_BIG);
			info = getTextWidth(GD_STR[2], TEXT_SIZE_BIG);
			canvas.drawText(GD_STR[2], a2x + info[0], a2y - (info[1] >> 1), mPaint);
		}	
		
		// 点对
		double ag 	= (double)(60 + (180 - startA)) / ((mPair / 2) + 1) ;
		double sag 	= 60 - ag ;
		int index = 0 ;
		int offsexX =  dp2px(3) ;
		
		// 从右到左
		// left
		for (int i = 0; i < mPair / 2; i++) {

			// 内圆left
			int nlx = (int) (iaf.centerX() - (irr * Math.cos(radian(sag))));
			int nly = (int) (iaf.centerY() - (irr * Math.sin(radian(sag))));
			p = new Point();
			p.name = String.valueOf(index + 1);
			p.x = nlx;
			p.y = nly;
			points.add(p);
			drawLinePoint(canvas, nlx, nly);
			drawPointText(canvas, String.valueOf(++index), nlx, nly, 0,0);

			// 外left
			int elx = (int) (laf.centerX() + (irr * Math.cos(radian(sag))));
			int ely = (int) (laf.centerY() - (irr * Math.sin(radian(sag))));
			p = new Point();
			p.name = String.valueOf(index + 1);
			p.x = elx;
			p.y = ely;
			points.add(p);
			drawLinePoint(canvas, elx - offsexX, ely);
			drawPointText(canvas, String.valueOf(++index), elx - offsexX, ely, dp2px(3),0);

			// 角度增加
			sag -= ag;
		}
		
		sag 	= 60 - ag ;
		// right
		for(int i = 0 ; i < mPair / 2 ; i++){
			
			// 外right
			int elx = (int) (laf.centerX() + (irr * Math.cos(radian(sag))));
			int ely = (int) (laf.centerY() - (irr * Math.sin(radian(sag))));
			p = new Point();
			p.name = String.valueOf(index + 1);
			p.x = elx;
			p.y = ely;
			points.add(p);
			drawLinePoint(canvas, elx + offsexX, ely);
			drawPointText(canvas, String.valueOf(++index), elx + offsexX, ely, dp2px(3),1);
			
			// 内圆right
			int nrx	= (int)(iaf.centerX() + (irr * Math.cos(radian(sag)))); 
			int nry	= (int)(iaf.centerY() - (irr * Math.sin(radian(sag)))); 
			p = new Point() ;
			p.name	= String.valueOf(index + 1) ;
			p.x		= nrx ;
			p.y		= nry ;
			points.add(p);
			drawLinePoint(canvas,nrx, nry);
			drawPointText(canvas, String.valueOf(++index), nrx, nry, 0,1);
			
			// 角度增加
			sag -= ag ;
		}
		
		canvas.restore() ;
	}
	
	private void drawPair(Canvas canvas){
		
		final int w = getWidth() ;
		final int h = getHeight() ;
		
		canvas.save() ;
		
		int irw = h - PADDING_TOP ; // 矩形宽度
		int irh = irw ;				// 矩形高度
		int irr = irw >> 1 ;		// 圆半径
		int ix = (w - irw) >> 1 ;	// 矩形开始位置
		int offy = h - irh * 3 / 4 ;
		RectF iaf = new RectF(ix, offy, ix + irw, offy + irh);
		Point p = null ;
		points.clear() ;
		
		// 1. 圆环
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(StrokeWidth);
		mPaint.setColor(Color.BLACK);
		int startA = 160 ;
		canvas.drawArc(iaf, startA, 180 + ((180 - startA) * 2), !true, mPaint);
		
		// 线
		mPaint.setStyle(Style.FILL);
		mPaint.setStrokeWidth(StrokeWidth);
		mPaint.setColor(Color.BLACK);
		int xo = (int)(irr * Math.sin(radian(90 - (180 - startA))));
		int yo = (int)(irr * Math.cos(radian(90 - (180 - startA))));
		canvas.drawLine(iaf.left + (irr - xo) - 1 , iaf.top + irr + yo - 1, iaf.right - (irr - xo) + 1 , iaf.top + irr + yo - 1, mPaint);
		
		// left 圆
		int ls 	= (int)(irr / Math.sin(radian(45))); // 中心坐标之间的长度
		int lcx = (int)(iaf.centerX() - ls) ;
		int lcy = (int)iaf.top ;
		RectF leftR = new RectF(lcx - irr, lcy, lcx + irr, lcy + irh);
		int lpix = (int)(iaf.centerX() - (irr * Math.cos(radian(45)))); 
		int lpiy= (int)(iaf.centerY() - (irr * Math.sin(radian(45)))); 
		
		// right 圆
		int rcx = (int)(iaf.centerX() + ls) ;
		RectF rightR = new RectF(rcx - irr, lcy, rcx + irr, lcy + irh);
		int rpix = (int)(iaf.centerX() + (irr * Math.cos(radian(45)))); 
		Rect cr = new Rect(lpix, lpiy, rpix, (int)(iaf.centerY() + yo));
		
		// 绘制
		canvas.save() ;
		canvas.clipRect(cr);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(StrokeWidth);
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeJoin(Join.ROUND);
		canvas.drawArc(leftR, 315, 90, false, mPaint);
		canvas.drawArc(rightR, 135, 90, false, mPaint);
		canvas.restore() ;
		
		// A1顶点
		int[] info = null ;
		if((mFlag & FLAG_A1) == FLAG_A1){
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(Color_P);
			p		= new Point() ;
			p.name  = GD_STR[0] ;
			p.x		= iaf.centerX() ;
			p.y		= iaf.top ;
			points.add(p);
			canvas.drawCircle(iaf.centerX(), iaf.top, PointRadius, mPaint);
			mPaint.setColor(Color.BLACK);
			mPaint.setTextSize(TEXT_SIZE_BIG);
			info = getTextWidth(GD_STR[0], TEXT_SIZE_BIG);
			canvas.drawText(GD_STR[0], iaf.centerX() - (info[0] >> 1), iaf.top - (info[1] >> 1), mPaint);
		}

		// A2顶点(三角形)
		int a = (int) (irr * Math.sin(radian(45)));
		int b = (int) (irr * Math.cos(radian(45)));
		if((mFlag & FLAG_A2) == FLAG_A2){
			int a1x = (int) (iaf.left + (irr - b));
			int a1y = (int) (iaf.top + (irr - a));
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(Color_P);
			p		= new Point() ;
			p.name  = GD_STR[1] ;
			p.x		= a1x ;
			p.y		= a1y ;
			points.add(p);
			canvas.drawCircle(a1x, a1y, PointRadius, mPaint);
			mPaint.setColor(Color.BLACK);
			mPaint.setTextSize(TEXT_SIZE_BIG);
			info = getTextWidth(GD_STR[1], TEXT_SIZE_BIG);
			canvas.drawText(GD_STR[1], a1x - (info[0] << 1) - 20, a1y - (info[1] >> 1), mPaint);
		}
		
		// A3顶点
		if((mFlag & FLAG_A3) == FLAG_A3){
			int a2x = (int) (iaf.right - (irr - b));
			int a2y = (int) (iaf.top + (irr - a));
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(Color_P);
			p		= new Point() ;
			p.name  = GD_STR[2] ;
			p.x		= a2x ;
			p.y		= a2y ;
			points.add(p);
			canvas.drawCircle(a2x, a2y, PointRadius, mPaint);
			mPaint.setColor(Color.BLACK);
			mPaint.setTextSize(TEXT_SIZE_BIG);
			info = getTextWidth(GD_STR[2], TEXT_SIZE_BIG);
			canvas.drawText(GD_STR[2], a2x + info[0], a2y - (info[1] >> 1), mPaint);
		}
		
		// 点对
		double ag 	= (double)45 / ((mPair / 2) + 1) ;
		double sag 	= 45 - ag ;
		int index 	= 0 ;
		
		// 从右到左
		// left
		for(int i = 0 ; i < mPair / 2 ; i++){
			
			// 内圆left
			int nlx = (int) (iaf.centerX() - (irr * Math.cos(radian(sag))));
			int nly = (int) (iaf.centerY() - (irr * Math.sin(radian(sag))));
			p = new Point() ;
			p.name	= String.valueOf(index + 1) ;
			p.x		= nlx ;
			p.y		= nly ;
			points.add(p);
			drawLinePoint(canvas, nlx, nly);
			drawPointText(canvas, String.valueOf(++index), nlx, nly, 0,0);
			
			// 外left
			int elx = (int) (leftR.centerX() + (irr * Math.cos(radian(sag))));
			int ely = (int) (leftR.centerY() - (irr * Math.sin(radian(sag))));
			p = new Point() ;
			p.name	= String.valueOf(index + 1) ;
			p.x		= elx ;
			p.y		= ely ;
			points.add(p);
			drawLinePoint(canvas, elx, ely);
			drawPointText(canvas,String.valueOf(++index),elx,ely,0,1);
			
			// 角度增加
			sag -= ag ;
		}
		
		sag 	= 45 - ag ;
		// right
		for(int i = 0 ; i < mPair / 2 ; i++){
			
			// 外right
			int erx = (int) (rightR.centerX() - (irr * Math.cos(radian(sag))));
			int ery = (int) (rightR.centerY() - (irr * Math.sin(radian(sag))));
			p = new Point() ;
			p.name	= String.valueOf(index + 1) ;
			p.x		= erx ;
			p.y		= ery ;
			points.add(p);
			drawLinePoint(canvas, erx, ery);
			drawPointText(canvas, String.valueOf(++index), erx, ery, 0,0);
			
			// 内圆right
			int nrx	= (int)(iaf.centerX() + (irr * Math.cos(radian(sag)))); 
			int nry	= (int)(iaf.centerY() - (irr * Math.sin(radian(sag)))); 
			p = new Point() ;
			p.name	= String.valueOf(index + 1) ;
			p.x		= nrx ;
			p.y		= nry ;
			points.add(p);
			drawLinePoint(canvas,nrx, nry);
			drawPointText(canvas, String.valueOf(++index), nrx, nry, 0,1);
			
			// 角度增加
			sag -= ag ;
		}
		
		canvas.restore() ;
	}
	
	private void drawLinePoint(Canvas canvas ,int x, int y){
		mPaint.setStyle(Style.FILL);
		mPaint.setColor(Color_P);
		canvas.drawCircle(x, y, dp2px(3), mPaint);
	}
	
	private void drawPointText(Canvas canvas ,String text,int x, int y, int top,int dir){
		
		mPaint.setColor(Color.BLACK);
		mPaint.setTextSize(TEXT_SIZE_SMALL);
		int[] info = getTextWidth(text, TEXT_SIZE_SMALL);
		
		int dx = 0 ;
		int dy = y ;
		
		// left
		if(dir == 0){
			dx	= x - info[0] - (text.length() > 1 ? dp2px(10) : dp2px(6)) ;
		} else {
			dx	= x + info[0] ;
		}
		
		canvas.drawText(text, dx, dy + top, mPaint);
	}
	
	// 字体宽度大小
	private int[] getTextWidth(String str, float size){
		
		int[] info = new int[2];
		
		Paint p = new Paint() ;
		p.setTextSize(size);
		
		Rect rect= new Rect();
		p.getTextBounds(str,0,1, rect);
		
		info[0] = rect.width() ;
		info[1] = rect.height() ;
		
		return info ;
	}
	
	private final double radian(double angle){
		return angle * Math.PI / 180 ;
	}
	
	private class Point {
		
		public String name ;
		public float x ;
		public float y ;
	}
	
	private class Line {
		
		String start = "";
		String end  = "" ;
		
		@Override
		public boolean equals(Object o) {
			
			Line s = (Line)o ;
			
			return (start.equals(s.start) && end.equals(s.end)) 
					|| (start.equals(s.end) && end.equals(s.start));
		}
	}
}
