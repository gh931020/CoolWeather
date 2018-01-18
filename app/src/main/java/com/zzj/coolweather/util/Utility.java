package com.zzj.coolweather.util;

import android.text.TextUtils;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zzj.coolweather.db.City;
import com.zzj.coolweather.db.County;
import com.zzj.coolweather.db.Province;
import com.zzj.coolweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yinxq on 2018/1/17 0017.
 */

public class Utility {
    /**
     * 处理请求返回的省级数据
     * @param response
     * @return
     */
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray array = new JSONArray(response);
                for (int i = 0;i<array.length();i++){
                    JSONObject object = array.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceCode(object.getInt("id"));
                    province.setProvinceName(object.getString("name"));
                    //将数据保存到数据库
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理请求返回的市级数据
     * @param response
     * @return
     */
    public static boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray array = new JSONArray(response);
                for (int i = 0;i<array.length();i++){
                    JSONObject object = array.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(object.getInt("id"));
                    city.setCityName(object.getString("name"));
                    city.setProvinceCode(provinceId);
                    //将数据保存到数据库
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 处理请求返回的县级数据
     * @param response
     * @return
     */
    public static boolean handleCountyResponse(String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray array = new JSONArray(response);
                for (int i = 0;i<array.length();i++){
                    JSONObject object = array.getJSONObject(i);
                    County county = new County();
                    county.setWeatherId(object.getString("weather_id"));
                    county.setCountyName(object.getString("name"));
                    county.setCityId(cityId);
                    //将数据保存到数据库
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse(String response){
        try {
            //获取返回的天气数据
            JSONObject jsonObject = new JSONObject(response);
            //返回数据为数组格式
            JSONArray array = jsonObject.getJSONArray("HeWeather");
            //从数组中取出所需数据
            String weatherContent = array.getJSONObject(0).toString();
            //利用Gson将数据转换成对象
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
