package com.yio.trade.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.jess.arms.base.BaseLazyLoadFragment;
import com.yio.mtg.trade.R;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.yio.trade.common.Const;
import com.yio.trade.model.Tab;
import com.yio.trade.mvp.contract.WeixinContract;
import com.yio.trade.mvp.presenter.WeixinPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import com.yio.trade.common.ScrollTopListener;
import com.yio.trade.di.component.DaggerWeixinComponent;

import com.yio.trade.mvp.ui.adapter.TabFragmentStatePagerAdapter;
import pers.zjc.commonlibs.util.ToastUtils;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class WeixinFragment extends BaseLazyLoadFragment<WeixinPresenter>
        implements WeixinContract.View, ScrollTopListener {

    @BindView(R.id.tabLayout)
    SlidingTabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private TabFragmentStatePagerAdapter adapter;
    private List<String> mTitles = new ArrayList<>();

    public static WeixinFragment newInstance() {
        WeixinFragment fragment = new WeixinFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerWeixinComponent //如找不到该类,请编译一下项目
                              .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weixin, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initViewPager();
    }

    private void initViewPager() {
        adapter = new TabFragmentStatePagerAdapter(getChildFragmentManager(),
                new TabFragmentStatePagerAdapter.FragmentCreator() {
                    @Override
                    public Fragment createFragment(Tab data, int position) {
                        return TabFragment.create(data, position, Const.Type.TYPE_TAB_WEIXIN);
                    }

                    @Override
                    public String createTitle(Tab data) {
                        return Html.fromHtml(data.getName()).toString();
                    }
                });
        viewPager.setAdapter(adapter);
    }

    private void initTabLayout() {
        tabLayout.setViewPager(viewPager, mTitles.toArray(new String[0]));
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
        if (progressBar == null) {
            return;
        }
        progressBar.setVisibility(View.GONE);
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
    protected void lazyLoadData() {
        mPresenter.requestWxTab();
    }

    @Override
    public void scrollToTop() {
        if (adapter == null || viewPager == null) {
            return;
        }
        // 获取缓存的fragment引用
        Fragment fragment = adapter.getFragment(viewPager.getCurrentItem());
        if (fragment == null) {
            return;
        }
        if (fragment.isAdded() && fragment.getUserVisibleHint() && fragment instanceof ScrollTopListener) {
            ((ScrollTopListener)fragment).scrollToTop();
        }
     }

    @Override
    public void scrollToTopRefresh() {
        if (adapter == null || viewPager == null) {
            return;
        }
        // 获取缓存的fragment引用
        Fragment fragment = adapter.getFragment(viewPager.getCurrentItem());
        if (fragment == null) {
            return;
        }
        if (fragment.isAdded() && fragment.getUserVisibleHint() && fragment instanceof ScrollTopListener) {
            ((ScrollTopListener)fragment).scrollToTopRefresh();
        }
    }

    @Override
    public void showData(List<Tab> data) {
        viewPager.setOffscreenPageLimit(data.size() - 1);
        adapter.setData(data);
        for (Tab wxTab : data) {
            mTitles.add(Html.fromHtml(wxTab.getName()).toString());
        }
        initTabLayout();
    }


}
