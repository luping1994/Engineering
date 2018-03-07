package net.suntrans.engineering.api;

import net.suntrans.engineering.bean.ProConfig;

import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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
    @GET("devConf/project")
    Observable<ProConfig> getProConfig();


}
