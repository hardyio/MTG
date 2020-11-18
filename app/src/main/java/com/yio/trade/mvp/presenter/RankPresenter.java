package com.yio.trade.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.yio.trade.model.Coin;
import com.yio.trade.model.PageInfo;
import com.yio.trade.mvp.contract.RankContract;

import java.util.List;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import com.yio.trade.base.BaseWanObserver;
import com.yio.trade.result.WanAndroidResponse;
import com.yio.trade.utils.rx.RxScheduler;

@FragmentScope
public class RankPresenter extends BasePresenter<RankContract.Model, RankContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public RankPresenter(RankContract.Model model, RankContract.View rootView) {
        super(model, rootView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRootView.killMyself();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

    public void loadRank(int page) {
        mModel.getRank(page)
              .compose(RxScheduler.Obs_io_main())
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .subscribe(new BaseWanObserver<WanAndroidResponse<PageInfo<Coin>>>(mRootView) {

                  @Override
                  protected void onStart() {
                      if (page == 1) {
                          mRootView.showLoading();
                      }
                  }

                  @Override
                  public void onSuccess(WanAndroidResponse<PageInfo<Coin>> response) {
                      PageInfo<Coin> info = response.getData();
                      List<Coin> coinList = info.getDatas();
                      for (int i = 0; i < coinList.size(); i++) {
                          Coin coin = coinList.get(i);
                          int realRank = (page - 1) * info.getSize() + i + 1;
                          coin.setRealRank(String.valueOf(realRank));
                      }
                      mRootView.showData(info);
                  }
              });
    }

    public void loadMyCoin() {
        mModel.personalCoin()
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .compose(RxScheduler.Obs_io_main())
              .subscribe(new BaseWanObserver<WanAndroidResponse<Coin>>(mRootView) {
                  @Override
                  public void onSuccess(WanAndroidResponse<Coin> response) {
                      mRootView.showCoin(response.getData());
                  }
              });
    }

}
