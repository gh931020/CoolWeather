package com.zzj.coolweather.gson;

/**
 * Created by yinxq on 2018/1/18 0018.
 * suggestion: {
 air: {
 brf: "良",
 txt: "气象条件有利于空气污染物稀释、扩散和清除，可在室外正常活动。"
 },
 comf: {
 brf: "较舒适",
 txt: "白天天气晴好，早晚会感觉偏凉，午后舒适、宜人。"
 }
 flu: {
 brf: "较易发",
 txt: "昼夜温差较大，较易发生感冒，请适当增减衣服。体质较弱的朋友请注意防护。"
 },
 sport: {
 brf: "较适宜",
 txt: "天气较好，但考虑风力较强且气温较低，推荐您进行室内运动，若在户外运动请注意防风并适当增减衣物。"
 },
 trav: {
 brf: "适宜",
 txt: "天气较好，温度适宜，但风稍微有点大。这样的天气适宜旅游，您可以尽情地享受大自然的无限风光。"
 }
 }
 */

public class Suggestion {
    /**
     * 空气质量
     */
    public Air air;
    /**
     * 舒适度
     */
    public Comf comf;
    /**
     * 感冒指数
     */
    public Flu flu;
    /**
     * 运动指数
     */
    public Sport sport;
    /**
     * 旅游指数
     */
    public Trav trav;

    public class Air {
        public String brf;
        public String txt;
    }

    public class Comf {
        public String brf;
        public String txt;
    }

    public class Flu {
        public String brf;
        public String txt;
    }

    public class Sport {
        public String brf;
        public String txt;
    }

    public class Trav {
        public String brf;
        public String txt;
    }
}
