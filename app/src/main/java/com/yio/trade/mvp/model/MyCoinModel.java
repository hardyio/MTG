package com.yio.trade.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.FragmentScope;
import com.yio.trade.api.WanAndroidService;
import com.yio.trade.model.Coin;
import com.yio.trade.model.CoinHistory;
import com.yio.trade.model.PageInfo;
import com.yio.trade.mvp.contract.MyCoinContract;
import com.yio.trade.result.WanAndroidResponse;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 12/16/2019 17:08
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@FragmentScope
public class MyCoinModel extends BaseModel implements MyCoinContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;
    @Inject
    WanAndroidService wanAndroidService;

    @Inject
    public MyCoinModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<WanAndroidResponse<PageInfo<CoinHistory>>> getMyCoin(int page) {
        return wanAndroidService.coinHistory(page);
    }

    @Override
    public Observable<WanAndroidResponse<PageInfo<Coin>>> getRank(int page) {
        return wanAndroidService.allRank(page);
    }

    @Override
    public Observable<WanAndroidResponse<Coin>> personalCoin() {
        return wanAndroidService.personalCoin();
    }
}