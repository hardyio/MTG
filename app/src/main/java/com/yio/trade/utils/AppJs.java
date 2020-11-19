package com.yio.trade.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.SPUtils;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yio.trade.mvp.ui.activity.PureWebActivity;
import com.yio.trade.mvp.ui.activity.WebActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;

import io.branch.referral.util.BranchEvent;

public class AppJs {
    private Context context;
    private final Method[] classMethods;

    public AppJs(Context context) {
        this.context = context;
        MethodUtil methodUtil = new MethodUtil();
        classMethods = methodUtil.getClassMethods(AppJs.class);
    }

    /**
     * 获取设备id
     * 必须保证有值
     * 获取不到的时候生成一个UUID
     */
    @JavascriptInterface
    @NonNull
    public String getDeviceId() {
        return DeviceIdUtil.getDeviceId(context);
    }

    /**
     * 获取个推设备id
     * 传空串就行
     */
    @JavascriptInterface
    public String takePushId() {
        //现在只需要空实现
        return "";
    }

    /**
     * 获取fcm 令牌
     */
    @JavascriptInterface
    public String takeFCMPushId() {
        //fcm生成的注册令牌
        return SPUtils.getInstance().getString("fcmToken");
    }

    /**
     * 获取渠道
     */
    @JavascriptInterface
    public String takeChannel() {
        return "google";
    }

    /**
     * 获取ANDROID_ID
     * public static final String ANDROID_ID
     */
    @JavascriptInterface
    public String getGoogleId() {
        return DeviceIdUtil.getAndroidId(context);
    }

    /**
     * 集成branch包的时候已经带有Google Play Service核心jar包
     * 获取gpsadid 谷歌广告id
     * AdvertisingIdClient.getAdvertisingIdInfo() 异步方法
     */
    @JavascriptInterface
    public String getGaId() {
        try {
            AdvertisingIdClient.Info advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
            return advertisingIdInfo.getId();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 调取谷歌登录方法
     *
     * @param data {"sign":"","host":"https://bb.skr.today"}
     */
    @JavascriptInterface
    public void openGoogle(String data) {
        //TODO
    }

    /**
     * branch事件统计
     *
     * @param eventName 统计事件名称
     */
    @JavascriptInterface
    public void branchEvent(String eventName) {
        new BranchEvent(eventName).logEvent(context);
    }

    /**
     * branch事件统计
     *
     * @param eventName  统计时间名称
     * @param parameters 自定义统计参数
     */
    @JavascriptInterface
    public void branchEvent(String eventName, String parameters) {
        try {
            BranchEvent branchEvent = new BranchEvent(eventName);
            JSONObject obj = null;
            obj = new JSONObject(parameters);
            Bundle bundle = new Bundle();
            Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = obj.optString(key);
                bundle.putString(key, value);
                branchEvent.addCustomDataProperty(key, value);
            }
            branchEvent.logEvent(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * branch事件统计
     *
     * @param eventName  统计事件名称
     * @param parameters 自定义统计参数
     * @param alias      事件别名
     */
    @JavascriptInterface
    public void branchEvent(String eventName, String parameters, String alias) {
        try {
            BranchEvent branchEvent = new BranchEvent(eventName);
            JSONObject obj = null;
            obj = new JSONObject(parameters);
            Bundle bundle = new Bundle();
            Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = obj.optString(key);
                bundle.putString(key, value);
                branchEvent.addCustomDataProperty(key, value);
            }
            branchEvent.setCustomerEventAlias(alias).logEvent(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * facebook事件统计
     *
     * @param eventName  事件名称
     * @param valueToSum 计数数值
     * @param parameters 自定义统计参数json{}需要全是String类型
     */
    @JavascriptInterface
    public void facebookEvent(String eventName, Double valueToSum, String parameters) {
        JSONObject obj = null;
        try {
            AppEventsLogger logger = AppEventsLogger.newLogger(context);
            obj = new JSONObject(parameters);
            Bundle bundle = new Bundle();
            Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = obj.optString(key);
                bundle.putString(key, value);
            }
            logger.logEvent(eventName, valueToSum, bundle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * facebook事件统计
     *
     * @param eventName  事件名称
     * @param parameters 自定义统计参数json{}需要全是String类型
     */
    @JavascriptInterface
    public void facebookEvent(String eventName, String parameters) {
        JSONObject obj = null;
        try {
            AppEventsLogger logger = AppEventsLogger.newLogger(context);
            obj = new JSONObject(parameters);
            Bundle bundle = new Bundle();
            Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = obj.optString(key);
                bundle.putString(key, value);
            }
            logger.logEvent(eventName, bundle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * facebook计数统计
     *
     * @param eventName  事件名称
     * @param valueToSum 计数数值
     */
    @JavascriptInterface
    public void facebookEvent(String eventName, Double valueToSum) {
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        logger.logEvent(eventName, valueToSum);
    }

    /**
     * facebook 计数事件统计
     *
     * @param eventName 事件名称
     */
    @JavascriptInterface
    public void facebookEvent(String eventName) {
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        logger.logEvent(eventName);
    }

    /**
     * firebase事件统计
     */
    @JavascriptInterface
    public void firebaseEvent(String category, String parameters) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(parameters);
            Bundle bundle = new Bundle();
            Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = obj.optString(key);
                bundle.putString(key, value);
            }
            FirebaseAnalytics.getInstance(context).logEvent(category, bundle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 头像获取
     *
     * @param callbackMethod 回传图片时调用H5的方法名
     */
    @JavascriptInterface
    public void takePortraitPicture(String callbackMethod) {
        //TODO
        //参考实现：成员变量记录下js方法名，图片转成base64字符串后调用该js方法传递给H5
        if (!TextUtils.isEmpty(callbackMethod)) {
            StringBuilder builder = new StringBuilder(callbackMethod).append("(");
            builder.append("'").append("data:image/png;base64,").append("").append("'");
            builder.append(")");
            String method = builder.toString();
            String javascript = "javascript:" + method;
            WebActivity webActivity = (WebActivity) this.context;
            WebView webView = webActivity.getWebView();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                webView.loadUrl(javascript);
            } else {
                webView.evaluateJavascript(javascript, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //此处为 js 返回的结果
                    }
                });
            }
        }
    }

    /**
     * AppJs是否存在交互方法 告诉H5是否存在传入的对应方法
     *
     * @param callbackMethod 回调给H5时调用的JavaScript方法
     * @param name 需要查询AppJs中是否存在的方法
     */
    @JavascriptInterface
    public void isContainsName(String callbackMethod, String name) {
        boolean has = false;
        int length = classMethods.length;
        for (int i = 0; i < length; i++) {
            String methodName = classMethods[i].getName();
            if (name.equals(methodName)) {
                has = true;
                break;
            }
        }
        WebActivity webActivity = (WebActivity) this.context;
        boolean finalHas = has;
        webActivity.runOnUiThread(() -> {
            WebView webView = webActivity.getWebView();
            String javascript = "javascript:" + callbackMethod + "(" + finalHas + ")";
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                webView.loadUrl(javascript);
            } else {
                webView.evaluateJavascript(javascript, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //此处为 js 返回的结果
                    }
                });
            }
        });
    }

    /**
     * 是否禁用系统返回键
     * 1 禁止
     */
    @JavascriptInterface
    public void shouldForbidSysBackPress(int forbid) {
        //WebActivity成员变量记录下是否禁止
        ((WebActivity) context).setShouldForbidBackPress(forbid);
        //WebActivity 重写onBackPressed方法 变量为1时禁止返回操作
    }

    /**
     * 返回键调用h5控制
     *
     * @param forbid     是否禁止返回键 1 禁止
     * @param methodName 反回时调用的h5方法 例如:detailBack() 不需要时传空串只禁止返回
     */
    @JavascriptInterface
    public void forbidBackForJS(int forbid, String methodName) {
        ((WebActivity) context).setShouldForbidBackPress(forbid);
        //同上
        ((WebActivity) context).setBackPressJSMethod(methodName);
        //WebActivity成员变量记录下js方法名 在禁止返回时调用js方法
    }

    /**
     * 使用手机里面的浏览器打开 url
     *
     * @param url 打开 url
     */
    @JavascriptInterface
    public void openBrowser(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(uri);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    /**
     * 打开一个基本配置的webview （不修改UA、不设置AppJs、可以缓存）
     * 打开新页面
     * 加载webview的情况分类(判断依据：url、postData、html)
     * |-------1、只有url：webView.loadUrl()
     * |-------2、有url和postData：webView.postUrl()
     * |-------3、有html webView.loadDataWithBaseURL()
     *
     * @param json 打开web传参
     *             {"title":"", 标题
     *             "url":"", 加载的地址
     *             "hasTitleBar":false, 是否显示标题栏
     *             "rewriteTitle":true, 是否通过加载的Web重写标题
     *             "stateBarTextColor":"black", 状态栏字体颜色 black|white
     *             "titleTextColor":"#FFFFFF", 标题字体颜色
     *             "titleColor":"#FFFFFF", 状态栏和标题背景色
     *             "postData":"", webView post方法时会用到
     *             "html":"", 加载htmlCode（例如：<body></body>）,
     *             "webBack":true, true:web回退(点击返回键webview可以回退就回退，无法回退的时候关闭该页面)|false(点击返回键关闭该页面) 直接关闭页面
     *             }
     */
    @JavascriptInterface
    public void openPureBrowser(String json) {
        Intent intent = new Intent(context, PureWebActivity.class);
        intent.putExtra("json", json);
        context.startActivity(intent);
    }

    /**
     * 控制显示当前页面是否显示 TitleBar
     * （点击返回键webview 后退）
     *
     * @param visible
     */
    @JavascriptInterface
    public void showTitleBar(boolean visible) {
        WebActivity webActivity = (WebActivity) this.context;
        webActivity.showTitleBar(visible);
    }

}
