package com.zzj.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yinxq on 2018/1/18 0018.
 * now: {
 cond: {
 code: "101",
 txt: "多云"
 }
 tmp: "3"
 wind: {
 dir: "东风",
 sc: "微风",
 spd: "4"
 }
 }
 */

public class Now {
    @SerializedName("tmp")
    public  String temperature;
    @SerializedName("cond")
    public TempInfo tempInfo;
    @SerializedName("wind")
    public WindInfo wind;


    public class TempInfo {
        public int code;
        @SerializedName("txt")
        public String tempInfo;
    }

    public class WindInfo {
        /**
         * 风向
         */
        @SerializedName("dir")
        public String wind_dir;
        /**
         * 风力
         */
        @SerializedName("sc")
        public String wind_force;
        /**
         * 风速
         */
        @SerializedName("spd")
        public String wind_speed;

    }
}
