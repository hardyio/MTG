package com.yio.trade.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.yio.trade.model.Coin;
import com.yio.trade.mvp.contract.UserContract;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import com.yio.trade.base.BaseWanObserver;
import com.yio.trade.common.CollectHelper;
import com.yio.trade.model.Article;
import com.yio.trade.model.ArticleInfo;
import com.yio.trade.model.ShareUserArticles;
import com.yio.trade.result.WanAndroidResponse;
import com.yio.trade.utils.rx.RxScheduler;

@FragmentScope
public class UserPresenter extends BasePresenter<UserContract.Model, UserContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public UserPresenter(UserContract.Model model, UserContract.View rootView) {
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

    public void loadData(long userId, int page) {
        mModel.getUserArticles(userId, page)
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .compose(RxScheduler.Obs_io_main())
              .subscribe(new BaseWanObserver<WanAndroidResponse<ShareUserArticles>>(mRootView) {
                  @Override
                  public void onSuccess(WanAndroidResponse<ShareUserArticles> response) {
                        ShareUserArticles data = response.getData();
                        Coin coin = data.getCoinInfo();
                        mRootView.showCoin(coin);
                        ArticleInfo info = data.getShareArticles();
                        mRootView.showArticles(info);
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
