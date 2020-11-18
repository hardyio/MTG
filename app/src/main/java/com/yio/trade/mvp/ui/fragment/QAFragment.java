package com.yio.trade.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.classic.common.MultipleStatusView;
import com.jess.arms.base.BaseLazyLoadFragment;
import com.yio.mtg.trade.R;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yio.trade.common.Const;
import com.yio.trade.common.ScrollTopListener;
import com.yio.trade.model.Article;
import com.yio.trade.model.ArticleInfo;
import com.yio.trade.mvp.contract.QAContract;
import com.yio.trade.mvp.presenter.QAPresenter;
import com.yio.trade.mvp.ui.activity.WebActivity;
import com.yio.trade.mvp.ui.adapter.ArticleAdapter;
import com.yio.trade.utils.RvAnimUtils;
import com.yio.trade.utils.RvScrollTopUtils;
import com.yio.trade.utils.SmartRefreshUtils;
import com.yio.trade.utils.WrapContentLinearLayoutManager;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import com.yio.trade.di.component.DaggerQAComponent;
import com.yio.trade.event.Event;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class QAFragment extends BaseLazyLoadFragment<QAPresenter>
        implements QAContract.View, ScrollTopListener {

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.statusView)
    MultipleStatusView statusView;

    private ArticleAdapter adapter;

    private int page = 1;
    private int pageCount;

    public static QAFragment newInstance() {
        QAFragment fragment = new QAFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerQAComponent //如找不到该类,请编译一下项目
                          .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qa, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initScrollList();
        initRefreshLayout();
        initStatusView();
    }

    private void initStatusView() {
        statusView.setOnRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.loadData(1);
            }
        });
    }

    private void initScrollList() {
        assert mPresenter != null;
        adapter = new ArticleAdapter(new ArrayList<>(), ArticleAdapter.TYPE_COMMON);
        RvAnimUtils.loadAnimation(adapter);
        ArmsUtils.configRecyclerView(mRecyclerView, new WrapContentLinearLayoutManager(mContext));
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
        adapter.setOnLoadMoreListener(() -> {
            if (pageCount != 0 && pageCount == page + 1) {
                adapter.loadMoreEnd();
                return;
            }
            page++;
            mPresenter.loadData(page);
        }, mRecyclerView);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            Article article  = this.adapter.getData().get(position);
            switch (view.getId()) {
                case R.id.tvAuthor:
//                    RouterHelper.switchToUserPage((MainActivity)getActivity(), article);
                    break;
                default:
                    break;
            }
        });
    }

    private void switchToWebPage(int position) {
        Intent intent = new Intent(mContext, WebActivity.class);
        Article article = adapter.getData().get(position);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_TYPE, WebActivity.TYPE_ARTICLE);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_DATA, article);
        launchActivity(intent);
    }

    private void initRefreshLayout() {
        SmartRefreshUtils.with(refreshLayout).pureScrollMode().setRefreshListener(() -> {
            if (mPresenter != null) {
                page = 1;
                mPresenter.loadData(page);
            }
        });
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
        statusView.showContent();
        refreshLayout.finishRefresh();
        if (adapter.isLoading()) {
            adapter.loadMoreComplete();
        }
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
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
    protected void lazyLoadData() {
        showLoading();
        mPresenter.loadData(page);
    }

    @Override
    public void showData(ArticleInfo data) {
        pageCount = data.getPageCount();
        List<Article> articles = data.getDatas();
        if (articles.isEmpty()) {
            adapter.loadMoreEnd();
            return;
        }
        if (data.getCurPage() == 1) {
            adapter.replaceData(articles);
            if (data.getPageCount() == data.getCurPage()) {
                adapter.loadMoreEnd();
            }
        }
        else {
            adapter.addData(data.getDatas());
            adapter.loadMoreComplete();
        }
    }

    @Override
    public void scrollToTop() {
        RvScrollTopUtils.smoothScrollTop(mRecyclerView);
    }

    @Override
    public void showError(String msg) {
        statusView.showError(msg);
    }

    @Override
    public void showNoNetwork() {
        statusView.showNoNetwork();
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

    @Subscriber
    public void onAnimChanged(Event event) {
        if (null != event && event.getEventCode() == Const.EventCode.CHANGE_RV_ANIM) {
            if (adapter != null) {
                RvAnimUtils.loadAnimation(adapter);
            }
        }
    }
}
