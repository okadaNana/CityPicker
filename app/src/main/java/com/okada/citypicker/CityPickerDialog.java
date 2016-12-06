package com.okada.citypicker;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.okada.citypicker.model.BeanCity;
import com.okada.citypicker.model.BeanDataSource;
import com.okada.citypicker.model.BeanDistrict;
import com.okada.citypicker.model.BeanProvince;
import com.okada.citypicker.wheelview.OnWheelChangedListener;
import com.okada.citypicker.wheelview.WheelView;
import com.okada.citypicker.wheelview.adapters.ArrayWheelAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityPickerDialog extends DialogFragment implements OnWheelChangedListener {

    /**
     * 省 WheelView
     */
    private WheelView mWheelViewProvince;
    /**
     * 市 WheelView
     */
    private WheelView mWheelViewCity;
    /**
     * 区 WheelView
     */
    private WheelView mWheelViewDistrict;

    /**
     * 所有省的数据
     */
    private BeanProvince[] mAllBeanProvinces;
    /**
     * 当前显示的市数据
     */
    private BeanCity[] mCurrentBeanCities;
    /**
     * 当前显示的区数据
     */
    private BeanDistrict[] mCurrentBeanDistricts;

    /**
     * <省 - 下面的市>
     */
    private Map<String, BeanCity[]> mProvinceToCityMap = new HashMap<>();
    /**
     * <市 - 下面的区>
     */
    private Map<String, BeanDistrict[]> mCityToDistrictMap = new HashMap<>();

    private BeanProvince mCurrentChooseProvince;
    private BeanCity mCurrentChooseCity;
    private BeanDistrict mCurrentChooseDistrict;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_city_picker_dialog);
        dialog.setCanceledOnTouchOutside(true);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);

        init(dialog);

        return dialog;
    }

    public interface OnCityChooseListener {
        void onCityChoose(BeanProvince beanProvince, BeanCity beanCity, BeanDistrict beanDistrict);
    }

    public void setOnCityChooseListener(OnCityChooseListener onCityChooseListener) {
        mOnCityChooseListener = onCityChooseListener;
    }

    private OnCityChooseListener mOnCityChooseListener;

    private void init(Dialog dialog) {
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCityChooseListener != null) {
                    mOnCityChooseListener.onCityChoose(mCurrentChooseProvince,
                            mCurrentChooseCity, mCurrentChooseDistrict);
                }
                dismiss();
            }
        });

        mWheelViewProvince = (WheelView) dialog.findViewById(R.id.id_province);
        mWheelViewCity = (WheelView) dialog.findViewById(R.id.id_city);
        mWheelViewDistrict = (WheelView) dialog.findViewById(R.id.id_district);

        mWheelViewProvince.setShadowColor(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
        mWheelViewCity.setShadowColor(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
        mWheelViewDistrict.setShadowColor(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);

        mWheelViewProvince.addChangingListener(this);
        mWheelViewCity.addChangingListener(this);
        mWheelViewDistrict.addChangingListener(this);

        initData();
        initWheelView();
        updateCities();
        updateDistrict();
    }

    public void initData() {
        BeanDataSource beanDataSource = new Gson().fromJson(getCityJSONStr(), BeanDataSource.class);

        setAllBeanProvinces(beanDataSource);
        setCurrentBeanCities(beanDataSource);
        setCurrentBeanDistricts(beanDataSource);

        setProvinceCityDistrictMap(beanDataSource);
    }

    private void setProvinceCityDistrictMap(BeanDataSource beanDataSource) {
        List<BeanProvince> allBeanProvinces = beanDataSource.getList();
        for (BeanProvince beanProvince : allBeanProvinces) {
            List<BeanCity> allCityOfAProvince = beanProvince.getList();
            BeanCity[] cityArray = new BeanCity[allCityOfAProvince.size()];
            for (int i = 0; i < allCityOfAProvince.size(); i++) {
                cityArray[i] = allCityOfAProvince.get(i);
            }
            mProvinceToCityMap.put(beanProvince.getName(), cityArray);


            for (BeanCity beanCity : allCityOfAProvince) {
                List<BeanDistrict> allDistrictOfACity = beanCity.getList();
                BeanDistrict[] districtArray = new BeanDistrict[allDistrictOfACity.size()];
                for (int i = 0; i < allDistrictOfACity.size(); i++) {
                    districtArray[i] = allDistrictOfACity.get(i);
                }
                mCityToDistrictMap.put(beanCity.getName(), districtArray);
            }
        }
    }

    private void initWheelView() {
        mWheelViewProvince.setVisibleItems(7);
        mWheelViewCity.setVisibleItems(7);
        mWheelViewDistrict.setVisibleItems(7);

        mWheelViewProvince.setCyclic(false);
        mWheelViewCity.setCyclic(false);
        mWheelViewDistrict.setCyclic(false);

        ArrayWheelAdapter<BeanProvince> provinceWheelAdapter = new ArrayWheelAdapter<>(getContext(), mAllBeanProvinces);
        provinceWheelAdapter.setPadding(10);
        provinceWheelAdapter.setTextSize(20);
        mWheelViewProvince.setViewAdapter(provinceWheelAdapter);

        ArrayWheelAdapter<BeanCity> cityWheelAdapter = new ArrayWheelAdapter<>(getContext(), mCurrentBeanCities);
        cityWheelAdapter.setPadding(10);
        cityWheelAdapter.setTextSize(20);
        mWheelViewCity.setViewAdapter(cityWheelAdapter);

        ArrayWheelAdapter<BeanDistrict> districtWheelAdapter = new ArrayWheelAdapter<>(getContext(), mCurrentBeanDistricts);
        districtWheelAdapter.setPadding(10);
        districtWheelAdapter.setTextSize(20);
        mWheelViewDistrict.setViewAdapter(districtWheelAdapter);
    }

    private void setAllBeanProvinces(BeanDataSource beanDataSource) {
        List<BeanProvince> beanProvinces = beanDataSource.getList();

        mAllBeanProvinces = new BeanProvince[beanProvinces.size()];
        for (int i = 0; i < beanProvinces.size(); i++) {
            mAllBeanProvinces[i] = beanProvinces.get(i);
        }
    }

    private void setCurrentBeanCities(BeanDataSource beanDataSource) {
        List<BeanCity> beanCities = beanDataSource.getList().get(0).getList();

        mCurrentBeanCities = new BeanCity[beanCities.size()];
        for (int i = 0; i < beanCities.size(); i++) {
            mCurrentBeanCities[i] = beanCities.get(i);
        }
    }

    private void setCurrentBeanDistricts(BeanDataSource beanDataSource) {
        List<BeanDistrict> beanDistricts = beanDataSource.getList().get(0).getList().get(0).getList();

        mCurrentBeanDistricts = new BeanDistrict[beanDistricts.size()];
        for (int i = 0; i < beanDistricts.size(); i++) {
            mCurrentBeanDistricts[i] = beanDistricts.get(i);
        }
    }

    private String getCityJSONStr() {
        InputStream inputStream = getResources().openRawResource(R.raw.city);

        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mWheelViewProvince) {
            updateCities();
        } else if (wheel == mWheelViewCity) {
            updateDistrict();
        } else if (wheel == mWheelViewDistrict) {
            mCurrentChooseDistrict = mCityToDistrictMap.get(mCurrentChooseCity.getName())[newValue];
        }
    }

    private void updateCities() {
        int provinceIndex = mWheelViewProvince.getCurrentItem();
        mCurrentChooseProvince = mAllBeanProvinces[provinceIndex];
        BeanCity[] citiesOfProvince = mProvinceToCityMap.get(mCurrentChooseProvince.getName());

        ArrayWheelAdapter cityWheelAdapter = new ArrayWheelAdapter<>(getContext(), citiesOfProvince);
        cityWheelAdapter.setPadding(10);
        cityWheelAdapter.setTextSize(20);

        mWheelViewCity.setViewAdapter(cityWheelAdapter);
        mWheelViewCity.setCurrentItem(0);

        updateDistrict();
    }

    private void updateDistrict() {
        int cityIndex = mWheelViewCity.getCurrentItem();
        mCurrentChooseCity = mProvinceToCityMap.get(mCurrentChooseProvince.getName())[cityIndex];
        BeanDistrict[] districtArray = mCityToDistrictMap.get(mCurrentChooseCity.getName());

        ArrayWheelAdapter districtWheelAdapter = new ArrayWheelAdapter<>(getContext(), districtArray);
        districtWheelAdapter.setPadding(10);
        districtWheelAdapter.setTextSize(20);

        mWheelViewDistrict.setViewAdapter(districtWheelAdapter);
        mWheelViewDistrict.setCurrentItem(0);

        mCurrentChooseDistrict = mCurrentChooseCity.getList().get(0);
    }
}
