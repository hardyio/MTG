package com.yio.trade.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.FragmentScope;
import com.yio.trade.api.WanAndroidService;
import com.yio.trade.http.NetWorkManager;
import com.yio.trade.model.Coin;
import com.yio.trade.mvp.contract.ContainerContract;
import com.yio.trade.result.WanAndroidResponse;

import javax.inject.Inject;

import io.reactivex.Observable;

@FragmentScope
public class ContainerModel extends BaseModel implements ContainerContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;
    @Inject
    WanAndroidService wanAndroidService;

    @Inject
    public ContainerModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<WanAndroidResponse<Coin>> personalCoin() {
        return NetWorkManager.getInstance().getWanAndroidService(mRepositoryManager).personalCoin();
    }

    @Override
    public Observable<WanAndroidResponse> logout() {
        return wanAndroidService.logout();
    }
}