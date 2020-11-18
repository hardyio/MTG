package com.yio.trade.di.module;

import com.yio.trade.mvp.contract.SearchResultContract;

import dagger.Binds;
import dagger.Module;

import com.yio.trade.mvp.model.SearchResultModel;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 12/09/2019 16:34
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Module
public abstract class SearchResultModule {

    @Binds
    abstract SearchResultContract.Model bindSearchResultModel(SearchResultModel model);
}