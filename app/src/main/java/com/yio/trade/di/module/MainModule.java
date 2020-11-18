package com.yio.trade.di.module;

import com.yio.trade.mvp.contract.MainContract;

import dagger.Binds;
import dagger.Module;

import com.yio.trade.mvp.model.MainModel;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/12/2019 15:19
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Module
public abstract class MainModule {

    @Binds
    abstract MainContract.Model bindMainModel(MainModel model);
}