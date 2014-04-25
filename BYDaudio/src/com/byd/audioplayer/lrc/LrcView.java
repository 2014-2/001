package com.byd.audioplayer.lrc;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class LrcView extends TextView {
    private float width;        //濮濆矁鐦濈憴鍡楁禈鐎硅棄瀹�
    private float height;       //濮濆矁鐦濈憴鍡楁禈妤傛ê瀹�
    private Paint currentPaint; //瑜版挸澧犻悽鑽ょ應鐎电钖�
    private Paint notCurrentPaint;  //闂堢偛缍嬮崜宥囨暰缁楁柨顕挒锟�    
    private float textHeight = 50;  //閺傚洦婀版妯哄
    private float textSize = 25;        //閺傚洦婀版径褍鐨�
    private float currentTextSize = 30; //瑜版挸澧犲宀冪槤閺傚洦婀版径褍鐨�
    private int index = 0;      //list闂嗗棗鎮庢稉瀣垼


    private List<LrcContent> mLrcList = new ArrayList<LrcContent>();

    public void setLrcList(List<LrcContent> lrcList) {
        this.mLrcList = lrcList;
    }

    public LrcView(Context context) {
        super(context);
        init();
    }
    public LrcView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LrcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setFocusable(true);     //鐠佸墽鐤嗛崣顖氼嚠閻掞拷

        //妤傛ü瀵掗柈銊ュ瀻
        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);    //鐠佸墽鐤嗛幎妤呮暜姒诲尅绱濈拋鈺傛瀮鐎涙绶ㄧ憴鍌炪偙濠婏拷
        currentPaint.setTextAlign(Paint.Align.CENTER);//鐠佸墽鐤嗛弬鍥ㄦ拱鐎靛綊缍堥弬鐟扮础

        //闂堢偤鐝禍顕�劥閸掞拷
        notCurrentPaint = new Paint();
        notCurrentPaint.setAntiAlias(true);
        notCurrentPaint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * 缂佹鏁惧宀冪槤
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(canvas == null) {
            return;
        }

        currentPaint.setColor(Color.argb(255, 255, 255, 255));
        notCurrentPaint.setColor(Color.argb(140, 255, 255, 255));

        currentPaint.setTextSize(currentTextSize);
        //        currentPaint.setTypeface(Typeface.DEFAULT);

        notCurrentPaint.setTextSize(textSize);
        //        notCurrentPaint.setTypeface(Typeface.DEFAULT);

        try {
            setText("");
            float tempY = height / 2;
            float lastY = height / 2;
            float textWidth = currentPaint.measureText(mLrcList.get(index).getLrcStr());
            float windowWidth = this.width;
            if (textWidth > windowWidth) {
                // 瑜版挸澧犲宀冪槤鏉╁洭鏆遍敍宀勬付鐟曚礁鍨庨崜鍙夋▔缁�拷
                int rows = (int)Math.ceil(textWidth / windowWidth);

                String[] separateStrings = mLrcList.get(index).getLrcStr().split(" ");
                if (separateStrings.length > 1) {
                    // 婢舵牗鏋冨宀冪槤婢额亪鏆遍崚鍡楀閺勫墽銇�
                    int column = (int)Math.ceil(separateStrings.length / (double)rows);;
                    String[][] words = new String[rows][column];
                    String[] sentences = new String[rows];
                    for (int i = 0; i < separateStrings.length; i++) {
                        words[i / column][i % column] = separateStrings[i];
                    }
                    for (int x = 0; x < sentences.length; x++) {
                        sentences[x] = "";
                    }
                    for (int r = 0; r < rows; r++) {
                        for (int c = 0; c < column; c++) {
                            if (words[r][c] != null) {
                                sentences[r] += words[r][c] + " ";
                            }
                        }
                    }
                    for (String line : sentences) {
                        canvas.drawText(line, width / 2, lastY, currentPaint);
                        lastY += textHeight;
                    }
                    lastY -= textHeight;
                } else {
                    // 娑擃厽鏋冨宀冪槤婢额亪鏆遍崚鍡楀閺勫墽銇�
                    String singleString = separateStrings[0];
                    int column = (int)Math.ceil(singleString.length() / (double)rows);
                    String[][] words = new String[rows][column];
                    String[] sentences = new String[rows];
                    for (int i = 0; i < singleString.length(); i++) {
                        words[i / column][i % column] = String.valueOf(singleString.charAt(i));
                    }
                    for (int x = 0; x < sentences.length; x++) {
                        sentences[x] = "";
                    }
                    for (int r = 0; r < rows; r++) {
                        for (int c = 0; c < column; c++) {
                            if (words[r][c] != null) {
                                sentences[r] += words[r][c];
                            }
                        }
                    }
                    for (String line : sentences) {
                        canvas.drawText(line, width / 2, lastY, currentPaint);
                        lastY += textHeight;
                    }
                    lastY -= textHeight;
                }
            } else {
                canvas.drawText(mLrcList.get(index).getLrcStr(), width / 2, lastY, currentPaint);
            }

            //閻㈣鍤張顒�綖娑斿澧犻惃鍕綖鐎涳拷
            for(int i = index - 1; i >= 0; i--) {
                //閸氭垳绗傞幒銊�
                tempY = tempY - textHeight;
                canvas.drawText(mLrcList.get(i).getLrcStr(), width / 2, tempY, notCurrentPaint);
            }
            tempY = lastY;
            //閻㈣鍤張顒�綖娑斿鎮楅惃鍕綖鐎涳拷
            for(int i = index + 1; i < mLrcList.size(); i++) {
                //瀵帮拷绗呴幒銊�
                tempY = tempY + textHeight;
                canvas.drawText(mLrcList.get(i).getLrcStr(), width / 2, tempY, notCurrentPaint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 瑜版悧iew婢堆冪毈閺�懓褰夐惃鍕閸婃瑨鐨熼悽銊ф畱閺傝纭�
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
