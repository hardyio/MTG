package com.yio.trade.mvp.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;
import com.yio.trade.model.Tab;
import com.yio.trade.mvp.contract.WeixinContract;

import java.util.List;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import com.yio.trade.base.BaseWanObserver;
import com.yio.trade.result.WanAndroidResponse;
import com.yio.trade.utils.rx.RxScheduler;

@FragmentScope
public class WeixinPresenter extends BasePresenter<WeixinContract.Model, WeixinContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public WeixinPresenter(WeixinContract.Model model, WeixinContract.View rootView) {
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

    public void requestWxTab() {
        mModel.getWxTabs()
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .compose(RxScheduler.Obs_io_main())
              .subscribe(new BaseWanObserver<WanAndroidResponse<List<Tab>>>(mRootView) {

                  @Override
                  protected void onStart() {
                      mRootView.showLoading();
                  }

                  @Override
                  public void onSuccess(WanAndroidResponse<List<Tab>> response) {
                      mRootView.showData(response.getData());
                  }

              });
//        List<Tab> tabs = new ArrayList<>();
//        tabs.add(new Tab(10, 1, "name", 100, 10, false, 1, null));
//        mRootView.showData(tabs);
    }
}
