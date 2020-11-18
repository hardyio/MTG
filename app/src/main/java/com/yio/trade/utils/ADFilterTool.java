package com.yio.trade.utils;

import android.content.Context;
import android.content.res.Resources;

import com.yio.mtg.trade.R;


/**
 * 广告过滤工具类
 */
public class ADFilterTool {

    public static boolean hasAd(Context context, String url) {
        Resources res = context.getResources();
        String[] adUrls = res.getStringArray(R.array.adBlockUrl);
        for (String adUrl : adUrls) {
            if (url.contains(adUrl)) {
                return true;
            }
        }
        return false;
    }
}
