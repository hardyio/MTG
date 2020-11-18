package com.yio.trade.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.FragmentScope;
import com.yio.trade.api.WanAndroidService;
import com.yio.trade.model.User;
import com.yio.trade.mvp.contract.SignupContract;
import com.yio.trade.result.WanAndroidResponse;

import javax.inject.Inject;

import io.reactivex.Observable;

@FragmentScope
public class SignupModel extends BaseModel implements SignupContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public SignupModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<WanAndroidResponse<User>> signUp(String username, String password,
                                                       String repassword) {
        return mRepositoryManager.obtainRetrofitService(WanAndroidService.class).register(username, password, repassword);
    }

    @Override
    public Observable<WanAndroidResponse<User>> login(String userName, String password) {
        return mRepositoryManager.obtainRetrofitService(WanAndroidService.class).login(userName, password);
    }
}