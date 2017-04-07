package com.example.administrator.android_sta_vod.ui.activity.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


import com.example.administrator.android_sta_vod.base.Global;
import com.example.administrator.android_sta_vod.bean.LyricsBean;

import java.util.ArrayList;

/**
 * 歌词自定义控件
 */
public class LyricsView extends View {

    // 歌词的颜色
    private int colorHighlight = Color.GREEN;
    private int colorDefault = Color.WHITE;

    // 歌词字体大小
    private int sizeHighlight = Global.dp2px(16);
    private int sizeDefault = Global.dp2px(14);    // 14sp

    private Paint mPaint;

    /** 一首歌的多行歌词 */
    private ArrayList<LyricsBean> lyricsData;

    /** 哪一行是高亮行 */
    private int highlightIndex = 0;

    /** 高亮行歌词实体 */
    private LyricsBean mCurrentLyrics;

    /** 歌词控件的宽 */
    private int mWidth;
    /** 歌词控件的高 */
    private int mHeight;

    /** 高亮行的Y坐标 */
    private float mHighlightY;

    /** 行高 = 歌词文本的高 + 行与行间距 */
    private float mRowHeight = 0;

    /** 当前播放时间 */
    private int currentPosition;

    public LyricsView(Context context) {
        super(context);
        init();
    }

    public LyricsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setTextSize(sizeDefault);
        mPaint.setColor(colorDefault);
        mPaint.setAntiAlias(true);  // 去锯齿

        mRowHeight = getTextHeight("中") + Global.dp2px(7);  // dp

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (lyricsData == null || lyricsData.size() < 1) {
            drawCenterText(canvas, "正在加载歌词...");
            return;
        }

        // 计算高亮行往上移动的偏移量
        int margin = calculateMargin();
        canvas.translate(0, -margin);   // 移动画布，负值会往上移动

        // 高亮行歌词实体
        mCurrentLyrics = lyricsData.get(highlightIndex);
        String lyricsText = mCurrentLyrics.text;

        // 绘制高亮行
        drawCenterText(canvas, lyricsText);

        // 绘制高亮行以上的歌词
        for (int i = highlightIndex - 1; i >= 0; i--) {
            // 要画的歌词
            String text = lyricsData.get(i).text;
            // Y = 高亮行的Y - 行高 * k (k = 1, 2, 3...)
            float y = mHighlightY - mRowHeight * (highlightIndex - i);
            drawHorizontalCenterText(canvas, text, y, false);
        }

        // 绘制高亮行以下的歌词
        for (int i = highlightIndex + 1; i < lyricsData.size() ; i ++) {
            // 要画的歌词
            String text = lyricsData.get(i).text;
            // Y = 高亮行的Y + 行高* k (k = 1, 2, 3...)
            float y = mHighlightY + mRowHeight *  (i - highlightIndex);
            drawHorizontalCenterText(canvas, text, y, false);
        }
    }

    /**
     * 计算高亮行往上移动的偏移量
     *
     * @return
     */
    private int calculateMargin() {
        // 高亮行往上移动的距离 与 高亮行已显示时间相关， 高亮行已显示时间越大，高亮行往上移动的距离 就越大；
        // 比例关系：高亮行往上移动的距离 / 行高 =  高亮行已显示时间 / 高亮行总显示时间
        if (lyricsData != null && highlightIndex != lyricsData.size() - 1) {  // 不是最后一行

            // 高亮行已显示时间 = 当前播放时间 - 高亮行开始显示时间
            long showedTime = currentPosition - lyricsData.get(highlightIndex).startTime;

            // 高亮行总显示时间 = 高亮行下一行的开始显示时间 - 高亮行开始显示时间
            long totalTime = lyricsData.get(highlightIndex + 1).startTime
                    - lyricsData.get(highlightIndex).startTime;

            // 高亮行往上移动的距离 = 高亮行已显示时间 / 高亮行总显示时间 * 行高
            int margin = (int) (1f * showedTime / totalTime * mRowHeight);
            return margin;
        }
        return 0;
    }

    /**
     * 绘制居中的文本
     *
     * @param canvas
     * @param lyricsText
     */
    private void drawCenterText(Canvas canvas, String lyricsText) {
        // y = 歌词View高度/2 + 歌词文本高度/2
        mHighlightY = mHeight / 2 + getTextHeight(lyricsText) / 2;
        drawHorizontalCenterText(canvas, lyricsText, mHighlightY, true);
    }

    /**
     * 绘制水平居中的文本
     * @param canvas
     * @param lyricsText
     * @param y
     * @param isHighlight 是否高亮行
     */
    private void drawHorizontalCenterText(Canvas canvas,
                                          String lyricsText, float y, boolean isHighlight) {
        mPaint.setColor(isHighlight ? colorHighlight : colorDefault);
        mPaint.setTextSize(isHighlight ? sizeHighlight : sizeDefault);

        // x = 歌词View宽度/2 - 歌词文本宽度/2
        float x = mWidth / 2- getTextWidth(lyricsText) / 2;
        // 绘制高亮行歌词
        canvas.drawText(lyricsText, x, y, mPaint);
    }


    /** 获取文本的宽度 */
    private int getTextWidth(String text) {
        Rect bounds = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    /** 获取文本的高度 */
    private int getTextHeight(String text) {
        Rect bounds = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.height();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    /**
     * ctrl + alt + H: 查看方法在哪里被调用了
     *
     * 更新当前播放时间，每隔300ms会刷新一次
     * @param currentPosition 当前播放时间
     */
    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;

        // 没有歌词
        if (lyricsData == null || lyricsData.size() < 1) {
            highlightIndex = 0;
            invalidate();
            return;
        }

        // 根据当前播放时间计算哪一行是高亮行 （计算highlightIndex的值）
//        For (int i = 0; i < 总行数；i ++) {
//            If (当前时间播放时间 > 第i行的开始显示时间) {
//                If (第i行是最后一行){
//                    说明：第i行就是高亮行
//                    break ;
//                }
//                If ( 当前时间播放时间  <= 第i+1行的开始显示时间) {
//                    说明：第i行就是高亮行
//                    break ;
//                }
//            }
//        }

        for (int i = 0; i < lyricsData.size(); i++) {
            if (currentPosition > lyricsData.get(i).startTime) {
                if (i == lyricsData.size() - 1) {
                    // 最后一行是高亮行
                    highlightIndex = i;
                    break;
                }

                if (currentPosition <= lyricsData.get(i+1).startTime) {
                    // 第i行是高亮行
                    highlightIndex = i;
                    break;
                }
            }
        }
        invalidate();  // 会调用onDraw方法重新绘制，刷新界面
    }

    /**
     * 设置音频的路径，加载并显示真实歌词
     *
     * @param path 音频的路径
     */
    public void setAudioPath(String path) {
      /*  lyricsData = LyricsLoader.loadLyricsData(path);
        highlightIndex = 0;     // 默认值为0
        invalidate();*/
    }
}















