package com.byd.player.lrc;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class LrcView extends TextView {
    private float width;        //歌词视图宽度
    private float height;       //歌词视图高度
    private Paint currentPaint; //当前画笔对象
    private Paint notCurrentPaint;  //非当前画笔对象
    private float textHeight = 50;  //文本高度
    private float textSize = 25;        //文本大小
    private float currentTextSize = 30; //当前歌词文本大小
    private int index = 0;      //list集合下标


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
        setFocusable(true);     //设置可对焦

        //高亮部分
        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);    //设置抗锯齿，让文字美观饱满
        currentPaint.setTextAlign(Paint.Align.CENTER);//设置文本对齐方式

        //非高亮部分
        notCurrentPaint = new Paint();
        notCurrentPaint.setAntiAlias(true);
        notCurrentPaint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * 绘画歌词
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
                // 当前歌词过长，需要分割显示
                int rows = (int)Math.ceil(textWidth / windowWidth);

                String[] separateStrings = mLrcList.get(index).getLrcStr().split(" ");
                if (separateStrings.length > 1) {
                    // 外文歌词太长分割显示
                    int column = separateStrings.length / rows + 1;
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
                    // 中文歌词太长分割显示
                    String singleString = separateStrings[0];
                    int column = singleString.length() / rows + 1;
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

            //画出本句之前的句子
            for(int i = index - 1; i >= 0; i--) {
                //向上推移
                tempY = tempY - textHeight;
                canvas.drawText(mLrcList.get(i).getLrcStr(), width / 2, tempY, notCurrentPaint);
            }
            tempY = lastY;
            //画出本句之后的句子
            for(int i = index + 1; i < mLrcList.size(); i++) {
                //往下推移
                tempY = tempY + textHeight;
                canvas.drawText(mLrcList.get(i).getLrcStr(), width / 2, tempY, notCurrentPaint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 当view大小改变的时候调用的方法
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
