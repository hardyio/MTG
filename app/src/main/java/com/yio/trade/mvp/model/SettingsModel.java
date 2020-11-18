package com.yio.trade.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;
import com.yio.trade.mvp.contract.SettingsContract;

import javax.inject.Inject;

@ActivityScope
public class SettingsModel extends BaseModel implements SettingsContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public SettingsModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }
}