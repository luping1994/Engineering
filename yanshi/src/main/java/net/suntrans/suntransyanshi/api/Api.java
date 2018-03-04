package net.suntrans.suntransyanshi.api;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Looney on 2017/1/4.
 */
public interface Api {

    /**
     * 获取用户信息api
     *
     * @return
     */
    @GET("/advanced.htm")
    Call<okhttp3.ResponseBody> getEwmSetting();

    /**
     * 设置用户信息api
     *
     * @returns
     */

    @POST("/advanced.htm")
    Call<okhttp3.ResponseBody> etEwmSetting(@FieldMap Map<String,String> map);



    /**
     * 设置用户信息api
     *
     * @returns
     */
    @FormUrlEncoded
    @POST("/basic.htm")
    Call<okhttp3.ResponseBody> etBaseEwmSetting(@Field("SSID") String SSID, @Field("pass") String pass, @Field("CLICK") String save);

}
