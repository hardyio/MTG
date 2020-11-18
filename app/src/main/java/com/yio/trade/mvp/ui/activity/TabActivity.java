package com.yio.trade.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gyf.immersionbar.ImmersionBar;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.yio.mtg.trade.R;
import com.yio.trade.common.Const;
import com.yio.trade.model.Tab;
import com.yio.trade.mvp.presenter.TabPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import com.yio.trade.common.ScrollTopListener;
import com.yio.trade.mvp.ui.fragment.TabFragment;
import pers.zjc.commonlibs.ui.BaseStatePagerAdapter;
import pers.zjc.commonlibs.util.StringUtils;
import pers.zjc.commonlibs.util.ToastUtils;

public class TabActivity extends BaseActivity<TabPresenter> {

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
    @BindView(R.id.appBar)
    AppBarLayout appBar;
    @BindView(R.id.tabLayout)
    SlidingTabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.fabTop)
    FloatingActionButton fabTop;

    private int curPosition;

    private List<String> mTitles = new ArrayList<>();
    private String mTitle;
    private List<Tab> dataList = new ArrayList<>();
    private BaseStatePagerAdapter<Tab, Fragment> adapter;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_tab; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initData();
        setView();
    }

    @Override
    public boolean useFragment() {
        return true;
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            ToastUtils.showShort("intent data is null");
            killMyself();
            return;
        }
        int type = intent.getIntExtra(Const.Key.KEY_TAB_FROM_TYPE, 0);
        if (type == 0) {
            ToastUtils.showShort("非法类型参数");
            return;
        }
        switch (type) {
            case Const.Type.TYPE_TAB_KNOWLEDGE:
                loadKnowledgeData(intent);
                break;
            case Const.Type.TYPE_TAB_WEIXIN:
                break;
            default:
                break;
        }
    }

    private void loadKnowledgeData(Intent intent) {
        Tab data = intent.getParcelableExtra(Const.Key.KEY_TAB_DATA);
        curPosition = intent.getIntExtra(Const.Key.KEY_TAB_CHILD_POSITION, -1);
        if (data == null) {
            showMessage("数据项为空");
            return;
        }
        mTitle = data.getName();
        this.dataList = data.getChildren();
        if (dataList.isEmpty()) {
            return;
        }
        for (Tab tab : data.getChildren()) {
            mTitles.add(tab.getName());
        }
    }

    private void setView() {
        initToolbar();
        initViewPager();
        initTabLayout();
        initFab();
    }

    private void initFab() {
        fabTop.setOnClickListener(v -> slideToTop());
    }

    private void slideToTop() {
        if (viewPager == null || adapter == null) {
            return;
        }
        int pos = viewPager.getCurrentItem();
        Fragment fragment = adapter.getFragment(pos);
        if (fragment instanceof ScrollTopListener) {
            ((ScrollTopListener)fragment).scrollToTop();
        }
    }

    private void initToolbar() {
        tvTitle.setText(StringUtils.isEmpty(mTitle) ? "知识体系" : mTitle);
        ivLeft.setOnClickListener(v -> killMyself());
    }

    private void initTabLayout() {
        if (curPosition == -1) {
            showMessage("非法参数");
            return;
        }
        tabLayout.setViewPager(viewPager, mTitles.toArray(new String[0]));
        tabLayout.setCurrentTab(curPosition, true);
    }

    private void initViewPager() {
        viewPager.setOffscreenPageLimit(mTitles.size() - 1);
        adapter = new BaseStatePagerAdapter<>(
                getSupportFragmentManager(),
                new BaseStatePagerAdapter.PagerFragCreator<Tab, Fragment>() {
                    @Override
                    public Fragment createFragment(Tab data, int position) {
                        return TabFragment.create(data, curPosition, Const.Type.TYPE_TAB_KNOWLEDGE);
                    }

                    @Override
                    public String createTitle(Tab data) {
                        return data.getName();
                    }
                });
        viewPager.setAdapter(adapter);
        adapter.setData(dataList);
    }

    public void killMyself() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImmersionBar.with(this)
                    .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                    .statusBarColor(R.color.colorPrimary)
                    .init();
        super.onCreate(savedInstanceState);
    }

    public void showMessage(String msg) {
        ToastUtils.showShort(msg);
    }

}
