package com.yio.trade.mvp.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;
import com.yio.trade.mvp.contract.WebContract;

import io.reactivex.Observable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import com.yio.trade.base.BaseWanObserver;
import com.yio.trade.http.RetryWithDelay;
import com.yio.trade.model.Article;
import com.yio.trade.result.WanAndroidResponse;
import com.yio.trade.utils.rx.RxScheduler;

@ActivityScope
public class WebPresenter extends BasePresenter<WebContract.Model, WebContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public WebPresenter(WebContract.Model model, WebContract.View rootView) {
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

     /**
     * 收藏或取消收藏文章
     */
    public void collectArticle(Article article) {
        Observable<WanAndroidResponse> observable;
        boolean isCollect = article.isCollect();
        if (!isCollect) {
            observable = mModel.collect(article.getId());
        }
        else {
            observable = mModel.unCollect(article.getId());
        }
        observable.compose(RxScheduler.Obs_io_main())
                  .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                  .retryWhen(new RetryWithDelay(1000L))
                  .subscribe(new BaseWanObserver<WanAndroidResponse>(mRootView) {
                      @Override
                      public void onSuccess(WanAndroidResponse wanAndroidResponse) {
                          mRootView.updateCollectStatus(!isCollect, article);
                      }
                  });
    }
}
