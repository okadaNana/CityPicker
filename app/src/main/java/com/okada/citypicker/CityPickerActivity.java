package com.okada.citypicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

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

public class CityPickerActivity extends AppCompatActivity implements OnWheelChangedListener {

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

    private BeanProvince mCurrentChooseProvinceName;
    private BeanCity mCurrentChooseCityName;
    private BeanDistrict mCurrentChooseDistrictName;

    private TextView mTvChooseProvince;
    private TextView mTvChooseCity;
    private TextView mTvDistrict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_picker);

        mTvChooseProvince = (TextView) findViewById(R.id.tv_choose_province);
        mTvChooseCity = (TextView) findViewById(R.id.tv_choose_city);
        mTvDistrict = (TextView) findViewById(R.id.tv_choose_district);

        mWheelViewProvince = (WheelView) findViewById(R.id.id_province);
        mWheelViewCity = (WheelView) findViewById(R.id.id_city);
        mWheelViewDistrict = (WheelView) findViewById(R.id.id_district);

        mWheelViewProvince.addChangingListener(this);
        mWheelViewCity.addChangingListener(this);
        mWheelViewDistrict.addChangingListener(this);

        initData();
        initWheelView();
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

        ArrayWheelAdapter<BeanProvince> provinceWheelAdapter = new ArrayWheelAdapter<>(this, mAllBeanProvinces);
        provinceWheelAdapter.setPadding(10);
        provinceWheelAdapter.setTextSize(20);
        mWheelViewProvince.setViewAdapter(provinceWheelAdapter);

        ArrayWheelAdapter<BeanCity> cityWheelAdapter = new ArrayWheelAdapter<>(this, mCurrentBeanCities);
        cityWheelAdapter.setPadding(10);
        cityWheelAdapter.setTextSize(20);
        mWheelViewCity.setViewAdapter(cityWheelAdapter);

        ArrayWheelAdapter<BeanDistrict> districtWheelAdapter = new ArrayWheelAdapter<>(this, mCurrentBeanDistricts);
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
            mCurrentChooseDistrictName = mCityToDistrictMap.get(mCurrentChooseCityName.getName())[newValue];
        }
    }

    private void updateCities() {
        int provinceIndex = mWheelViewProvince.getCurrentItem();
        mCurrentChooseProvinceName = mAllBeanProvinces[provinceIndex];
        BeanCity[] citiesOfProvince = mProvinceToCityMap.get(mCurrentChooseProvinceName.getName());

        ArrayWheelAdapter cityWheelAdapter = new ArrayWheelAdapter<>(this, citiesOfProvince);
        cityWheelAdapter.setPadding(10);
        cityWheelAdapter.setTextSize(20);

        mWheelViewCity.setViewAdapter(cityWheelAdapter);
        mWheelViewCity.setCurrentItem(0);

        updateDistrict();
    }

    private void updateDistrict() {
        int cityIndex = mWheelViewCity.getCurrentItem();
        mCurrentChooseCityName = mProvinceToCityMap.get(mCurrentChooseProvinceName.getName())[cityIndex];
        BeanDistrict[] districtArray = mCityToDistrictMap.get(mCurrentChooseCityName.getName());

        ArrayWheelAdapter districtWheelAdapter = new ArrayWheelAdapter<>(this, districtArray);
        districtWheelAdapter.setPadding(10);
        districtWheelAdapter.setTextSize(20);

        mWheelViewDistrict.setViewAdapter(districtWheelAdapter);
        mWheelViewDistrict.setCurrentItem(0);
    }
}
