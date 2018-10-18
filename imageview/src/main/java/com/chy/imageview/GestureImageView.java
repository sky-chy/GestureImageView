package com.chy.imageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @author chenhongye
 */
@SuppressLint("AppCompatCustomView")
public class GestureImageView extends ImageView implements View.OnClickListener {
    private int lastX;//触摸点的X坐标
    private int lasty;//触摸点的Y坐标

    private int left;//原始的leftpadding
    private int top;//原始的toppadding
    private int right;//原始的rightpadding
    private int bottom;//原始的bottompadding

    private int moveLeft;//移动后的leftpadding
    private int moveTop;//移动后的toppadding
    private int moveRight;//移动后的rightpadding
    private int moveBottom;//移动后的bottompadding

    private boolean isFrist = true;//记录获得的原始position

    private int screenWidth;//屏幕宽度
    private int screenHeight;//屏幕高度

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
        this.setOnClickListener(this);
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    private void initView() {
        left = getLeft();
        top = getTop();
        right = getRight();
        bottom = getBottom();

        moveLeft = left;
        moveTop = top;
        moveRight = right;
        moveBottom = bottom;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //记录触摸点的坐标
                lastX = x;
                lasty = y;
                if (isFrist) {
                    isFrist = false;
                    initView();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //计算偏移量
                int offsetX = x - lastX;
                int offsetY = y - lasty;
                //在当前left,top,right,bottom的基础上加上偏移量
                moveLeft = getLeft() + offsetX;
                moveTop = getTop() + offsetY;
                moveRight = getRight() + offsetX;
                moveBottom = getBottom() + offsetY;
                layout(moveLeft, moveTop, moveRight, moveBottom);
                break;
            case MotionEvent.ACTION_UP:
                if (moveLeft < 100 - screenWidth || moveTop < 0 - screenHeight / 3 || moveLeft > screenWidth - 100) {
                    layout(left, top, right, bottom);
                }

                if (left - moveLeft > 5 || top - moveTop > 5 || moveRight - right > 5) {
                    if (moveBottom - bottom > 200) {
                        return super.onTouchEvent(event);
                    }
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getContext(), "内部调用", Toast.LENGTH_SHORT).show();
    }
}
