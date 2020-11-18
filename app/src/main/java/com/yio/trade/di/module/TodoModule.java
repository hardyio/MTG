package com.yio.trade.di.module;


import com.jess.arms.integration.IRepositoryManager;
import com.yio.trade.mvp.contract.TodoContract;
import com.yio.trade.mvp.model.TodoModel;

import dagger.Binds;
import dagger.Module;

import dagger.Provides;
import com.yio.trade.api.WanAndroidService;
import com.yio.trade.http.NetWorkManager;

@Module
public abstract class TodoModule {

    @Binds
    abstract TodoContract.Model bindTodoModel(TodoModel model);

    @Provides
    static WanAndroidService provideService(IRepositoryManager repositoryManager) {
        return NetWorkManager.getInstance().getWanAndroidService(repositoryManager);
    }
}