package com.yio.trade.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;
import com.yio.trade.mvp.contract.TabContract;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import com.yio.trade.base.BaseWanObserver;
import com.yio.trade.common.CollectHelper;
import com.yio.trade.common.Const;
import com.yio.trade.http.RetryWithDelay;
import com.yio.trade.model.Article;
import com.yio.trade.model.ArticleInfo;
import com.yio.trade.result.WanAndroidResponse;
import com.yio.trade.utils.rx.RxScheduler;

@FragmentScope
public class TabPresenter extends BasePresenter<TabContract.Model, TabContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public TabPresenter(TabContract.Model model, TabContract.View rootView) {
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

    public void requestArticles(int childId, int page, int fromType) {
        switch (fromType) {
            case Const.Type.TYPE_TAB_KNOWLEDGE:
                requestKnowledgeArticles(childId, page);
                break;
            case Const.Type.TYPE_TAB_WEIXIN:
                requestWeixinArticles(childId, page);
                break;
            case Const.Type.TYPE_TAB_PROJECT:
                requestProjectArticles(childId, page);
                break;
            default:
                mRootView.showMessage("未知类型");
                break;
        }
    }

    public void requestKnowledgeArticles(int childId, int page) {
        mModel.getKnowledgeArticles(childId, page)
              .compose(RxScheduler.Obs_io_main())
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .retryWhen(new RetryWithDelay(3000L))
              .subscribe(new BaseWanObserver<WanAndroidResponse<ArticleInfo>>(mRootView) {

                  @Override
                  public void onSuccess(WanAndroidResponse<ArticleInfo> response) {
                      ArticleInfo articleInfo = response.getData();
                      mRootView.showData(articleInfo);
                  }

                  @Override
                  public void onComplete() {
                      mRootView.hideLoading();
                  }
              });
    }


    private void requestProjectArticles(int childId, int page) {
        mModel.getProjectArticles(page, childId)
              .compose(RxScheduler.Obs_io_main())
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .retryWhen(new RetryWithDelay(3000L))
              .subscribe(new BaseWanObserver<WanAndroidResponse<ArticleInfo>>(mRootView) {

                  @Override
                  public void onSuccess(WanAndroidResponse<ArticleInfo> response) {
                      ArticleInfo articleInfo = response.getData();
                      mRootView.showData(articleInfo);
                  }

                  @Override
                  public void onComplete() {
                      mRootView.hideLoading();
                  }
              });
    }

    /**
     * 收藏或取消收藏文章
     */
    public void collectArticle(Article article, int position) {
        CollectHelper.with(mRootView).target(article).position(position).collect();
    }

    public void requestWeixinArticles(int cid, int page) {
        mModel.getWxArticles(cid, page)
              .compose(RxScheduler.Obs_io_main())
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .retryWhen(new RetryWithDelay(3000L))
              .subscribe(new BaseWanObserver<WanAndroidResponse<ArticleInfo>>(mRootView) {

                  @Override
                  public void onSuccess(WanAndroidResponse<ArticleInfo> response) {
                      ArticleInfo articleInfo = response.getData();
                      mRootView.showData(articleInfo);
                  }

                  @Override
                  public void onComplete() {
                      mRootView.hideLoading();
                  }
              });
    }
}
