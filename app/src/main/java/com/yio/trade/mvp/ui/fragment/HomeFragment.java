package com.yio.trade.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.classic.common.MultipleStatusView;
import com.jess.arms.base.BaseLazyLoadFragment;
import com.yio.mtg.trade.R;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yio.trade.common.AppConfig;
import com.yio.trade.common.Const;
import com.yio.trade.common.GlideImageLoader;
import com.yio.trade.common.JApplication;
import com.yio.trade.common.ScrollTopListener;
import com.yio.trade.model.Article;
import com.yio.trade.model.BannerImg;
import com.yio.trade.mvp.contract.HomeContract;
import com.yio.trade.mvp.presenter.HomePresenter;
import com.yio.trade.mvp.ui.activity.MainActivity;
import com.yio.trade.mvp.ui.activity.WebActivity;
import com.yio.trade.mvp.ui.adapter.ArticleAdapter;
import com.yio.trade.utils.JUtils;
import com.yio.trade.utils.RouterHelper;
import com.yio.trade.utils.RvScrollTopUtils;
import com.yio.trade.utils.SmartRefreshUtils;
import com.yio.trade.utils.UIUtils;
import com.yio.trade.utils.WrapContentLinearLayoutManager;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import com.yio.trade.di.component.DaggerHomeComponent;
import com.yio.trade.event.Event;
import com.yio.trade.model.ArticleInfo;

import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class HomeFragment extends BaseLazyLoadFragment<HomePresenter>
        implements HomeContract.View, ScrollTopListener {

    private Banner banner;

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.statusView)
    MultipleStatusView statusView;

    private ArticleAdapter adapter;

    private int page;
    private List<BannerImg> mBannerImgs;
    private int pageCount;
    private LinearLayoutManager layoutManager;

    private List<String> bannerUrls = new ArrayList<>();
    private List<String> bannerTitles = new ArrayList<>();

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerHomeComponent //如找不到该类,请编译一下项目
                            .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(Const.Key.SAVE_INSTANCE_STATE)) {
                //                lazyLoadData();
            }
        }
        initRefreshLayout();
        initScrollList();
        initBanner();
        initStatusView();
    }

    private void initStatusView() {
        statusView.setOnRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.requestHomeData();
            }
        });
    }

    private void initScrollList() {
        assert mPresenter != null;
        adapter = new ArticleAdapter(new ArrayList<>(), ArticleAdapter.TYPE_COMMON);
        loadAnimation(AppConfig.getInstance().getRvAnim());
        layoutManager = new WrapContentLinearLayoutManager(mContext);
        ArmsUtils.configRecyclerView(mRecyclerView, layoutManager);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
//            switchToWebPage(position);
        });
        adapter.setLikeListener(new ArticleAdapter.LikeListener() {
            @Override
            public void liked(Article item, int adapterPosition) {
                mPresenter.collectArticle(item, adapterPosition);
            }

            @Override
            public void unLiked(Article item, int adapterPosition) {
                mPresenter.collectArticle(item, adapterPosition);
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Article article = HomeFragment.this.adapter.getData().get(position);
                switch (view.getId()) {
                    case R.id.tvAuthor:
//                        switchToUserPage(article);
                        break;
                    case R.id.tvType:
//                        switchTabPage(article);
                        break;
                    default:
                        break;
                }
            }
        });
        adapter.setOnLoadMoreListener(() -> {
            if (pageCount != 0 && pageCount == page + 1) {
                Timber.d("不再加载更多");
                adapter.loadMoreEnd();
                return;
            }
            page++;
            mPresenter.requestArticle(page);
        }, mRecyclerView);
    }

    private void switchTabPage(Article article) {

    }

    private void switchToUserPage(Article article) {
        RouterHelper.switchToUserPage((MainActivity)getActivity(), article);
    }

    private void switchToWebPage(int position) {
        Intent intent = new Intent(mContext, WebActivity.class);
        Article article = adapter.getData().get(position);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_TYPE, WebActivity.TYPE_ARTICLE);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_DATA, article);
        launchActivity(intent);
    }

    private void initRefreshLayout() {
        SmartRefreshUtils.with(refreshLayout)
                         .pureScrollMode()
                         .setRefreshListener(() -> {
                             if (mPresenter != null) {
                                 page = 0;
                                 mPresenter.requestHomeData();
                             }
                         });
    }

    private void initBanner() {
        // 用代码创建的banner无法显示指示器，换为使用布局创建
        //        banner = new Banner(mContext);
        banner = (Banner)LayoutInflater.from(mContext)
                                       .inflate(R.layout.layout_banner, mRecyclerView, false);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dp2px(mContext, 200L));
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        banner.setLayoutParams(params);
        //显示圆形指示器和标题（水平显示)
        banner.setOnBannerListener(position -> {
            if (null != mBannerImgs && mBannerImgs.size() > 0) {
//                Intent intent = new Intent(mContext, WebActivity.class);
//                intent.putExtra(Const.Key.KEY_WEB_PAGE_TYPE, WebActivity.TYPE_BANNER);
//                intent.putExtra(Const.Key.KEY_WEB_PAGE_DATA, mBannerImgs.get(position));
//                startActivity(intent);
            }
        });
        //                Paint paint = new Paint();
        //                ColorMatrix cm = new ColorMatrix();
        //                cm.setSaturation(0);
        //                paint.setColorFilter(new ColorMatrixColorFilter(cm));
        //                getWindow().getDecorView().setLayerType(View.LAYER_TYPE_HARDWARE, paint);
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading() {
        statusView.showLoading();
    }

    @Override
    public void hideLoading() {
        refreshLayout.finishRefresh();
        statusView.showContent();
        if (adapter.isLoading()) {
            adapter.loadMoreComplete();
        }
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ToastUtils.showShort(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {

    }

    @Override
    public void showMoreArticles(ArticleInfo articleInfo) {
        this.pageCount = articleInfo.getPageCount();
        List<Article> data = articleInfo.getDatas();
        adapter.addData(data);
        adapter.loadMoreComplete();
    }

    @Override
    public void showBanner(List<BannerImg> bannerImgs) {
        mBannerImgs = bannerImgs;
        bannerUrls.clear();
        bannerTitles.clear();
        for (BannerImg bannerImg : bannerImgs) {
            bannerUrls.add(bannerImg.getImagePath());
            bannerTitles.add(JUtils.html2String(bannerImg.getTitle()));
        }
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(bannerUrls);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.Default);
        //设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(bannerTitles);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
        // 解决IllegalStateException，不能通过addHeaderView重复添加子View
        // 2.6.8版本新增setHeaderView方法
        if (adapter.getHeaderLayoutCount() > 0) {
            adapter.setHeaderView(banner);
        }
        else {
            adapter.addHeaderView(banner);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        hideLoading();
    }

    @Override
    public void refresh(List<Article> articleList) {
        adapter.replaceData(articleList);
    }

    @Override
    public void showLoadMoreFail() {
        adapter.loadMoreFail();
    }

    @Override
    public void addDailyPic(String url) {
        if (mBannerImgs == null || banner == null) {
            return;
        }
        List<String> titles = new ArrayList<>(bannerTitles);
        List<String> urls = new ArrayList<>(bannerUrls);
        titles.add(0, "每日一图");
        urls.add(0, url);
        BannerImg bannerImg = new BannerImg();
        bannerImg.setImagePath(url);
        bannerImg.setTitle("每日一图");
        mBannerImgs.add(0, bannerImg);
        banner.update(urls, titles);
    }

    @Override
    protected void lazyLoadData() {
        showLoading();
        page = 0;
        mPresenter.requestHomeData();
    }

    @Override
    public void scrollToTop() {
        RvScrollTopUtils.smoothScrollTop(mRecyclerView);
    }

    @Override
    public void scrollToTopRefresh() {
        lazyLoadData();
    }

    /**
     * 登录成功
     */
    @Subscriber
    public void onLoginSuccess(Event event) {
        if (null != event && event.getEventCode() == Const.EventCode.LOGIN_SUCCESS) {
            lazyLoadData();
        }
    }

    /**
     * 退出登录
     */
    @Subscriber
    public void onLogout(Event event) {
        if (null != event && event.getEventCode() == Const.EventCode.LOG_OUT) {
            lazyLoadData();
        }
    }

    private void loadAnimation(int type) {
        adapter.openLoadAnimation(type);
    }

    @Subscriber
    public void onAnimChanged(Event event) {
        if (null != event && event.getEventCode() == Const.EventCode.CHANGE_RV_ANIM) {
            Integer animType = (Integer)event.getData();
            if (animType != null && animType > 0) {
                loadAnimation(animType);
            }
        }
    }

    @Subscriber
    public void onArticleCollected(Event<Article> event) {
        if (null == event) {
            return;
        }
        if (event.getEventCode() == Const.EventCode.COLLECT_ARTICLE) {
            Article article = event.getData();
            for (Article item : adapter.getData()) {
                if (article.getId() == item.getId()) {
                    item.setCollect(article.isCollect());
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Timber.e("%s 保存状态", this.getClass().getSimpleName());
        // 意外销毁时（屏幕方向切换、颜色模式改变等）保存状态
        outState.putBoolean(Const.Key.SAVE_INSTANCE_STATE, true);
        super.onSaveInstanceState(outState);
    }

    public void showError(String msg) {
        statusView.showError(msg);
    }

    public void showNoNetwork() {
//        statusView.showNoNetwork();
        showMessage(JApplication.getInstance().getString(R.string.network_unavailable_tip));
    }

    @Override
    public void onCollectSuccess(Article article, int position) {

    }

    @Override
    public void onCollectFail(Article article, int position) {
        adapter.restoreLike(position);
    }
}
