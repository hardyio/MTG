package com.yio.trade.di.module;

import com.yio.trade.mvp.contract.TabContract;

import dagger.Binds;
import dagger.Module;

import com.yio.trade.mvp.model.TabModel;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 11/18/2019 10:05
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Module
public abstract class TabModule {

    @Binds
    abstract TabContract.Model bindTreeTabModel(TabModel model);
}