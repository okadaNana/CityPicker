package com.okada.citypicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.okada.citypicker.model.BeanCity;
import com.okada.citypicker.model.BeanDistrict;
import com.okada.citypicker.model.BeanProvince;

public class MainActivity extends AppCompatActivity implements CityPickerDialog.OnCityChooseListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CityPickerDialog cityPickerDialog = new CityPickerDialog();
                cityPickerDialog.setOnCityChooseListener(MainActivity.this);
                cityPickerDialog.show(getSupportFragmentManager(), "");
            }
        });
    }



    @Override
    public void onCityChoose(BeanProvince beanProvince, BeanCity beanCity, BeanDistrict beanDistrict) {
        Toast.makeText(this, beanProvince.getName() + beanCity.getList() + beanDistrict.getName(), Toast.LENGTH_SHORT).show();
    }
}
