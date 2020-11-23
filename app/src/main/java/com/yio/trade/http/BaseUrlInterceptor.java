package com.yio.trade.http;

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BaseUrlInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        // 获取request
        Request request = chain.request();
        // 从request中获取原有的HttpUrl实例oldHttpUrl
        HttpUrl oldHttpUrl = request.url();
        String url = oldHttpUrl.queryParameter("url");
        if (!TextUtils.isEmpty(url)) {
            if (!url.endsWith("/")) {
                url = url + "/";
            }
            // 获取request的创建者builder
            Request.Builder builder = request.newBuilder();
            HttpUrl newFullUrl = oldHttpUrl
                    .newBuilder()
                    // 更换网络协议
                    .scheme("https")
                    // 更换主机名
                    .host("c1.mufg365.com")
                    // 更换端口
                    .port(80)
                    .build();
            return chain.proceed(builder.url(newFullUrl).build());
        }
        return chain.proceed(request);
    }
}