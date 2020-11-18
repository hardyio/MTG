package com.yio.trade.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.yio.trade.mvp.contract.QAContract;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import com.yio.trade.common.CollectHelper;
import com.yio.trade.model.Article;
import com.yio.trade.model.ArticleInfo;

@FragmentScope
public class QAPresenter extends BasePresenter<QAContract.Model, QAContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public QAPresenter(QAContract.Model model, QAContract.View rootView) {
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

    public void loadData(int page) {
//        mModel.getQAList(page)
//              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
//              .compose(RxScheduler.Obs_io_main())
//              .subscribe(new BaseWanObserver<WanAndroidResponse<ArticleInfo>>(mRootView) {
//
//                  @Override
//                  public void onSuccess(WanAndroidResponse<ArticleInfo> response) {
//                      ArticleInfo info = response.getData();
//                      mRootView.showData(info);
//                  }
//
//                  @Override
//                  public void onComplete() {
//                      mRootView.hideLoading();
//                  }
//              });
        ArticleInfo info = new ArticleInfo();
        List<Article> articles = new ArrayList<>();
        articles.add(new Article("","",249,"Majors",false,13,"","",false,12554,"link","Nov 05, 12:36","","","",1606665600000L,0,"EURUSD","EUR/USD: Extra gains likely in the near-term – UOB",1,-1,1,0,null,false,0,"",0));
        articles.add(new Article("","",250,"Crosses",false,14,"","",false,12554,"link","Nov 01, 18:21","","","",1606665600000L,0,"EURGBP","EUR/GBP climbs to over one-week tops, beyond mid-0.9000s ahead of BoE",1,-1,1,0,null,false,0,"",0));
        articles.add(new Article("","",251,"Metals",false,15,"","",false,12554,"link","Nov 01, 08:10","","","",1606665600000L,0,"Gold","Gold Price Analysis: XAU/USD refreshes intraday high above $1,900 on triangle break",1,-1,1,0,null,false,0,"",0));
        articles.add(new Article("","",252,"Eurozone",false,16,"","",false,12554,"link","Oct 30, 09:20","","","",1606665600000L,0,"Germany","Coronavirus update: Germany reports about 20K new cases, record daily rise",1,-1,1,0,null,false,0,"",0));
        articles.add(new Article("","",253,"Currencies",false,17,"","",false,12554,"link","Oct 30, 07:12","","","",1606665600000L,0,"NZDUSD","NZD/USD Price Analysis: Gyrates near six-week-old ascending channel resistance",1,-1,1,0,null,false,0,"",0));
        articles.add(new Article("","",254,"Majors",false,18,"","",false,12554,"link","Oct 30, 08:39","","","",1606665600000L,0,"USDCHF","USD/CHF options show weakest bearish bias since May 2019",1,-1,1,0,null,false,0,"",0));
        info.setDatas(articles);
        mRootView.showData(info);
        mRootView.hideLoading();
    }

    public void collectArticle(Article item, int position) {
        CollectHelper.with(mRootView).target(item).position(position).collect();
    }
}
