package com.yio.trade.mvp.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;
import com.yio.trade.mvp.contract.SearchResultContract;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import com.yio.trade.base.BaseWanObserver;
import com.yio.trade.common.CollectHelper;
import com.yio.trade.model.Article;
import com.yio.trade.model.ArticleInfo;
import com.yio.trade.result.WanAndroidResponse;
import com.yio.trade.utils.rx.RxScheduler;

@FragmentScope
public class SearchResultPresenter
        extends BasePresenter<SearchResultContract.Model, SearchResultContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public SearchResultPresenter(SearchResultContract.Model model,
                                 SearchResultContract.View rootView) {
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

    public void search(int page, String key, boolean refresh) {
        mModel.search(page, key)
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .compose(RxScheduler.Obs_io_main())
              .subscribe(new BaseWanObserver<WanAndroidResponse<ArticleInfo>>(mRootView) {

                  @Override
                  protected void onStart() {
                      if (refresh) {
                          mRootView.showLoading();
                      }
                  }

                  @Override
                  public void onSuccess(WanAndroidResponse<ArticleInfo> response) {
                      ArticleInfo data = response.getData();
                      if (data != null) {
                          mRootView.showData(data, refresh);
                      }
                      else {
                          mRootView.showEmpty();
                      }
                  }
              });
    }

    /**
     * 收藏或取消收藏文章
     */
    public void collectArticle(Article article, int position) {
        CollectHelper.with(mRootView).target(article).position(position).collect();
    }
}
