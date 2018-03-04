package net.suntrans.suntransyanshi.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.suntrans.suntransyanshi.R;
import net.suntrans.suntransyanshi.ScanDevices_Activity;
import net.suntrans.suntransyanshi.sensus.Sixsensor_activity;
import net.suntrans.suntransyanshi.slslc10.St_slc_10list_activity;
import net.suntrans.suntransyanshi.stslc6.Stslc6_activity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Looney on 2017/2/21.
 */

public class RecyclerAdapter extends RecyclerView.Adapter {
    private final int HEADER = 1;
    private final int ITEM = 0;
    private Context context;
    public static final String ST_SLC_10 = "ST-SLC-10";
    public static final String ST_SLC_6 = "ST-SLC-6";
    public static final String sensus = "sensus";
    public static final String ST_SLC_23 = "ST-SLC-2/3";

    private List<SparseArray<String>> deviceNames;
    private List<SparseArray<String>> devicesDes;
    private int[] imageIds = {R.drawable.aswitch, R.drawable.aswitch, R.drawable.sixsonsor};

    public RecyclerAdapter(Context context) {
        this.context = context;

        deviceNames = new ArrayList<>();
        devicesDes = new ArrayList<>();
        SparseArray<String> array0 = new SparseArray<>();
        array0.put(1, context.getString(R.string.st_slc_10));
        array0.put(2, context.getString(R.string.st_slc10_des));
        deviceNames.add(array0);

        SparseArray<String> array1 = new SparseArray<>();
        array1.put(1, context.getString(R.string.st_slc_6));
        array1.put(2, context.getString(R.string.st_slc6_des));
        deviceNames.add(array1);

        SparseArray<String> array2 = new SparseArray<>();
        array2.put(1, context.getString(R.string.six_sensor));
        array2.put(2, context.getString(R.string.sensor_des));
        deviceNames.add(array2);
        SparseArray<String> array3 = new SparseArray<>();
        array3.put(1, context.getString(R.string.st_slc_2_3));
        array3.put(2, context.getString(R.string.st_slc_2_3_des));
        deviceNames.add(array3);




    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder1 holder1 = new ViewHolder1(LayoutInflater.from(context).inflate(R.layout.item_mainactivity, null));
        return holder1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder1) {
            ((ViewHolder1) holder).setData(position);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }


    class ViewHolder1 extends RecyclerView.ViewHolder {
        TextView name;
        LinearLayout ll;
        TextView number;
        TextView des;
        public ViewHolder1(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            number = (TextView) itemView.findViewById(R.id.number);
            des = (TextView) itemView.findViewById(R.id.des);

            ll = (LinearLayout) itemView.findViewById(R.id.ll);
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = null;
                    if (getAdapterPosition() == 0) {
                        intent = new Intent(context, St_slc_10list_activity.class);
                        intent.putExtra("devicetype", ST_SLC_10);
                        context.startActivity(intent);
                    } else if (getAdapterPosition() == 1) {
                        intent = new Intent(context, Stslc6_activity.class);
                        context.startActivity(intent);
                    } else if (getAdapterPosition() == 2) {
                        intent = new Intent(context, Sixsensor_activity.class);
                        intent.putExtra("devicetype", sensus);
                        context.startActivity(intent);
                    }

                }
            });
        }

        public void setData(int position) {
            name.setText(deviceNames.get(position).get(1));
            number .setText("0"+(position+1));
            des .setText(""+deviceNames.get(position).get(2));
        }
    }


}
