package com.yio.trade.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jess.arms.http.log.RequestInterceptor;
import com.jess.arms.integration.IRepositoryManager;
import com.yio.mtg.trade.BuildConfig;
import com.yio.trade.api.WanAndroidService;
import com.yio.trade.common.Const;
import com.yio.trade.common.JApplication;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 网络请求管理类
 */
public class NetWorkManager {

    private OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit;

    private static class NetWorkManagerHolder {
        private static final NetWorkManager INSTANCE = new NetWorkManager();
    }

    /*
    * 静态内部类单例
    */
    public static NetWorkManager getInstance() {
        return NetWorkManagerHolder.INSTANCE;
    }

    private NetWorkManager() {
        this.mOkHttpClient = getOkHttpClient();
        this.mRetrofit = new Retrofit.Builder()
                .baseUrl(Const.Url.WAN_ANDROID)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //使用rxjava
                .addConverterFactory(GsonConverterFactory.create()) //使用Gson
                .addConverterFactory(ScalarsConverterFactory.create()) //接收非json字符串
                .client(mOkHttpClient)
                .build();
    }

    public OkHttpClient getOkHttpClient() {
//        RequestInterceptor interceptor = new RequestInterceptor(message -> {
//            if (BuildConfig.DEBUG) {
////                try {
////                    String s = htmlReplace(message);
////                    String text = URLDecoder.decode(s, "utf-8");
////                    Timber.d("返回数据：%s", text);
////                } catch (UnsupportedEncodingException e) {
////                    e.printStackTrace();
////                   Timber.e(e);
////                }
//                LogUtils.debugLongInfo(htmlReplace(message));
//            }
//        });
        OkHttpClient.Builder builder = getOkHttpBuilder();
        // debug模式才打印
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(new RequestInterceptor());
        }
        mOkHttpClient = RetrofitUrlManager.getInstance().with(builder) //RetrofitUrlManager 初始化
                          .readTimeout(Const.HttpConst.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                          .writeTimeout(Const.HttpConst.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                          .connectTimeout(Const.HttpConst.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                          .addInterceptor(new AddCookiesInterceptor(JApplication.getInstance()))
                          .addInterceptor(new SaveCookiesInterceptor(JApplication.getInstance()))
                          //用于模拟弱网的拦截器
//                          .addNetworkInterceptor(new DoraemonWeakNetworkInterceptor())
                          //网络请求监控的拦截器
//                          .addInterceptor(new DoraemonInterceptor())
                          .build();
        return mOkHttpClient;
    }

    public OkHttpClient.Builder getOkHttpBuilder() {
        return RetrofitUrlManager.getInstance().with(new OkHttpClient.Builder());
    }

    public <T> T getApiService(Class<T> tClass) {
        Objects.requireNonNull(tClass, "api service class can not be null");
        return mRetrofit.create(tClass);
    }

    public WanAndroidService getWanAndroidService() {
        return mRetrofit.create(WanAndroidService.class);
    }

    public WanAndroidService getWanAndroidService(IRepositoryManager mRepositoryManager) {
        return mRepositoryManager.obtainRetrofitService(WanAndroidService.class);
    }

    public static boolean isNetWorkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)JApplication.getInstance()
                                                                       .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager != null ? manager.getActiveNetworkInfo() : null;
        return null != info && info.isAvailable();
    }

    /**
     *
     *(特殊字符替换)
     * @return String    返回类型
     * @author xsw
     */
    public static String htmlReplace(String str){
        str = str.replace("&ldquo;", "“");
        str = str.replace("&rdquo;", "”");
        str = str.replace("&nbsp;", " ");
        str = str.replace("&amp;", "&");
        str = str.replace("&#39;", "'");
        str = str.replace("&rsquo;", "’");
        str = str.replace("&mdash;", "—");
        str = str.replace("&ndash;", "–");
        return str;
    }
}
