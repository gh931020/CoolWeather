package com.zzj.coolweather.util;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yinxq on 2018/1/17 0017.
 */

public class HttpUtil {

    public static void sendOkHttpRequest(String address, Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        //创建请求队列，添加回调监听用于数据返回
        client.newCall(request).enqueue(callback);
    }

}
