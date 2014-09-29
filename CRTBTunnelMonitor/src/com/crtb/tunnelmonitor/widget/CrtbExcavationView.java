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
	private int TEXT_SIZE_MEDIUM	= 17 ;
	private int TEXT_SIZE_SMALL 	= 14 ;
	
	private final int Color_P		= Color.parseColor("#fab40a");
	
	// 点对数(双侧壁法)
	private int mPair 				= 4 ;
	private List<Point> points 		= new ArrayList<Point>();
	private List<Line> lines 		= new ArrayList<Line>();
	
	private final String[] GD_STR	= {"A1","A2","A3"} ;
	private int mFlag				= 0 ;
	private DRAW_TYPE mDrawType		= DRAW_TYPE.DRAW_TYPE_PAIR ;
	
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
		TEXT_SIZE_MEDIUM= dp2px(11);
		TEXT_SIZE_SMALL	= dp2px(8);
		
		mPair			= 4 ;
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
	
	public int getAllPoints(){
		
		int count = 0 ;
		
		if((mFlag & FLAG_A1) == FLAG_A1){
			count++ ;
		}
		
		if((mFlag & FLAG_A2) == FLAG_A2){
			count++ ;
		}
		
		if((mFlag & FLAG_A3) == FLAG_A3){
			count++ ;
		}
		
		return count + (mPair * 2) ;
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
		
		int offset = dp2px(5) ;
		
		// 绘制线
		for(int index = 0 ; index < lines.size() ; index++){
			
			Line l 		= lines.get(index);
			String sn 	= "S" + (index + 1) ;
			
			mPaint.setStyle(Style.FILL);
			mPaint.setStrokeWidth(dp2px(1));
			mPaint.setColor(Color.BLACK);
			//mPaint.setColor(Color.parseColor("#e3d546"));
			
			Point start = findPoint(l.start);
			Point end 	= findPoint(l.end);
			
			if(start != null && end != null){
				
				boolean findPoint = false ;
				
				// 是否存在在同一直线上的点
				for(Point p : points){
					
					if(!p.name.equals(start.name) 
							&& !p.name.equals(end.name)){
						
						if(p.x > start.x 
								&& p.x < end.x 
								&& p.y == start.y
								&& p.y == end.y){
							findPoint	= true ;
							break ;
						}
					}
				}
				
				int[] info = getTextWidth(sn, TEXT_SIZE_MEDIUM);
				
				if(findPoint){
					
					int newsx = (int)(start.x + offset);
					int newsy = (int)(start.y + offset);
					
					int newex = (int)(end.x - offset);
					int newey = (int)(end.y + offset);
					
					canvas.drawLine(start.x,start.y,newsx,newsy,mPaint);
					canvas.drawLine(newsx - 1,newsy - 1,newex + 1,newey - 1,mPaint);
					canvas.drawLine(newex,newey,end.x,end.y,mPaint);
					
					// 线名 Color.parseColor("#e3d546")
					mPaint.setColor(Color.RED);
					canvas.drawText(sn, newsx + ((newex - newsx) >> 1), newsy + (info[1] >> 1) + dp2px(2), mPaint);
					
				} else {
					
					canvas.drawLine(start.x,start.y,end.x,end.y,mPaint);
					
					float dx = 0 ;
					float dy = 0 ;
					
					// x
					if(end.x > start.x){
						dx = start.x + (((int)(end.x - start.x)) >> 1 ) ;
					} else {
						dx = end.x + (((int)(start.x - end.x)) >> 1 ) ;
					}
					
					// y
					if(end.y > start.y){
						dy = start.y + (((int)(end.y - start.y)) >> 1 ) ;
					} else {
						dy = end.y + (((int)(start.y - end.y)) >> 1 ) ;
					}
					
					// 线名
					mPaint.setColor(Color.RED);
					canvas.drawText(sn, dx - (info[0] >> 1), dy - dp2px(2), mPaint);
				}
			}
		}
		
		// 大坐标线
		mPaint.setStrokeWidth(PW);
		mPaint.setColor(Color.BLACK);
		//canvas.drawLine(getWidth() >> 1, 0, getWidth() >> 1, getHeight(), mPaint);
		//canvas.drawLine(0, getHeight() >> 1, getWidth(), getHeight() >> 1, mPaint);
		
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
		int offy = h - irh * 7 / 8  ;// * 3 / 4;//(h - irh) >> 1 ;
		RectF iaf = new RectF(ix, offy, ix + irw, offy + irh);
		Point p = null ;
		points.clear() ;
		
		// 1. 圆环
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(StrokeWidth);
		mPaint.setColor(Color.BLACK);
		int startA = 140 ;
		canvas.drawArc(iaf, startA, 180 + ((180 - startA) * 2), !true, mPaint);
		
		// 线
		mPaint.setStyle(Style.FILL);
		mPaint.setStrokeWidth(StrokeWidth);
		mPaint.setColor(Color.BLACK);
		int xo = (int)(irr * Math.sin(radian(90 - (180 - startA))));
		int yo = (int)(irr * Math.cos(radian(90 - (180 - startA))));
		int yy = (int)(iaf.top + irr + yo) ;
		canvas.drawLine(iaf.left + (irr - xo) - dp2px(1) , yy, iaf.right - (irr - xo) + dp2px(1) , yy, mPaint);
	
		// left 
		canvas.save() ;
		RectF laf = new RectF(iaf.left - 5 * irr, iaf.top - 2 * irr, iaf.right - irr, iaf.bottom + 2 * irr);
		// 上边交点坐标
		double angt = 19 ;
		int tjx = (int) (laf.centerX() + (irr * 3 * Math.cos(radian(angt))));
		int tjy = (int) (laf.centerY() - (irr * 3 * Math.sin(radian(angt))));
		// 下边交点坐标
		double angb = -12.4 ;
		int bjx = (int) (laf.centerX() + (irr * 3 * Math.cos(radian(angb))));
		int bjy = (int) (laf.centerY() - (irr * 3 * Math.sin(radian(angb))));
		Rect lc = null ;
		// 裁剪矩形
		if(tjx < bjx){
			lc = new Rect(tjx - dp2px(1), tjy, bjx + dp2px(10), bjy);
		} else {
			lc = new Rect(bjx - dp2px(1), tjy, tjx + dp2px(10), bjy);
		}
		canvas.clipRect(lc);
		
		// 绘制上线弧线
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(StrokeWidth * 2);
		mPaint.setColor(Color.BLACK);
		canvas.drawArc(laf, 0, 360, !true, mPaint);
		canvas.restore() ;
		
		// top
		canvas.save() ;
		RectF taf 	= new RectF(iaf.left - 2 * irr , iaf.top - 5 * irr - dp2px(5) , iaf.right + 2 * irr, iaf.bottom - irr - dp2px(5));
		// 左边交点
		double angl = -109 ;
		int ljx = (int) (taf.centerX() + (irr * 3 * Math.cos(radian(angl))));
		int ljy = (int) (taf.centerY() - (irr * 3 * Math.sin(radian(angl))));
		// 右边交点
		double angr = -71 ;
		int rjx = (int) (taf.centerX() + (irr * 3 * Math.cos(radian(angr))));
		int rjy = (int) (taf.centerY() - (irr * 3 * Math.sin(radian(angr))));
		// 裁剪矩形
		Rect tc = null ;
		if(ljy < rjy){
			tc = new Rect(ljx, ljy, rjx, rjy + dp2px(20)) ;
		} else {
			tc = new Rect(ljx, rjy, rjx, ljy + dp2px(20)) ;
		}
		canvas.clipRect(tc);
		
		// 绘制上线弧线
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(StrokeWidth);
		mPaint.setColor(Color.BLACK);
		canvas.drawArc(taf, 0, 180, !true, mPaint);
		canvas.restore() ;
		
		// 交点坐标
		double angc = -89.8 ;
		//int jcx = (int) (taf.centerX() + (irr * 3 * Math.cos(radian(angc))));
		int jcy = (int) (taf.centerY() - (irr * 3 * Math.sin(radian(angc))));
		
		int[] info = null ;
		// A1顶点(三角形)
		int a = (int) (irr * Math.sin(radian(68)));
		int b = (int) (irr * Math.cos(radian(68)));
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
		
		// 从左到右
		// 分位上下2部分
		int index 	= 0 ;
		int offsexX = dp2px(3) ;
		int size 	= mPair / 4 ;
		int ltl		= (int)(jcy - iaf.top) / (size + 1) ; // 上部分
		int lbl		= (int)(bjy - jcy) / (size + 1 ); // 下部分
		
		// 左上
		for (int i = 0; i < mPair / 4 ; i++) {
			
			// 上左
			int ly = (int)(iaf.top + ltl * (i + 1)) ;
			int lx = computeTemp(iaf.centerX(),iaf.centerY(),irr,ly,1,0);
			p = new Point();
			p.x = lx ;
			p.y	= ly ;
			p.name = String.valueOf(index + 1);
			points.add(p);
			drawLinePoint(canvas, lx, ly);
			drawPointText(canvas, String.valueOf(++index), (int)p.x, (int)p.y, dp2px(0),0);
			
			// 上右
			int lcy = ly ;
			int lcx = computeTemp(laf.centerX(),laf.centerY(),irr * 3,ly,1,1);
			p = new Point();
			p.x = lcx - offsexX;
			p.y	= lcy ;
			p.name = String.valueOf(index + 1);
			points.add(p);
			drawLinePoint(canvas, (int)p.x, (int)p.y);
			drawPointText(canvas, String.valueOf(++index), (int)p.x, (int)p.y, dp2px(0),0);
		}
		
		// 左下
		for (int i = 0; i < mPair / 4; i++) {

			// 下左
			int bcy = jcy + lbl * (i + 1) ;	
			int bcx = computeTemp(iaf.centerX(),iaf.centerY(),irr,bcy,1,0);
			p = new Point();
			p.x = bcx ;
			p.y	= bcy ;
			p.name = String.valueOf(index + 1);
			points.add(p);
			drawLinePoint(canvas, bcx, bcy);
			drawPointText(canvas, String.valueOf(++index), (int)p.x, (int)p.y, dp2px(0),0);
			
			// 下右
			int lcy = bcy;
			int lcx = computeTemp(laf.centerX(), laf.centerY(), irr * 3, bcy, 1,1);
			p = new Point();
			p.x = lcx - offsexX;
			p.y = lcy;
			p.name = String.valueOf(index + 1);
			points.add(p);
			drawLinePoint(canvas, (int)p.x, lcy);
			drawPointText(canvas, String.valueOf(++index), (int) p.x,(int) p.y, dp2px(0), 0);
		}
		
		// 右上
		for (int i = 0; i < mPair / 4; i++) {

			// 上右
			int lcy = (int)(iaf.top + ltl * (i + 1)) ;
			int lcx = computeTemp(laf.centerX(), laf.centerY(), irr * 3, lcy, 1,1);
			p = new Point();
			p.x = lcx + offsexX;
			p.y = lcy;
			p.name = String.valueOf(index + 1);
			points.add(p);
			drawLinePoint(canvas, (int)p.x, lcy);
			drawPointText(canvas, String.valueOf(++index), (int) p.x,(int) p.y, dp2px(0), 1);
			
			int ly = lcy ;
			int lx = computeTemp(iaf.centerX(),iaf.centerY(),irr,ly,1,1);
			p = new Point();
			p.x = lx ;
			p.y	= ly ;
			p.name = String.valueOf(index + 1);
			points.add(p);
			drawLinePoint(canvas, lx, ly);
			drawPointText(canvas, String.valueOf(++index), (int)p.x, (int)p.y, dp2px(0),1);
		}
		
		// 右下
		for (int i = 0; i < mPair / 4; i++) {
			
			int lcy = jcy + lbl * (i + 1) ;
			int lcx = computeTemp(laf.centerX(), laf.centerY(), irr * 3, lcy, 1,1);
			p = new Point();
			p.x = lcx + offsexX;
			p.y = lcy;
			p.name = String.valueOf(index + 1);
			points.add(p);
			drawLinePoint(canvas, (int)p.x, lcy);
			drawPointText(canvas, String.valueOf(++index), (int) p.x,(int) p.y, dp2px(0), 1);
			
			int bcy = lcy ;	
			int bcx = computeTemp(iaf.centerX(),iaf.centerY(),irr,bcy,1,1);
			p = new Point();
			p.x = bcx ;
			p.y	= bcy ;
			p.name = String.valueOf(index + 1);
			points.add(p);
			drawLinePoint(canvas, bcx, bcy);
			drawPointText(canvas, String.valueOf(++index), (int)p.x, (int)p.y, dp2px(0),1);
		}
		
		canvas.restore() ;
	}
	
	private int computeTemp(double cx , double cy, double r, double value, int type, int rt){
		
		double mv = 0 ;
		
		// 已知X, 求Y
		if(type == 0){
			
		} 
		// 已知Y, 求X
		else {
			
			if(rt == 0){
				mv = cx - Math.sqrt(Math.pow(r, 2) - Math.pow(cy - value, 2));
			} else {
				mv = Math.sqrt(Math.pow(r, 2) - Math.pow(cy - value, 2)) + cx;
			}
		}
		
		return (int) mv ;
	}
	
	private void drawPair(Canvas canvas){
		
		final int w = getWidth() ;
		final int h = getHeight() ;
		
		canvas.save() ;
		
		int irw = h - PADDING_TOP ; // 矩形宽度
		int irh = irw ;				// 矩形高度
		int irr = irw >> 1 ;		// 圆半径
		int ix = (w - irw) >> 1 ;	// 矩形开始位置
		int offy = h - irh * 7 / 8 ;
		RectF iaf = new RectF(ix, offy, ix + irw, offy + irh);
		Point p = null ;
		points.clear() ;
		
		// 1. 圆环
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(StrokeWidth);
		mPaint.setColor(Color.BLACK);
		int startA = 140 ;
		canvas.drawArc(iaf, startA, 180 + ((180 - startA) * 2), !true, mPaint);
		
		// 线
		mPaint.setStyle(Style.FILL);
		mPaint.setStrokeWidth(StrokeWidth);
		mPaint.setColor(Color.BLACK);
		int xo = (int)(irr * Math.sin(radian(90 - (180 - startA))));
		int yo = (int)(irr * Math.cos(radian(90 - (180 - startA))));
		int yy = (int)(iaf.top + irr + yo) ;
		canvas.drawLine(iaf.left + (irr - xo) - dp2px(1) , yy, iaf.right - (irr - xo) + dp2px(1) , yy, mPaint);
		
		// left 圆
		int of = dp2px(15);
		RectF lf = new RectF(iaf.left - irr - of, iaf.top, iaf.right - irr - of, iaf.bottom) ;
		
		// 上边交点坐标
		double angt = 53 ;
		int ljtx = (int) (lf.centerX() + (irr * Math.cos(radian(angt))));
		int ljty = (int) (lf.centerY() - (irr * Math.sin(radian(angt))));
		
		// 下边交点
		double angb = -39.5 ;
		int ljbx = (int) (lf.centerX() + (irr * Math.cos(radian(angb))));
		int ljby = (int) (lf.centerY() - (irr * Math.sin(radian(angb))));
		
		// 坐标裁剪矩形
		RectF cl = null ;
		if(ljtx < ljbx){
			cl	= new RectF(ljtx, ljty, ljbx + (irr >> 1), ljby);
		} else {
			cl	= new RectF(ljbx, ljty, ljtx + (irr >> 1), ljby);
		}
		
		// 绘制左边圆
		canvas.save() ;
		canvas.clipRect(cl);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(StrokeWidth);
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeJoin(Join.ROUND);
		canvas.drawArc(lf, 0, 360, false, mPaint);
		canvas.restore() ;
		
		// right 圆
		RectF rf = new RectF(iaf.left + irr + of, iaf.top, iaf.right + irr + of, iaf.bottom) ;
		
		// 上边交点坐标
		double angrt = -233;
		int rjtx = (int) (rf.centerX() + (irr * Math.cos(radian(angrt))));
		int rjty = (int) (rf.centerY() - (irr * Math.sin(radian(angrt))));

		// 下边交点
		double angrb = -141;
		int rjbx = (int) (rf.centerX() + (irr * Math.cos(radian(angrb))));
		int rjby = (int) (rf.centerY() - (irr * Math.sin(radian(angrb))));
		
		RectF cr = null ;
		if(rjtx < rjbx){
			cr	= new RectF(rjtx, rjty, rjbx - (irr >> 1), rjby);
		} else {
			cr	= new RectF(rjbx - (irr >> 1), rjty, rjtx, rjby);
		}
		
		// 绘制右边圆
		canvas.save();
		canvas.clipRect(cr);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(StrokeWidth);
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeJoin(Join.ROUND);
		canvas.drawArc(rf, 0, 360, false, mPaint);
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
		int a = (int) (irr * Math.sin(radian(70)));
		int b = (int) (irr * Math.cos(radian(70)));
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
		
		// 绘制点
		int index 	= 0 ;
		int offsexX = dp2px(0) ;
		int size 	= mPair / 2 ;
		int ltl		= (int)(ljby - ljty) / (size + 1) ; // 上部分
		
		// 左边
		for(int i = 0 ; i < mPair / 2 ; i++){
			
			// 左
			int ly = (int) (ljty + ltl * (i + 1));
			int lx = computeTemp(iaf.centerX(), iaf.centerY(), irr, ly, 1, 0);
			p = new Point();
			p.x = lx;
			p.y = ly;
			p.name = String.valueOf(index + 1);
			points.add(p);
			drawLinePoint(canvas, lx, ly);
			drawPointText(canvas, String.valueOf(++index), (int) p.x,(int) p.y, dp2px(0), 0);

			// 右
			int lcy = ly;
			int lcx = computeTemp(lf.centerX(), lf.centerY(), irr, ly, 1,1);
			p = new Point();
			p.x = lcx - offsexX;
			p.y = lcy;
			p.name = String.valueOf(index + 1);
			points.add(p);
			drawLinePoint(canvas, (int) p.x, (int) p.y);
			drawPointText(canvas, String.valueOf(++index), (int) p.x,(int) p.y, dp2px(0), 0);
		}
		
		// 右上
		for (int i = 0; i < mPair / 2; i++) {

			// 上右
			int lcy = (int) (ljty + ltl * (i + 1));
			int lcx = computeTemp(rf.centerX(), rf.centerY(), irr, lcy,1, 0);
			p = new Point();
			p.x = lcx + offsexX;
			p.y = lcy;
			p.name = String.valueOf(index + 1);
			points.add(p);
			drawLinePoint(canvas, (int) p.x, lcy);
			drawPointText(canvas, String.valueOf(++index), (int) p.x,(int) p.y, dp2px(0), 0);

			int ly = lcy;
			int lx = computeTemp(iaf.centerX(), iaf.centerY(), irr, ly, 1, 1);
			p = new Point();
			p.x = lx;
			p.y = ly;
			p.name = String.valueOf(index + 1);
			points.add(p);
			drawLinePoint(canvas, lx, ly);
			drawPointText(canvas, String.valueOf(++index), (int) p.x, (int) p.y, dp2px(0), 1);
		}
		
		canvas.restore() ;
	}
	
	private void drawLinePoint(Canvas canvas ,int x, int y){
		mPaint.setStyle(Style.FILL);
		mPaint.setColor(Color_P);
		canvas.drawCircle(x, y, dp2px(3), mPaint);
	}
	
	private void drawPointText(Canvas canvas ,String text,int x, int y, int top,int dir){
		
		mPaint.setColor(Color.RED);
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
