package com.zzj.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yinxq on 2018/1/18 0018.
 * "basic: {
 city: "南京",
 id: "CN101190101",
 lat: "32.04154587",
 lon: "118.76741028",
 update: {
 loc: "2018-01-18 07:58"
 }
 }
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    /**
     * 经度值
     */
    public float lat;
    /**
     * 纬度值
     */
    public float lon;
    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
