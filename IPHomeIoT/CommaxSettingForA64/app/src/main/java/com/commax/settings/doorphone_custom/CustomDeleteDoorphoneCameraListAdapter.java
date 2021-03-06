package com.commax.settings.doorphone_custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.commax.settings.R;
import com.commax.settings.common.Log;
import com.commax.settings.common.TypeDef;
import com.commax.settings.util.PlusViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 도어폰 카메라 삭제 리스트 어댑터
 * Created by bagjeong-gyu on 2016. 8. 22..
 */
public class CustomDeleteDoorphoneCameraListAdapter extends ArrayAdapter<CustomDevice> {


    private String LOG_TAG = CustomDeleteDoorphoneCameraListAdapter.class.getSimpleName();
    private CustomNameChangeListener mListener;
    private final LayoutInflater mLayoutInflater;
    private final List<CustomDevice> mDatas;
    private final Context mContext;


    public CustomDeleteDoorphoneCameraListAdapter(Context context, int resource, List<CustomDevice> devices) {
        super(context, resource, devices);
        mLayoutInflater = LayoutInflater.from(context);
        mDatas = devices;
        mContext = context;

        if(!TypeDef.OP_NEWDELETE_ADAPTOR_ENABLE) {
            try {
                mListener = (CustomNameChangeListener) context;
            } catch (ClassCastException e) {
                Log.d(LOG_TAG, "ClassCastException: " + e.getMessage());
            }
        }
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            if(TypeDef.OP_NEWDELETE_ADAPTOR_ENABLE) {
                convertView = mLayoutInflater.inflate(R.layout.list_item_delete_doorphone_camera2,
                        parent, false);
            } else {
                convertView = mLayoutInflater.inflate(R.layout.list_item_delete_doorphone_camera,
                        parent, false);
            }
        }

        //체크박스
        //ImageView deviceCheckbox = PlusViewHolder.get(convertView, R.id.deviceCheckbox);
        //deviceCheckbox.setSelected(true);
        //deviceCheckbox.setVisibility(View.GONE); //숨김

        //카메라 명
        final TextView deviceName = PlusViewHolder.get(convertView, R.id.deviceName);

        //String deviceNameString = mDatas.get(position).getName();
        String deviceNameString = mDatas.get(position).getModelName();

        if (deviceNameString == null) {
            deviceNameString = "";
        }

        deviceName.setText(deviceNameString);


        return convertView;
    }


    /**
     * Device 추가
     *
     * @param device
     */
    public void addDevice(CustomDevice device) {

        mDatas.add(device);
        notifyDataSetChanged();
    }

    /**
     * Device 삭제
     *
     * @param position
     */
    public void delDevice(int position) {

        mDatas.remove(position);
        notifyDataSetChanged();
    }


    public List<CustomDevice> getAllData() {
        return mDatas;
    }

    /**
     * Device 삭제
     *
     * @param device
     */
    public void deleteDevice(CustomDevice device) {
        mDatas.remove(device);
        notifyDataSetChanged();
    }

    public ArrayList<CustomDevice> getDevices() {
        return (ArrayList<CustomDevice>) mDatas;
    }
}
