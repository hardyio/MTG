package com.yio.trade.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.FragmentScope;

import javax.inject.Inject;

import io.reactivex.Observable;
import com.yio.trade.api.WanAndroidService;
import com.yio.trade.model.ArticleInfo;
import com.yio.trade.mvp.contract.CollectionContract;
import com.yio.trade.result.WanAndroidResponse;

@FragmentScope
public class CollectionModel extends BaseModel implements CollectionContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;
    @Inject
    WanAndroidService wanAndroidService;

    @Inject
    public CollectionModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<WanAndroidResponse<ArticleInfo>> getCollection(int page) {
        return wanAndroidService.colArticles(page);
    }

    @Override
    public Observable<WanAndroidResponse> unCollect(int id, int originId) {
        return wanAndroidService.unCollectMine(id, originId);
    }

    @Override
    public Observable<WanAndroidResponse> collect(int id, int originId) {
        return wanAndroidService.collectInside(id);
    }
}