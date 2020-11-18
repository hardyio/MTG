package com.yio.trade.di.module;

import com.jess.arms.integration.IRepositoryManager;
import com.yio.trade.mvp.contract.ShareContract;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import com.yio.trade.api.WanAndroidService;
import com.yio.trade.http.NetWorkManager;
import com.yio.trade.mvp.model.ShareModel;

@Module
public abstract class ShareModule {

    @Binds
    abstract ShareContract.Model bindShareModel(ShareModel model);

    @Provides
    static WanAndroidService provideService(IRepositoryManager repositoryManager) {
        return NetWorkManager.getInstance().getWanAndroidService(repositoryManager);
    }
}