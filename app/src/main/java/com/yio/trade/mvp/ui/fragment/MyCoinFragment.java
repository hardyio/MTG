package com.yio.trade.mvp.ui.fragment;

import android.content.Intent;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jess.arms.base.BaseFragment;
import com.yio.mtg.trade.R;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.yio.trade.common.Const;
import com.yio.trade.model.Coin;
import com.yio.trade.model.CoinHistory;
import com.yio.trade.model.PageInfo;
import com.yio.trade.mvp.contract.MyCoinContract;
import com.yio.trade.mvp.presenter.MyCoinPresenter;
import com.yio.trade.mvp.ui.adapter.MyCoinAdapter;
import com.yio.trade.utils.RvScrollTopUtils;
import com.yio.trade.utils.WrapContentLinearLayoutManager;
import com.yio.trade.widgets.DashboardView;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import com.yio.trade.di.component.DaggerMyCoinComponent;
import com.yio.trade.event.Event;

import pers.zjc.commonlibs.util.FragmentUtils;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class MyCoinFragment extends BaseFragment<MyCoinPresenter> implements MyCoinContract.View {

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
    @BindView(R.id.dvCoin)
    DashboardView dvCoin;
    @BindView(R.id.rlHead)
    RelativeLayout rlHead;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.fabTop)
    FloatingActionButton fabTop;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private MyCoinAdapter adapter;
    private int pageCount;
    private int page = 1;

    private int maxCoin;
    private int myCoin;

    public static MyCoinFragment newInstance() {
        return new MyCoinFragment();
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerMyCoinComponent //如找不到该类,请编译一下项目
                              .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_appbar_recyclerview, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setView();
        mPresenter.loadRank(1);
        mPresenter.loadMyCoinHistory(1);
    }

    private void setView() {
        initToolbar();
        initRecyclerView();
        fabTop.setOnClickListener(v -> scrollToTop());
    }

    private void initRecyclerView() {
        adapter = new MyCoinAdapter(R.layout.item_my_coin, new ArrayList<>());
        ArmsUtils.configRecyclerView(mRecyclerView, new WrapContentLinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);
        adapter.setOnLoadMoreListener(() -> {
            if ((pageCount != 0 && pageCount == page + 1)) {
                adapter.loadMoreEnd();
                return;
            }
            page++;
            mPresenter.loadMyCoinHistory(page);
        }, mRecyclerView);
    }

    private void initToolbar() {
        tvTitle.setText("My coin");
        toolbarLeft.setOnClickListener(v -> killMyself());
        rlHead.setVisibility(View.VISIBLE);
        Bundle bundle = getArguments();
        if (null != bundle) {
            myCoin = bundle.getInt(Const.Key.KEY_COIN_COUNT);
            if (myCoin != 0) {
//                loadCoinAnim();
            }
        }
    }

    private void loadCoinAnim() {
        if (myCoin == 0) {
            mPresenter.loadMyCoin();
        } else {
            dvCoin.setProgress(maxCoin, myCoin);
        }
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

    public void scrollToTop() {
        RvScrollTopUtils.smoothScrollTop(mRecyclerView);
    }

    @Override
    public void showData(PageInfo<CoinHistory> info) {
        pageCount = info.getPageCount();
        List<CoinHistory> coins = info.getDatas();
        if (coins.isEmpty()) {
            adapter.loadMoreEnd();
            return;
        }
        if (info.getCurPage() == 1) {
            adapter.replaceData(info.getDatas());
        }
        else {
            adapter.addData(info.getDatas());
            adapter.loadMoreComplete();
        }
    }

    @Override
    public void showRank(Coin maxCoin) {
        this.maxCoin = maxCoin.getCoinCount();
        loadCoinAnim();
    }

    @Override
    public void setMyCoin(Coin data) {
        this.myCoin = data.getCoinCount();
        dvCoin.setProgress(maxCoin, myCoin);
    }

    /**
     * 登录成功
     */
    @Subscriber
    public void onLoginSuccess(Event event) {
        if (null != event && event.getEventCode() == Const.EventCode.LOGIN_SUCCESS) {
            mPresenter.loadData();
        }
    }

}
