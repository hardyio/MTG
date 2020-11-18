package com.yio.trade.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.FragmentScope;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import com.yio.trade.api.WanAndroidService;
import com.yio.trade.model.Tab;
import com.yio.trade.mvp.contract.WeixinContract;
import com.yio.trade.result.WanAndroidResponse;

@FragmentScope
public class WeixinModel extends BaseModel implements WeixinContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;
    WanAndroidService wanAndroidService;

    @Inject
    public WeixinModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
        wanAndroidService = mRepositoryManager.obtainRetrofitService(WanAndroidService.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<WanAndroidResponse<List<Tab>>> getWxTabs() {
        return wanAndroidService.wxList();
    }
}