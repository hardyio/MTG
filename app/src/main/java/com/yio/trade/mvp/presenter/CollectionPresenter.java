package com.yio.trade.mvp.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;
import com.yio.trade.mvp.contract.CollectionContract;

import io.reactivex.Observable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import com.yio.trade.base.BaseWanObserver;
import com.yio.trade.model.Article;
import com.yio.trade.model.ArticleInfo;
import com.yio.trade.result.WanAndroidResponse;
import com.yio.trade.utils.rx.RxScheduler;

@FragmentScope
public class CollectionPresenter
        extends BasePresenter<CollectionContract.Model, CollectionContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public CollectionPresenter(CollectionContract.Model model, CollectionContract.View rootView) {
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

    public void loadCollection(int page) {
        mModel.getCollection(page)
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .compose(RxScheduler.Obs_io_main())
              .subscribe(new BaseWanObserver<WanAndroidResponse<ArticleInfo>>(mRootView) {
                  @Override
                  protected void onStart() {
                      if (page == 0) {
                          mRootView.showLoading();
                      }
                  }

                  @Override
                  public void onSuccess(WanAndroidResponse<ArticleInfo> response) {
                      mRootView.showData(response.getData());
                  }
              });
    }

    public void uncollectArticle(Article article, int position) {
        //        boolean isCollect = article.isCollect();
        int originId = article.getOriginId() == 0 ? -1 : article.getOriginId();
        Observable<WanAndroidResponse> observable = mModel.unCollect(article.getId(), originId);
        observable.compose(RxScheduler.Obs_io_main())
                  .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                  .subscribe(new BaseWanObserver<WanAndroidResponse>(mRootView) {
                      @Override
                      public void onSuccess(WanAndroidResponse response) {
                          mRootView.updateStatus(article, position);
                      }
                  });

    }
}
