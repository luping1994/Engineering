package net.suntrans.engineering.bean;

import java.util.List;

/**
 * Created by Looney on 2018/3/5.
 * Des:
 */

public class ProConfig {


    /**
     * code : 1
     * msg : 请求成功
     * data : [{"id":"1","name":"广州三川培训测试项目","status":"1","sub":[{"id":"1","host":"183.236.25.190","port":"9101","proj_id":"1","dev_type":"4100","dev_code":"ST-SLC-10"},{"id":"2","host":"183.236.25.190","port":"9102","proj_id":"1","dev_type":"4300","dev_code":"ST-SLC-6"},{"id":"3","host":"183.236.25.190","port":"9109","proj_id":"1","dev_type":"4301","dev_code":"ST-SLC-2-3P"}]}]
     */

    public int code;
    public String msg;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * id : 1
         * name : 广州三川培训测试项目
         * status : 1
         * sub : [{"id":"1","host":"183.236.25.190","port":"9101","proj_id":"1","dev_type":"4100","dev_code":"ST-SLC-10"},{"id":"2","host":"183.236.25.190","port":"9102","proj_id":"1","dev_type":"4300","dev_code":"ST-SLC-6"},{"id":"3","host":"183.236.25.190","port":"9109","proj_id":"1","dev_type":"4301","dev_code":"ST-SLC-2-3P"}]
         */

        public String id;
        public String name;
        public String status;
        public List<SubBean> sub;

        public static class SubBean {
            /**
             * id : 1
             * host : 183.236.25.190
             * port : 9101
             * proj_id : 1
             * dev_type : 4100
             * dev_code : ST-SLC-10
             */

            public String id;
            public String host;
            public int port;
            public String proj_id;
            public String dev_type;
            public String dev_code;
        }
    }
}
