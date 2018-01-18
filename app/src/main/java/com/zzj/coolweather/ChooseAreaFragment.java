package com.zzj.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzj.coolweather.db.City;
import com.zzj.coolweather.db.County;
import com.zzj.coolweather.db.Province;
import com.zzj.coolweather.util.HttpUtil;
import com.zzj.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by yinxq on 2018/1/17 0017.
 */

public class ChooseAreaFragment extends Fragment {
    //用于标记省市县三级
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    public static final String BASE_URL = "http://guolin.tech/api/china/";

    private TextView title;
    private Button back;
    private ListView listView;
    //用于显示数据的适配器
    private ArrayAdapter<String> arrayAdapter;

    private List<String> dataList = new ArrayList<>();
    /**
     * 存放省级名称的集合
     */
    private List<Province> provinces;
    private List<City> cities;
    private List<County> counties;
    /**
     * 选中的省份
     */
    private Province selectProvince;
    private City selectCity;//选中的城市
    private County selectCounty;
    /**
     * 当前选中的级别
     */
    private int currentLevel;

    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container,false);
        title = view.findViewById(R.id.title_text);
        back = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(arrayAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    //如果当前选中的是省级区域，就开始搜索对应的市级区域；
                    selectProvince = provinces.get(position);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    //如果当前选中的是市级区域，就开始搜索对应的县级区域；
                    selectCity = cities.get(position);
                    queryCounties();
                }else if (currentLevel == LEVEL_COUNTY){
                    String weatherId = counties.get(position).getWeatherId();
                    Intent intent = new Intent(getActivity(),WeatherActivity.class);
                    intent.putExtra("weather_id",weatherId);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果当前是县级列表，按下返回按钮后，显示市级列表
                if (currentLevel == LEVEL_COUNTY){
                    queryCities();
                //如果当前是市级列表，按下back后，显示省级列表
                }else if (currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        //开始默认显示省级列表
        queryProvinces();
    }

    /**
     * 查询全国所有的省份，优先从数据库中查询，数据库中无数据就从服务器获取
     */
    private void queryProvinces() {
        title.setText("中国");
        back.setVisibility(View.GONE);
        //从数据库中查询所有的省份
        provinces = DataSupport.findAll(Province.class);
        if (provinces.size() > 0){
            dataList.clear();
            for (Province province:provinces) {
                dataList.add(province.getProvinceName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else{
            queryFromServer(BASE_URL,"province");
        }
    }

    private void queryCities() {
        title.setText(selectProvince.getProvinceName());
        back.setVisibility(View.VISIBLE);
        cities = DataSupport.where("provincecode = ?",String.valueOf(selectProvince.getProvinceCode())).find(City.class);
        if (cities.size()>0){
            dataList.clear();
            for (City city:cities) {
                dataList.add(city.getCityName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel= LEVEL_CITY;
        }else{
            int provinceId = selectProvince.getProvinceCode();
            queryFromServer(BASE_URL+provinceId,"city");
        }
    }

    private void queryCounties() {
        title.setText(selectCity.getCityName());
        back.setVisibility(View.VISIBLE);
        counties = DataSupport.where("cityid = ?",String.valueOf(selectCity.getCityCode())).find(County.class);
        if (counties.size()>0){
            dataList.clear();
            for (County county:counties) {
                dataList.add(county.getCountyName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else{
            int selectProvinceId = selectProvince.getProvinceCode();
            int selectCityId = selectCity.getCityCode();
            queryFromServer(BASE_URL+selectProvinceId+"/"+selectCityId,"county");
        }
    }

    /**
     * 根据传入的url和类型从服务器中获取数据
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if (type.equals("province")){
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    result = Utility.handleCityResponse(responseText,selectProvince.getProvinceCode());
                }else if ("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText,selectCity.getCityCode());
                }

                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });


    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (dialog == null){
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading...");
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
