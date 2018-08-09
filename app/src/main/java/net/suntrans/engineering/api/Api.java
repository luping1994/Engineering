package net.suntrans.engineering.api;

import net.suntrans.engineering.bean.LoginEntity;
import net.suntrans.engineering.bean.ProConfig;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Looney on 2017/1/4.
 */

public interface Api {

    /**
     * 登录api
     *
     * @return
     */
    @POST("Conf/index")
    Observable<ProConfig> getProConfig();


    @FormUrlEncoded
    @POST("oauth/login")
    Observable<LoginEntity> login(@Field("grant_type") String grant_type,
                                  @Field("client_id") String client_id,
                                  @Field("client_secret") String client_secret,
                                  @Field("username") String username,
                                  @Field("password") String password);


}
