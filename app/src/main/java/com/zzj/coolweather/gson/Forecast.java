package com.zzj.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yinxq on 2018/1/18 0018.
 */

public class Forecast {
    @SerializedName("cond")
    public Cond cond;
    public String date;
    @SerializedName("tmp")
    public Temperature temperature;
    @SerializedName("wind")
    public Now.WindInfo windInfo;

    public class Temperature {
        public String max;
        public String min;
    }

    public class Cond {
        public String code_d;
        public String txt_d;
    }
}
/**
 * daily_forecast: [
 {
  cond: {
 code: "101",
 txt_d: "多云"
 },
 date: "2018-01-18",
 tmp: {
 max: "11",
 min: "4"
 },
 wind: {
 dir: "东北风",
 sc: "3-4",
 spd: "14"
 }
 },
 {
 astro: {
 mr: "08:21",
 ms: "19:25",
 sr: "07:04",
 ss: "17:28"
 },
 cond: {
 code_d: "101",
 code_n: "101",
 txt_d: "多云",
 txt_n: "多云"
 },
 date: "2018-01-19",
 hum: "70",
 pcpn: "0.0",
 pop: "0",
 pres: "1025",
 tmp: {
 max: "12",
 min: "4"
 },
 uv: "3",
 vis: "20",
 wind: {
 deg: "82",
 dir: "东风",
 sc: "微风",
 spd: "8"
 }
 },
 {
 astro: {
 mr: "08:58",
 ms: "20:20",
 sr: "07:04",
 ss: "17:29"
 },
 cond: {
 code_d: "104",
 code_n: "305",
 txt_d: "阴",
 txt_n: "小雨"
 },
 date: "2018-01-20",
 hum: "70",
 pcpn: "0.7",
 pop: "74",
 pres: "1023",
 tmp: {
 max: "14",
 min: "5"
 },
 uv: "3",
 vis: "19",
 wind: {
 deg: "79",
 dir: "东风",
 sc: "微风",
 spd: "7"
 }
 }
 ]
 */
