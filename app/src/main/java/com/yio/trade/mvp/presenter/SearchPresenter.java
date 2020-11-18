package com.yio.trade.mvp.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;
import com.yio.trade.model.HotKey;
import com.yio.trade.mvp.contract.SearchContract;

import java.util.List;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import com.yio.trade.base.BaseWanObserver;
import com.yio.trade.result.WanAndroidResponse;
import com.yio.trade.utils.rx.RxScheduler;

@ActivityScope
public class SearchPresenter extends BasePresenter<SearchContract.Model, SearchContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public SearchPresenter(SearchContract.Model model, SearchContract.View rootView) {
        super(model, rootView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

    public void loadHotKeys() {
        mModel.getHotKeys()
              .compose(RxScheduler.Obs_io_main())
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .subscribe(new BaseWanObserver<WanAndroidResponse<List<HotKey>>>(mRootView) {

                  @Override
                  protected void onStart() {
                      mRootView.showLoading();
                  }

                  @Override
                  public void onSuccess(WanAndroidResponse<List<HotKey>> response) {
                      List<HotKey> data = response.getData();
                      if (data != null && !data.isEmpty()) {
                          mRootView.showHotKeys(data);
                      } else {
                          mRootView.showEmpty();
                      }

                  }
              });

    }
}
