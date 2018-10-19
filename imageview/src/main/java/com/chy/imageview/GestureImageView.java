package com.chy.imageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @author chenhongye
 */
@SuppressLint("AppCompatCustomView")
public class GestureImageView extends ImageView implements View.OnClickListener {
    private static String TAG = "GestureImageView";
    private int lastX;//触摸点的X坐标
    private int lasty;//触摸点的Y坐标

    private boolean isFrist = true;//记录获得的原始position
    private int originalLeft;//原始的leftpadding
    private int originalTop;//原始的toppadding
    private int originalRight;//原始的rightpadding
    private int originalBottom;//原始的bottompadding

    private int scaleLeft;//放大后的leftpadding
    private int scaleTop;//放大后toppadding
    private int scaleRight;//放大的rightpadding
    private int scaleBottom;//放大的bottompadding

    private int moveLeft;//移动后的leftpadding
    private int moveTop;//移动后的toppadding
    private int moveRight;//移动后的rightpadding
    private int moveBottom;//移动后的bottompadding


    private int screenWidth;//屏幕宽度
    private int screenHeight;//屏幕高度

    private int viewWidht;//控件的宽度
    private int viewHeight;//控件的高度

    /**
     * 透明度操作
     */
    private boolean changeAlpha = true;
    private float alpha = 1f;
    private float miniAlpha = 0.5f;
    private float maxAlpha = 1f;

    /**
     * 缩放操作
     */
    private boolean changeScale = true;
    private float scale = 1f;
    private float miniScale = 0.5f;
    private float maxScale = 1f;

    /**
     * 双击操作
     */
    private boolean doubleClick = true;
    private boolean isAmplification = false;
    private boolean firstClick = false;
    private boolean secondClick = false;

    public GestureImageView(Context context) {
        super(context);
        init(context);
    }

    public GestureImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GestureImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.setAdjustViewBounds(true);
        this.setOnClickListener(this);
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        getVIewWidhtAndHeight();
    }

    private void getVIewWidhtAndHeight() {
        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        this.measure(w, h);
        viewWidht = this.getMeasuredWidth();
        viewHeight = this.getMeasuredHeight();
    }

    private void initView() {
        originalLeft = getLeft();
        originalTop = getTop();
        originalRight = getRight();
        originalBottom = getBottom();

        moveLeft = originalLeft;
        moveTop = originalTop;
        moveRight = originalRight;
        moveBottom = originalBottom;

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int contactsNumber = event.getPointerCount();
        int x0 = (int) event.getX(0);
        int y0 = (int) event.getY(0);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                actionDown(x0, y0);
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(x0, y0);
                break;
            case MotionEvent.ACTION_UP:
                //移动到屏幕左、上、右的边界，就自动把图片移动到原始位置
                if (!doubleClick || !isAmplification) {
                    if (moveLeft < 100 - screenWidth || moveTop < 0 - screenHeight / 3 || moveLeft > screenWidth - 100) {
                        layout(originalLeft, originalTop, originalRight, originalBottom);
                    }
                }
                if (!isAmplification)
                    if (originalLeft - moveLeft > 5 || originalTop - moveTop > 5 || moveRight - originalRight > 5) {
                        //往下滑动200距离就拦截滑动事件
                        if (moveBottom - originalBottom > screenHeight / 4) {
                            return super.onTouchEvent(event);
                        }
                        return true;
                    }
                if (firstClick) {
                    secondClick = true;
                }
                firstClick = true;
                //双击事件
                DoubleClickEvent();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 双击事件
     */
    private void DoubleClickEvent() {
        if (doubleClick) {
            if (firstClick)
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (secondClick) {
                            if (!isAmplification) {
                                isAmplification = true;
                                GestureImageView.this.setScaleX(2.0f);
                                GestureImageView.this.setScaleY(2.0f);
                            } else {
                                isAmplification = false;
                                GestureImageView.this.setScaleX(1.0f);
                                GestureImageView.this.setScaleY(1.0f);
                            }
                            firstClick = false;
                            secondClick = false;
                        }
                    }
                }, 300);
        }
    }

    /**
     * ACTION_DOWN事件
     *
     * @param x 触点的横轴坐标
     * @param y 触点的纵轴坐标
     */
    private void actionDown(int x, int y) {
        //记录触摸点的坐标
        lastX = x;
        lasty = y;
        if (isFrist) {
            isFrist = false;
            initView();
        }
    }

    /**
     * ACTION_MOVE事件
     *
     * @param x 触点的横轴坐标
     * @param y 触点的纵轴坐标
     */
    private void actionMove(int x, int y) {
        //计算偏移量
        int offsetX = x - lastX;
        int offsetY = y - lasty;
        //在当前left,top,right,bottom的基础上加上偏移量
        moveLeft = getLeft() + offsetX;
        moveTop = getTop() + offsetY;
        moveRight = getRight() + offsetX;
        moveBottom = getBottom() + offsetY;
        layout(moveLeft, moveTop, moveRight, moveBottom);
        //设置透明度和缩放的状态
        if (!doubleClick)
            setAlphaAndScaleStatus();
    }

    /**
     * 设置透明度和缩放的状态
     */
    private void setAlphaAndScaleStatus() {
        if (moveBottom - originalBottom > 5) {
            //是否开始透明度变化
            if (changeAlpha)
                if (alpha > miniAlpha) {
                    alpha = alpha - 0.005f;
                    this.setAlpha(alpha);
                }
            //是否开始缩放变化
            if (changeScale)
                if (scale > miniScale) {
                    scale = scale - 0.005f;
                    this.setScaleX(scale);
                    this.setScaleY(scale);
                }
        } else {
            //是否开始透明度变化
            if (changeAlpha)
                if (alpha < maxAlpha) {
                    alpha = alpha + 0.005f;
                    this.setAlpha(alpha);
                }
            //是否开始缩放变化
            if (changeScale)
                if (scale < maxScale) {
                    scale = scale + 0.005f;
                    this.setScaleX(scale);
                    this.setScaleY(scale);
                }
        }
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getContext(), "退出", Toast.LENGTH_SHORT).show();
    }

    /**
     * 是否开启透明度变化
     *
     * @param changeAlpha 是或否
     */
    public void setChangeAlpha(boolean changeAlpha) {
        this.changeAlpha = changeAlpha;
    }

    /**
     * 是否开启缩放变化
     *
     * @param changeScale 是或否
     */
    public void setChangeScale(boolean changeScale) {
        this.changeScale = changeScale;
    }

    /**
     * 设置最低的透明度值
     *
     * @param miniAlpha 最低的透明度值，默认0.5f
     */
    public void setMiniAlpha(float miniAlpha) {
        this.miniAlpha = miniAlpha;
    }

    /**
     * 设置最低的缩放值
     *
     * @param miniScale 最低的缩放值，默认0.5f
     */
    public void setMiniScale(float miniScale) {
        this.miniScale = miniScale;
    }

    /**
     * 是否开启双击功能
     *
     * @param doubleClick 是或否
     */
    public void setDoubleClick(boolean doubleClick) {
        this.doubleClick = doubleClick;
    }

    public Bitmap getBitmap() {
        this.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        return bitmap;
    }
}
