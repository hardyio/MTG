package com.yio.trade.mvp.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.FragmentUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jess.arms.base.BaseFragment;
import com.yio.mtg.trade.R;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.yio.trade.common.Const;
import com.yio.trade.model.Article;
import com.yio.trade.model.Coin;
import com.yio.trade.model.PageInfo;
import com.yio.trade.mvp.contract.RankContract;
import com.yio.trade.mvp.presenter.RankPresenter;
import com.yio.trade.mvp.ui.activity.MainActivity;
import com.yio.trade.mvp.ui.adapter.RankAdapter;
import com.yio.trade.utils.RouterHelper;
import com.yio.trade.utils.RvScrollTopUtils;
import com.yio.trade.utils.WrapContentLinearLayoutManager;
import com.yio.trade.widgets.DashboardView;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import com.yio.trade.di.component.DaggerRankComponent;
import com.yio.trade.event.Event;


import static com.jess.arms.utils.Preconditions.checkNotNull;

public class RankFragment extends BaseFragment<RankPresenter> implements RankContract.View {

    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.toolbar_left)
    RelativeLayout toolbarLeft;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivRight)
    ImageView ivRight;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.fabTop)
    FloatingActionButton fabTop;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.rlHead)
    RelativeLayout rlHead;
    @BindView(R.id.dvCoin)
    DashboardView dvCoin;

    private int pageCount;

    private RankAdapter adapter;
    private List<Coin> mData = new ArrayList<>();
    private int page = 1;
    private Coin myCoin;
    private Coin maxCoin;

    public static RankFragment newInstance() {
        return new RankFragment();
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerRankComponent //如找不到该类,请编译一下项目
                            .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_appbar_recyclerview, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initView();
        initArgs();
    }

    private void initArgs() {
        Bundle bundle = getArguments();
        if (null == bundle) {
            return;
        }
        myCoin = bundle.getParcelable(Const.Key.KEY_MY_COIN);
        if (null == myCoin) {
            mPresenter.loadMyCoin();
        } else {
            mPresenter.loadRank(page);
        }
    }

    private void initView() {
        initToolbar();
        initRecyclerView();
        fabTop.setOnClickListener(v -> scrollToTop());
    }

    private void initRecyclerView() {
        adapter = new RankAdapter(R.layout.item_rank, new ArrayList<>());
        ArmsUtils.configRecyclerView(mRecyclerView, new WrapContentLinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);
        adapter.setOnLoadMoreListener(() -> {
            if ((pageCount != 1 && pageCount == page + 1)) {
                adapter.loadMoreEnd();
                return;
            }
            page++;
            mPresenter.loadRank(page);
        }, mRecyclerView);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            Coin coin  = this.adapter.getData().get(position);
            switch (view.getId()) {
                case R.id.tvUser:
                    Article article = new Article();
                    article.setAuthor(coin.getUsername());
                    article.setUserId(coin.getUserId());
                    RouterHelper.switchToUserPage((MainActivity)getActivity(), article);
                    break;
                default:
                    break;
            }
        });
    }

    private void initToolbar() {
        tvTitle.setText("Coin");
        toolbarLeft.setOnClickListener(v -> killMyself());
        rlHead.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.drawable.ic_question);
        ivRight.setColorFilter(Color.WHITE);
        ivRight.setOnClickListener(v -> RouterHelper.switchToWebPageWithUrl(mContext, Const.Url.RANK_RULES, "Coin rule"));
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        // 由于解绑时机发生在onComplete()之后，容易引起空指针
//        if (progressBar == null) {
//            return;
//        }
        progressBar.setVisibility(View.GONE);
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
        FragmentUtils.pop(getFragmentManager(), true);
    }

    @Override
    public void showData(PageInfo<Coin> info) {
        pageCount = info.getPageCount();
        List<Coin> coins = info.getDatas();
        if (coins.isEmpty()) {
            adapter.loadMoreEnd();
            return;
        }
        if (info.getCurPage() == 1) {
            mData = info.getDatas();
            adapter.replaceData(mData);
            // 积分进度
            maxCoin = mData.get(0);
            dvCoin.setProgress(maxCoin.getCoinCount(), myCoin.getCoinCount());
        } else {
            mData.addAll(info.getDatas());
            adapter.addData(info.getDatas());
            adapter.loadMoreComplete();
        }
    }

    @Override
    public void showCoin(Coin data) {
        this.myCoin = data;
        mPresenter.loadRank(page);
    }

    public void scrollToTop() {
        RvScrollTopUtils.smoothScrollTop(mRecyclerView);
    }

    @Subscriber
    public void onLoginSuccess(Event event) {
        if (null != event && event.getEventCode() == Const.EventCode.LOGIN_SUCCESS) {
            mPresenter.loadMyCoin();
        }
    }
}
