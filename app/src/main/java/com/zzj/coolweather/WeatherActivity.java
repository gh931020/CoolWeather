package com.zzj.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zzj.coolweather.gson.Forecast;
import com.zzj.coolweather.gson.Weather;
import com.zzj.coolweather.util.HttpUtil;
import com.zzj.coolweather.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private TextView wt_titile_city;
    private TextView wt_update_time;
    private TextView wn_temperature;
    private TextView wn_weather_info;
    private LinearLayout wf_forecast;

    private TextView wa_aqi;
    private TextView wa_qlty;
    private TextView wa_pm25;
    private TextView ws_air;
    private TextView ws_comf;
    private TextView ws_flu;
    private TextView ws_sport;
    private TextView ws_trav;

    private ScrollView weatherLayout;
    private ImageView iv_background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏为透明状态
        if (Build.VERSION.SDK_INT >=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);
        initView();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        //从preference中获取保存的数据
        String bingPic = sp.getString("bing_pic",null);
        if (bingPic != null){
            //如果有保存图片链接，直接加载
            Glide.with(this).load(bingPic).into(iv_background);
        }else{
            //否则从网络获取
            loadBingPic();
        }


        String weatherContent = sp.getString("weather",null);
        //如果没有缓存数据，就从服务器中获取
        if (weatherContent != null){
            Weather weather = Utility.handleWeatherResponse(weatherContent);
            //处理并显示天气数据
            showWeatherInfo(weather);
        }else{
            //无缓存时从服务器中获取
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }

    private void loadBingPic() {
        String bingpicUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(bingpicUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取图片链接
                final String bingpic = response.body().string();
                //将图片链接保存到本地
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingpic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //加载图片
                        Glide.with(WeatherActivity.this).load(bingpic).into(iv_background);
                    }
                });
            }
        });
    }

    /**
     * 根据天气ID请求天气信息
     * @param weatherId
     */
    private void requestWeather(String weatherId) {
        String url = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=fe4eb696eb1142c3a1473f742946a593";
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //信息获取失败
                        Toast.makeText(WeatherActivity.this,"获取信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(result);
                //回到主线程处理获取到的信息
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            //如果获取到的天气不为空，且返回结果为ok，保存到本地
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",result);
                            editor.apply();
                            //显示天气信息
                            showWeatherInfo(weather);
                        }else{
                            //信息获取失败
                            Toast.makeText(WeatherActivity.this,"获取信息失败",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
        //更新背景图片
        loadBingPic();
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        //2018-01-18 07:58
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String temperature = weather.now.temperature;
        String tempInfo = weather.now.tempInfo.tempInfo;
        wt_titile_city.setText(cityName);
        wt_update_time.setText(updateTime);
        wn_temperature.setText(temperature+"℃");
        wn_weather_info.setText(tempInfo);
        //预报
        wf_forecast.removeAllViews();
        List<Forecast> forecasts = weather.forecasts;
        for (Forecast forecast:forecasts) {
            View view = LayoutInflater.from(this).inflate(R.layout.weather_forecast_item,wf_forecast,false);
            TextView wfi_date = view.findViewById(R.id.wfi_date);
            wfi_date.setText(forecast.date);
            TextView wfi_info = view.findViewById(R.id.wfi_info);
            wfi_info.setText(forecast.cond.txt_d);
            TextView wfi_max = view.findViewById(R.id.wfi_max);
            wfi_max.setText(forecast.temperature.max+"℃");
            TextView wfi_min = view.findViewById(R.id.wfi_min);
            wfi_min.setText(forecast.temperature.min+"℃");
            wf_forecast.addView(view);
        }
        if (weather.aqi != null){
            wa_aqi.setText(weather.aqi.aqiCity.aqi);
            wa_qlty.setText(weather.aqi.aqiCity.qlty);
            wa_pm25.setText(weather.aqi.aqiCity.pm25);
        }
        String air = "空气质量："+weather.suggestion.air.brf+"。"+weather.suggestion.air.txt;
        String comf = "舒适度："+weather.suggestion.comf.brf+"。"+weather.suggestion.comf.txt;
        String flu = "健康指数："+weather.suggestion.flu.brf+"。"+weather.suggestion.flu.txt;
        String sport = "运动建议："+weather.suggestion.sport.brf+"。"+weather.suggestion.sport.txt;
        String trav = "出行建议："+weather.suggestion.trav.brf+"。"+weather.suggestion.trav.txt;
        ws_air.setText(air);
        ws_comf.setText(comf);
        ws_flu.setText(flu);
        ws_sport.setText(sport);
        ws_trav.setText(trav);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    private void initView() {
        weatherLayout = findViewById(R.id.aw_sw_weather);
        iv_background = findViewById(R.id.aw_iv_background);

        wt_titile_city = findViewById(R.id.wt_title_city);
        wt_update_time = findViewById(R.id.wt_update_time);
        wn_temperature = findViewById(R.id.wn_temperature);
        wn_weather_info = findViewById(R.id.wn_weather_info);
        wf_forecast = findViewById(R.id.wf_forecast);
        wa_aqi = findViewById(R.id.wa_aqi);
        wa_qlty = findViewById(R.id.wa_qlty);
        wa_pm25 = findViewById(R.id.wa_pm25);
        ws_air = findViewById(R.id.ws_air);
        ws_comf = findViewById(R.id.ws_comf);
        ws_flu = findViewById(R.id.ws_flu);
        ws_sport = findViewById(R.id.ws_sport);
        ws_trav = findViewById(R.id.ws_trav);
    }
}
