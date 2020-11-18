package com.yio.trade.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.FragmentScope;
import com.yio.trade.api.WanAndroidService;
import com.yio.trade.http.NetWorkManager;
import com.yio.trade.model.Coin;
import com.yio.trade.model.PageInfo;
import com.yio.trade.mvp.contract.RankContract;
import com.yio.trade.result.WanAndroidResponse;

import javax.inject.Inject;

import io.reactivex.Observable;

@FragmentScope
public class RankModel extends BaseModel implements RankContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;
    WanAndroidService wanAndroidService;

    @Inject
    public RankModel(IRepositoryManager repositoryManager) {
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
    public Observable<WanAndroidResponse<PageInfo<Coin>>> getRank(int page) {
        return wanAndroidService.allRank(page);
    }

    @Override
    public Observable<WanAndroidResponse<Coin>> personalCoin() {
        return wanAndroidService.personalCoin();
    }
}