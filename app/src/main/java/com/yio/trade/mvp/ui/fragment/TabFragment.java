package com.yio.trade.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.classic.common.MultipleStatusView;
import com.jess.arms.base.BaseLazyLoadFragment;
import com.yio.mtg.trade.R;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yio.trade.common.AppConfig;
import com.yio.trade.common.Const;
import com.yio.trade.common.ScrollTopListener;
import com.yio.trade.model.Article;
import com.yio.trade.model.ArticleInfo;
import com.yio.trade.model.Tab;
import com.yio.trade.mvp.contract.TabContract;
import com.yio.trade.mvp.presenter.TabPresenter;
import com.yio.trade.mvp.ui.activity.MainActivity;
import com.yio.trade.mvp.ui.activity.WebActivity;
import com.yio.trade.mvp.ui.adapter.ArticleAdapter;
import com.yio.trade.utils.RouterHelper;
import com.yio.trade.utils.RvScrollTopUtils;
import com.yio.trade.utils.SmartRefreshUtils;
import com.yio.trade.utils.WrapContentLinearLayoutManager;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import com.yio.trade.di.component.DaggerTabComponent;
import com.yio.trade.event.Event;


public class TabFragment extends BaseLazyLoadFragment<TabPresenter>
        implements TabContract.View, ScrollTopListener {

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.statusView)
    MultipleStatusView statusView;

    private ArticleAdapter adapter;
    private List<Article> mArticles = new ArrayList<>();
    private int page;
    private int pageCount;
    private int fromType = -1;
    private int cid = -1;

    public static TabFragment newInstance() {
        return new TabFragment();
    }

    public static Fragment create(Tab data, int position, int fromType) {
        TabFragment fragment = new TabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Const.Key.KEY_TAB_FROM_TYPE, fromType);
        bundle.putInt(Const.Key.KEY_TAB_CHILD_POSITION, position);
        bundle.putParcelable(Const.Key.KEY_TAB_DATA, data);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerTabComponent.builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setView();
        initData();
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        fromType = bundle.getInt(Const.Key.KEY_TAB_FROM_TYPE);
        switch (fromType) {
            case Const.Type.TYPE_TAB_KNOWLEDGE:
                initKnowledgeData(bundle);
                break;
            case Const.Type.TYPE_TAB_WEIXIN:
            case Const.Type.TYPE_TAB_PROJECT:
                initWeixinData(bundle);
                break;
            case -1:
            default:
                break;
        }
        if (mPresenter == null) {
            showMessage("presenter is null");
            return;
        }
    }

    private void initKnowledgeData(Bundle bundle) {
        Tab child = bundle.getParcelable(Const.Key.KEY_TAB_DATA);
        int position = bundle.getInt(Const.Key.KEY_TAB_CHILD_POSITION, -1);
        if (child == null || position == -1) {
            showMessage("参数错误");
            return;
        }
        cid = child.getId();
    }

    private void initWeixinData(Bundle bundle) {
        Tab child = bundle.getParcelable(Const.Key.KEY_TAB_DATA);
        if (child == null) {
            showMessage("参数错误");
            return;
        }
        cid = child.getId();
    }

    private void refreshData() {
        assert mPresenter != null;
        mPresenter.requestArticles(cid, 0, fromType);
    }

    private void setView() {
        adapter = new ArticleAdapter(new ArrayList<>(), ArticleAdapter.TYPE_COMMON);
        loadAnimation(AppConfig.getInstance().getRvAnim());
        ArmsUtils.configRecyclerView(mRecyclerView, new WrapContentLinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);
        // 开启越界回弹
        SmartRefreshUtils.with(refreshLayout)
                         .pureScrollMode()
                         .setRefreshListener(this::refreshData);
        adapter.setOnLoadMoreListener(() -> {
            if ((pageCount != 0 && pageCount == page + 1)) {
                adapter.loadMoreEnd();
                return;
            }
            if (cid == -1) {
                return;
            }
            page++;
            mPresenter.requestArticles(cid, page, fromType);
        }, mRecyclerView);
        adapter.setOnItemClickListener((adapter, view, position) -> switchToWebPage(position));
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
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            Article article  = this.adapter.getData().get(position);
            switch (view.getId()) {
                case R.id.tvAuthor:
                    RouterHelper.switchToUserPage((MainActivity)getActivity(), article);
                    break;
                default:
                    break;
            }
        });
    }

    private void switchToWebPage(int position) {
        Article article = adapter.getData().get(position);
        Intent intent = new Intent(mContext, WebActivity.class);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_TYPE, WebActivity.TYPE_ARTICLE);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_DATA, article);
        launchActivity(intent);
    }

    @Override
    public void setData(@Nullable Object data) {
    }

    @Override
    public void showData(ArticleInfo data) {
        pageCount = data.getPageCount();
        List<Article> articles = data.getDatas();
        if (articles.isEmpty()) {
            adapter.loadMoreEnd();
            return;
        }
        if (data.getCurPage() == 0) {
            mArticles = data.getDatas();
            adapter.replaceData(mArticles);
        }
        else {
            mArticles.addAll(data.getDatas());
            adapter.addData(data.getDatas());
            adapter.loadMoreComplete();
        }
    }

    @Override
    public void showMessage(@NonNull String message) {
        ToastUtils.showShort(message);
    }

    @Override
    protected void lazyLoadData() {
        page = 0;
        showLoading();
        refreshData();
    }

    @Override
    public void showLoading() {
        statusView.showLoading();
    }

    @Override
    public void hideLoading() {
        statusView.showContent();
        refreshLayout.finishRefresh();
        if (adapter.isLoading()) {
            adapter.loadMoreComplete();
        }
    }

    @Override
    public void updateCollectStatus(boolean isCollect, Article item, int position) {
        for (Article article : adapter.getData()) {
            if (article.equals(item)) {
                article.setCollect(isCollect);
            }
        }
        adapter.notifyItemChanged(position);
    }

    @Override
    public void scrollToTop() {
        RvScrollTopUtils.smoothScrollTop(mRecyclerView);
    }

    /**
     * 登录成功
     */
    @Subscriber
    public void onLoginSuccess(Event event) {
        if (null != event && event.getEventCode() == Const.EventCode.LOGIN_SUCCESS) {
            if (isAdded() && isVisible() && getUserVisibleHint()) {
                lazyLoadData();
                mPresenter.requestArticles(cid, page, fromType);
            }
        }
    }

    /**
     * 退出登录
     */
    @Subscriber
    public void onLogout(Event event) {
        if (null != event && event.getEventCode() == Const.EventCode.LOG_OUT) {
            if (isAdded() && isVisible() && getUserVisibleHint()) {
                lazyLoadData();
                mPresenter.requestArticles(cid, page, fromType);
            }
        }
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

    private void loadAnimation(int type) {
        adapter.openLoadAnimation(type);
    }

    @Override
    public void onCollectSuccess(Article article, int position) {

    }

    @Override
    public void onCollectFail(Article article, int position) {
        adapter.restoreLike(position);
    }

    @Override
    public void scrollToTopRefresh() {
        lazyLoadData();
    }

    public void showNoNetwork() {
        statusView.showNoNetwork();
    }
}
