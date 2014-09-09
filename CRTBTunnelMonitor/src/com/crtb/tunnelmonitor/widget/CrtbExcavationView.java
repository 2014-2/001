package com.crtb.tunnelmonitor.widget;

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
	
	public static final int 		FLAG_A1	= 1 << 0;
	public static final int 		FLAG_A2	= 1 << 1;
	public static final int 		FLAG_A3	= 1 << 2;
	
	private Context					mContext ;
	
	private Paint mPaint 			= new Paint() ;
	private int PADDING_TOP 		= 50 ;
	private int PW 					= 2 ;
	private int StrokeWidth			= 10 ;
	private int PointRadius			= 16 ;
	
	private int TEXT_SIZE_BIG		= 20 ;
	private int TEXT_SIZE_SMALL 	= 14 ;
	
	private int GD_ANGLE			= 55 ; // 拱顶角度
	private int EXCA_ANGLE			= 70 ; // 拱顶角度
	
	private int EXCA_R				= 167 ;
	private int EXCA_X				= -300 ;
	private int EXCA_Y				= 150 ;
	
	private int mFlag				= 0 ;
	
	private final String[] GD_STR	= {"A1","A2","A3"} ;
	
	public CrtbExcavationView(Context context) {
		this(context, null);
	}

	public CrtbExcavationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mContext 		= context ;
		
		mPaint.setAntiAlias(true);
		
		PADDING_TOP		= dp2px(20);
		PW				= dp2px(1);
		StrokeWidth		= dp2px(2);
		PointRadius		= dp2px(6);
		
		TEXT_SIZE_BIG	= dp2px(18);
		TEXT_SIZE_SMALL	= dp2px(12);
		
		EXCA_R 			= dp2px(167);
		EXCA_X			= dp2px(100);
		EXCA_Y			= dp2px(50);
	}
	
	private int dp2px(float value) {
		final float scale = mContext.getResources().getDisplayMetrics().densityDpi;
		return (int) (value * (scale / 160) + 0.5f);
	}
	
	private int px2dp(float value) {
    	final float scale = mContext.getResources().getDisplayMetrics().densityDpi;
        return (int) ((value * 160) / scale + 0.5f);
    }
	
	public void addFlag(int flag){
		mFlag |= flag ;
	}
	
	public void clearFlag(int flag){
		mFlag &=~flag ;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		final int w = getWidth() ;
		final int h = getHeight() ;
		
		// default color
		final int defColor = mPaint.getColor() ;
		
		// draw bg
		canvas.drawColor(Color.GREEN);
		
		canvas.save() ;
		
		// 基本位置 
		int irw = h - PADDING_TOP ; // 矩形宽度
		int irh = irw ;				// 矩形高度
		int irr = irw >> 1 ;		// 圆半径
		int ix = (w - irw) >> 1 ;	// 矩形开始位置
		int offy = h - (irh * 3 / 4) - StrokeWidth / 2;
		int startA = 150 ;
		RectF iaf = new RectF(ix, offy, ix + irw, offy + irh);
		int[] info = null ;
		
		mPaint.setColor(Color.MAGENTA);
		//canvas.drawRect(iaf, mPaint);
		
		// 1. 圆环
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(StrokeWidth);
		mPaint.setColor(Color.BLACK);
		canvas.drawArc(iaf, startA, 180 + ((180 - startA) * 2), !true, mPaint);
		
		// 线
		mPaint.setStyle(Style.FILL);
		mPaint.setStrokeWidth(StrokeWidth);
		mPaint.setColor(Color.BLACK);
		int xo = (int)(irr * Math.sin(radian(90 - (180 - startA))));
		int yo = (int)(irr * Math.cos(radian(90 - (180 - startA))));
		canvas.drawLine(iaf.left + (irr - xo) - 1 , iaf.top + irr + yo - 1, iaf.right - (irr - xo) + 1 , iaf.top + irr + yo - 1, mPaint);
		
		// 2. A1顶点
		if((mFlag & FLAG_A1) == FLAG_A1){
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(Color.RED);
			canvas.drawCircle(iaf.centerX(), iaf.top, PointRadius, mPaint);
			mPaint.setColor(Color.BLACK);
			mPaint.setTextSize(TEXT_SIZE_BIG);
			info = getTextWidth(GD_STR[0], TEXT_SIZE_BIG);
			canvas.drawText(GD_STR[0], iaf.centerX() - (info[0] >> 1), iaf.top - (info[1] >> 1), mPaint);
		}
		
		// 3. A2顶点(三角形)
		int a = (int)(irr * Math.sin(radian(GD_ANGLE)));
		int b = (int)(irr * Math.cos(radian(GD_ANGLE)));
		if((mFlag & FLAG_A2) == FLAG_A2){
			int a1x = (int)(iaf.left + (irr - b)) ;
			int a1y = (int)(iaf.top  + (irr - a)) ;
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(Color.RED);
			canvas.drawCircle(a1x, a1y, PointRadius, mPaint);
			mPaint.setColor(Color.BLACK);
			mPaint.setTextSize(TEXT_SIZE_BIG);
			info = getTextWidth(GD_STR[1], TEXT_SIZE_BIG);
			canvas.drawText(GD_STR[1], a1x - (info[0] << 1) - 20, a1y - (info[1] >> 1), mPaint);

		}
		
		// 4. A3顶点
		if((mFlag & FLAG_A3) == FLAG_A3){
			int a2x = (int)(iaf.right - (irr - b)) ;
			int a2y = (int)(iaf.top  + (irr - a)) ;
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(Color.RED);
			canvas.drawCircle(a2x, a2y, PointRadius, mPaint);
			mPaint.setColor(Color.BLACK);
			mPaint.setTextSize(TEXT_SIZE_BIG);
			info = getTextWidth(GD_STR[2], TEXT_SIZE_BIG);
			canvas.drawText(GD_STR[2], a2x + info[0], a2y - (info[1] >> 1), mPaint);
		}
		
		// 开挖线
		canvas.save() ;
		Rect cr = new Rect(0, ( h >> 1 ) - PADDING_TOP, w, h);
		//canvas.clipRect(cr);
		
		// 开挖线
		int ls 	= (int)(irr / Math.sin(radian(45))); // 中心坐标之间的长度
		int lcx = (int)(iaf.centerX() - ls) ;
		int lcy = (int)iaf.top + offy ;
		
		RectF eaf = new RectF(lcx - irr, lcy, lcx + irr, lcy + irh);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(StrokeWidth);
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeJoin(Join.ROUND);
		canvas.drawArc(eaf, 275, 180, false, mPaint);
		canvas.restore() ;
		
		canvas.restore() ;
		
		// 大坐标线
		mPaint.setStrokeWidth(PW);
		mPaint.setColor(Color.BLACK);
		//canvas.drawLine(w >> 1, 0, w >> 1, h, mPaint);
		//canvas.drawLine(0, h >> 1, w, h >> 1, mPaint);
		
		// reset color
		mPaint.setColor(defColor);
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
	
	private final double radian(int angle){
		return angle * Math.PI / 180 ;
	}
}
