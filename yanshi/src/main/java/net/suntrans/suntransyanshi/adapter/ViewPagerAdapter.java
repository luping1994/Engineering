package net.suntrans.suntransyanshi.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import net.suntrans.suntransyanshi.R;
import net.suntrans.suntransyanshi.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/11.
 */

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    List<View> mLists;
    private final int[] resid={R.drawable.ic_homepage1,R.drawable.ic_homepage2,R.drawable.ic_homepage3};
    private String[] titles = new String[]{"智能家居APP","十通道智能继电器控制器","第六感官环境监测"};
    public ViewPagerAdapter(Context context) {
        this.context= context;
        init();
    }

    private void init() {
        mLists = new ArrayList<>();
        for (int i=0;i<3;i++){
            View view = LayoutInflater.from(context).inflate(R.layout.imageview,null,false);
//            int height = UiUtils.dip2px((int) context.getResources().getDimension(R.dimen.app_bar_height));
//            int width = UiUtils.getDisplaySize(context.getApplicationContext())[0];
//            Bitmap bitmap = compressBitmap(context.getResources(), resid[i], width, height);
            ((TextView)view.findViewById(R.id.title)).setText(titles[i]);
            ((ImageView)view.findViewById(R.id.imageView)).setImageResource(resid[i]);
            mLists.add(view);
        }
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //对ViewPager页号求模取出View列表中要显示的项
        position %= mLists.size();
        if (position<0){
            position = mLists.size()+position;
        }
        View view = mLists.get(position);
        //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        ViewParent vp =view.getParent();
        if (vp!=null){
            ViewGroup parent = (ViewGroup)vp;
            parent.removeView(view);
        }
        container.addView(view);
        //add listeners here if necessary
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView((View) object);
    }
    /**
     * @param res Resource
     * @param resId 资源id
     * @param targetWidth 目标图片的宽，单位px
     * @param targetHeight 目标图片的高，单位px
     * @return 返回压缩后的图片的Bitmap
     */
    public Bitmap compressBitmap(Resources res, int resId, int targetWidth, int targetHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//设为true，节约内存
        BitmapFactory.decodeResource(res, resId, options);//返回null
        int height = options.outHeight;//得到源图片height，单位px
        int width = options.outWidth;//得到源图片的width，单位px
        //计算inSampleSize
        options.inSampleSize = calculateInSampleSize(width,height,targetWidth,targetHeight);
        options.inJustDecodeBounds = false;//设为false，可以返回Bitmap
        return BitmapFactory.decodeResource(res,resId,options);
    }

    /**
     * 计算压缩比例
     * @param width  源图片的宽
     * @param height 源图片的高
     * @param targetWidth  目标图片的宽
     * @param targetHeight 目标图片的高
     * @return inSampleSize 压缩比例
     */
    public int calculateInSampleSize(int width,int height, int targetWidth, int targetHeight) {
        int inSampleSize = 1;
        if (height > targetHeight || width > targetWidth) {
            //计算图片实际的宽高和目标图片宽高的比率
            final int heightRate = Math.round((float) height / (float) targetHeight);
            final int widthRate = Math.round((float) width / (float) targetWidth);
            //选取最小的比率作为inSampleSize
            inSampleSize = heightRate < widthRate ? heightRate : widthRate;
        }
        return inSampleSize;
    }
}
