package com.yio.trade.di.module;

import com.jess.arms.integration.IRepositoryManager;
import com.yio.trade.mvp.contract.TodoEditContract;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import com.yio.trade.api.WanAndroidService;
import com.yio.trade.http.NetWorkManager;
import com.yio.trade.mvp.model.TodoEditModel;

@Module
public abstract class TodoEditModule {

    @Binds
    abstract TodoEditContract.Model bindTodoEditModel(TodoEditModel model);

    @Provides
    static WanAndroidService provideService(IRepositoryManager repositoryManager) {
        return NetWorkManager.getInstance().getWanAndroidService(repositoryManager);
    }
}