package net.suntrans.engineering.api;



import net.suntrans.engineering.App;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Looney on 2016/12/15.
 */

public class RetrofitHelper {

    private static Api api;
    //public static final String BASE_URL = "http://www.suntrans.net:8956";
//    public static final String BASE_URL = "http://appone.suntrans.net/api.php/";
    public static final String BASE_URL = "http://hm.suntrans-cloud.com/appone/api.php/";
    public static final String BASE_URL2 = "http://hm.suntrans-cloud.com/appone/api.php/";

    private static OkHttpClient mOkHttpClient;

    static {
        initOkHttpClient();
    }

    public static Api getLoginApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(Api.class);
    }

    public static Api getApi() {
        if (api == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(mOkHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())

//                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            api = retrofit.create(Api.class);
        }
        return api;
    }

    public static Api getApi2() {

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build().create(Api.class);
    }


    private static void initOkHttpClient() {


        Interceptor netInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String header = App.getSharedPreferences().getString("access_token", "-1");
                header = "Bearer " + header;
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Authorization", header)
                        .method(original.method(), original.body())
                        .build();
                Response response = chain.proceed(request);
                return response;
            }
        };


        if (mOkHttpClient == null) {
            synchronized (RetrofitHelper.class) {
                if (mOkHttpClient == null) {
                    mOkHttpClient = new OkHttpClient.Builder()
                            .addInterceptor(netInterceptor)
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
    }
}
