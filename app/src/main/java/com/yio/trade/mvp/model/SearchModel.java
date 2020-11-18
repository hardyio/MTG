package com.yio.trade.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;
import com.yio.trade.api.WanAndroidService;
import com.yio.trade.http.NetWorkManager;
import com.yio.trade.model.HotKey;
import com.yio.trade.mvp.contract.SearchContract;
import com.yio.trade.result.WanAndroidResponse;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

@ActivityScope
public class SearchModel extends BaseModel implements SearchContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;
    WanAndroidService wanAndroidService;

    @Inject
    public SearchModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
        wanAndroidService = NetWorkManager.getInstance().getWanAndroidService(mRepositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<WanAndroidResponse<List<HotKey>>> getHotKeys() {
        return wanAndroidService.hotKeys();
    }
}