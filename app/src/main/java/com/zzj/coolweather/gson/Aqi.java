package com.zzj.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yinxq on 2018/1/18 0018.
 * aqi: {
 city: {
 aqi: "180",
 qlty: "中度污染",
 pm25: "136"
 }
 }
 */

public class Aqi {
    @SerializedName("city")
    public AqiCity aqiCity;

    public class AqiCity {
        public String aqi;
        public String pm25;
        public String qlty;
    }
}
