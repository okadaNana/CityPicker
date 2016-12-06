package com.okada.citypicker;

import android.content.Context;

import com.google.gson.Gson;
import com.okada.citypicker.model.BeanCity;
import com.okada.citypicker.model.BeanDataSource;
import com.okada.citypicker.model.BeanDistrict;
import com.okada.citypicker.model.BeanProvince;
import com.okada.citypicker.wheelview.WheelView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class CityPicker {

    /** 省 WheelView */
    private WheelView mViewProvince;
    /** 市 WheelView */
    private WheelView mViewCity;
    /** 区 WheelView */
    private WheelView mViewDistrict;

    /** 当前显示的省数据 */
    private BeanProvince[] mCurrentBeanProvinces;
    /** 当前显示的市数据 */
    private BeanCity[] mCurrentBeanCities;
    /** 当前显示的区数据 */
    private BeanDistrict[] mCurrentBeanDistricts;

    /** <省 - 下面的市> */
    private Map<String, String[]> mProvinceToCityMap = new HashMap<>();
    /** <市 - 下面的区> */
    private Map<String, String[]> mCityToDistrictMap = new HashMap<>();

    public void initData(Context context) {
        BeanDataSource beanDataSource = new Gson().fromJson(getCityJSONStr(context), BeanDataSource.class);
    }

    private String getCityJSONStr(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.city);

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
}
