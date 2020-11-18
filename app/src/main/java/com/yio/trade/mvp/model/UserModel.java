package com.yio.trade.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.FragmentScope;
import com.yio.trade.api.WanAndroidService;
import com.yio.trade.model.ShareUserArticles;
import com.yio.trade.mvp.contract.UserContract;
import com.yio.trade.result.WanAndroidResponse;

import javax.inject.Inject;

import io.reactivex.Observable;

@FragmentScope
public class UserModel extends BaseModel implements UserContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public UserModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<WanAndroidResponse<ShareUserArticles>> getUserArticles(long userId, int page) {
        return mRepositoryManager.obtainRetrofitService(WanAndroidService.class).shareUserArticles(userId, page);
    }
}