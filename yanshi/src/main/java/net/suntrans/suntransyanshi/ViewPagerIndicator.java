package net.suntrans.suntransyanshi;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import net.suntrans.suntransyanshi.utils.UiUtils;

/**
 * Created by Administrator on 2017/4/11.
 */

public class ViewPagerIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {
    private ViewPager viewPager;
    private LayoutParams unSelectParams;
    private LayoutParams selectedParams;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.HORIZONTAL);
        unSelectParams = new LayoutParams(
                UiUtils.dip2px(4), UiUtils.dip2px(4));
        unSelectParams.leftMargin = UiUtils.dip2px(5);
        unSelectParams.rightMargin = UiUtils.dip2px(5);
        unSelectParams.gravity= Gravity.CENTER_VERTICAL;


        selectedParams = new LayoutParams(
                UiUtils.dip2px(6), UiUtils.dip2px(6));
        selectedParams.leftMargin = UiUtils.dip2px(5);
        selectedParams.rightMargin = UiUtils.dip2px(5);
        unSelectParams.gravity= Gravity.CENTER_VERTICAL;
    }

    public void setUpViewPager(ViewPager viewPager) {
        if (viewPager == null) {
            return;
        }
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(this);
        }
        this.viewPager = viewPager;
        int count = viewPager.getAdapter().getCount();
        // 初始化小圆点
        for (int i = 0; i < count; i++) {
            View point = new View(getContext());
            point.setBackgroundResource(R.drawable.shape_point_gray);// 设置引导页默认圆点
            point.setLayoutParams(unSelectParams);// 设置圆点的大小
            this.addView(point);// 将圆点添加给线性布局
        }
        setCurrentItem(0);
    }

    public void setCurrentItem(int position) {
        if (viewPager == null) {
            throw new IllegalStateException("ViewPager has not been found");
        }
        for (int i = 0; i < getChildCount(); i++) {
            if (i == position) {

                this.getChildAt(i).setBackgroundResource(R.drawable.shape_point_red);
                this.getChildAt(i).setLayoutParams(selectedParams);
            } else {
                this.getChildAt(i).setBackgroundResource(R.drawable.shape_point_gray);
                this.getChildAt(i).setLayoutParams(unSelectParams);
            }
        }
        viewPager.setCurrentItem(position);
        requestLayout();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setCurrentItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
