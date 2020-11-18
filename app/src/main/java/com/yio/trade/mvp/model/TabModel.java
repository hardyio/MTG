package com.yio.trade.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.yio.trade.api.WanAndroidService;
import com.yio.trade.model.ArticleInfo;
import com.yio.trade.mvp.contract.TabContract;
import com.yio.trade.result.WanAndroidResponse;

import javax.inject.Inject;

import io.reactivex.Observable;

@FragmentScope
public class TabModel extends BaseModel implements TabContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;
    private WanAndroidService wanAndroidService;

    @Inject
    public TabModel(IRepositoryManager repositoryManager) {
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
    public Observable<WanAndroidResponse<ArticleInfo>> getKnowledgeArticles(int childId, int page) {
        return wanAndroidService.treeArticles(page, childId);
    }

    @Override
    public Observable<WanAndroidResponse> collect(int id) {
        return wanAndroidService.collectInside(id);
    }

    @Override
    public Observable<WanAndroidResponse> unCollect(int id) {
        return wanAndroidService.unCollect(id);
    }

    @Override
    public Observable<WanAndroidResponse<ArticleInfo>> getWxArticles(int cid, int page) {
        return wanAndroidService.wxArticleList(cid, page);
    }

    @Override
    public Observable<WanAndroidResponse<ArticleInfo>> getProjectArticles(int page, int childId) {
        return wanAndroidService.projects(page, childId);
    }
}