package com.yio.trade.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.FragmentScope;
import com.yio.trade.api.WanAndroidService;
import com.yio.trade.http.NetWorkManager;
import com.yio.trade.model.ArticleInfo;
import com.yio.trade.mvp.contract.SearchResultContract;
import com.yio.trade.result.WanAndroidResponse;

import javax.inject.Inject;

import io.reactivex.Observable;

@FragmentScope
public class SearchResultModel extends BaseModel implements SearchResultContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;
    WanAndroidService wanAndroidService;

    @Inject
    public SearchResultModel(IRepositoryManager repositoryManager) {
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
    public Observable<WanAndroidResponse<ArticleInfo>> search(int page, String key) {
        return wanAndroidService.search(page, key);
    }

    @Override
    public Observable<WanAndroidResponse> collect(int id) {
        return wanAndroidService.collectInside(id);
    }

    @Override
    public Observable<WanAndroidResponse> unCollect(int id) {
        return wanAndroidService.unCollect(id);
    }
}