package com.yio.trade.di.module;

import com.yio.trade.mvp.contract.UserContract;

import dagger.Binds;
import dagger.Module;

import com.yio.trade.mvp.model.UserModel;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 01/28/2020 18:33
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Module
public abstract class UserModule {

    @Binds
    abstract UserContract.Model bindUserModel(UserModel model);
}