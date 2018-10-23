package com.experience.moon.play.params.response;

public class FirstResDto{

    /**
     * data : {"url":"https://t.dijiadijia.com/active/activation.html"}
     * code : 102
     * msg : 请先激活
     */

    public DataBean data;
    public int code;
    public String msg;

    public static class DataBean {
        /**
         * url : https://t.dijiadijia.com/active/activation.html
         */
        public String url;
        public String uid;
    }
}
