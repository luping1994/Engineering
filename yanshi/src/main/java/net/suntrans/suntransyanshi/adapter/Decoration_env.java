package net.suntrans.suntransyanshi.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.suntrans.suntransyanshi.R;
import net.suntrans.suntransyanshi.utils.UiUtils;

/**
 * Created by Looney on 2017/3/2.
 */

public class Decoration_env extends RecyclerView.ItemDecoration {
    private int tagWidth;
    private Paint paint;
    private Paint textpaint;

    public Decoration_env(Context context) {
        Paint.FontMetrics fontMetrics = new Paint.FontMetrics();
        paint = new Paint();
        textpaint = new Paint();
        textpaint.setTypeface(Typeface.DEFAULT_BOLD);
        textpaint.setAntiAlias(true);
        textpaint.setTextSize(UiUtils.dip2px(12));
        textpaint.getFontMetrics(fontMetrics);
        textpaint.setTextAlign(Paint.Align.LEFT);
        textpaint.setColor(context.getResources().getColor(R.color.secondary_text));

        paint.setColor(context.getResources().getColor(R.color.bg_color));
        tagWidth = context.getResources().getDimensionPixelSize(R.dimen.tag_width);
    }


    private int dividerHeight = UiUtils.dip2px(40);
    private int oneDp = UiUtils.dip2px(1);
    private int headerHeight = UiUtils.dip2px(25);

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        if (pos == 0) {
            outRect.bottom = dividerHeight;
            outRect.top = UiUtils.dip2px(20);
        } else if (pos == 1 || pos == 6 || pos == 11) {
            outRect.top = headerHeight;
        } else {
            outRect.bottom = oneDp;
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            if (hasHeader(position)) {
                String textLine = getHeaderText(position);
                float top = view.getTop() - headerHeight;
                float bottom = view.getTop();
                c.drawRect(left, top, right, bottom, paint);//绘制矩形
                c.drawText(textLine, left, bottom-UiUtils.dip2px(2), textpaint);//绘制文本
            }

        }
    }

    private String getHeaderText(int position) {
        if (position == 1) {
            return "空气质量";
        }
        if (position == 6)
            return "室内环境";
        if (position == 11)
            return "姿态信息";
        return "wu";
    }

    private boolean hasHeader(int position) {

        if (position == 1 || position == 6 || position == 11) {
            return true;
        }
        return false;
    }
}
