package com.yio.trade.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.StringUtils;
import com.jess.arms.base.BaseFragment;
import com.yio.mtg.trade.R;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.integration.EventBusManager;
import com.jess.arms.utils.ArmsUtils;
import com.yio.trade.common.AppConfig;
import com.yio.trade.common.CommonTextWatcher;
import com.yio.trade.common.Const;
import com.yio.trade.mvp.contract.ShareContract;
import com.yio.trade.mvp.presenter.SharePresenter;
import com.yio.trade.mvp.ui.activity.WebActivity;

import org.simple.eventbus.EventBus;

import java.util.Objects;

import butterknife.BindView;
import com.yio.trade.di.component.DaggerShareComponent;
import com.yio.trade.event.Event;

import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class ShareFragment extends BaseFragment<SharePresenter> implements ShareContract.View {

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
    @BindView(R.id.tvArcTitle)
    TextView tvArcTitle;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.tvLink)
    TextView tvLink;
    @BindView(R.id.etLink)
    EditText etLink;
    @BindView(R.id.tvRefresh)
    TextView tvRefresh;
    @BindView(R.id.tvOpen)
    TextView tvOpen;

    public static ShareFragment newInstance() {
        ShareFragment fragment = new ShareFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerShareComponent //如找不到该类,请编译一下项目
                             .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        checkLogin();
        initView();
    }

    private void checkLogin() {
        if (!AppConfig.getInstance().isLogin()) {
            EventBus.getDefault().post(new Event<>(Const.EventCode.LOGIN_EXPIRED, null));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        CharSequence cs = ClipboardUtils.getText();
        if (cs == null) {
            Timber.d("未获取到剪切板内容");
            return;
        }
        String url = cs.toString();
        Timber.d("获取到剪切板内容%s", url);
        etLink.setText(url);
    }

    private void initView() {
        initToolbar();
        initText();
    }

    private void initToolbar() {
        ivLeft.setOnClickListener(v -> killMyself());
        tvTitle.setText("分享文章");
//        Glide.with(ivRight.getContext()).load(R.drawable.ic_upload).into(ivRight);
        ivRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_upload));
        ivRight.setOnClickListener(v -> shareArticle());
        ivRight.setVisibility(View.GONE);
    }

    private void initText() {
        CommonTextWatcher watcher1 = new CommonTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                validate();
            }
        };
        CommonTextWatcher watcher2 = new CommonTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                validate();
                parseHtml();
            }
        };
        etTitle.addTextChangedListener(watcher1);
        etLink.addTextChangedListener(watcher2);
        tvRefresh.setOnClickListener(v -> parseHtml());
        tvOpen.setOnClickListener(v -> openWebPage());
    }

    private void openWebPage() {
        String title = etTitle.getText().toString().trim();
        String url = etLink.getText().toString().trim();
        Intent intent = new Intent(mContext, WebActivity.class);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_TYPE, WebActivity.TYPE_URL);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_URL, url);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_TITLE, title);
        launchActivity(intent);
    }

    private void parseHtml() {
        String url = etLink.getText().toString().trim();
        mPresenter.parseTitleFromUrl(url);
    }

    /**
     * 设置提交按钮隐藏
     */
    private void validate() {
        String link = etLink.getText().toString();
        String title = etTitle.getText().toString();
        boolean enable = !StringUtils.isEmpty(link) && !StringUtils.isEmpty(title);
        ivRight.setVisibility(enable ? View.VISIBLE : View.GONE);
        tvOpen.setEnabled(enable);
    }

    private void shareArticle() {
        String title = etTitle.getText().toString();
        String link = etLink.getText().toString();
        mPresenter.share(title, link);
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

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
        if (getFragmentManager() != null) {
            FragmentUtils.pop(getFragmentManager(), true);
        }
    }

    @Override
    public void success() {
        EventBusManager.getInstance().post(new Event<>(Const.EventCode.SHARE_SUCCESS, null));
        KeyboardUtils.hideSoftInput(Objects.requireNonNull(getActivity()));
        killMyself();
    }

    @Override
    public void parseOut(String title) {
        etTitle.setText(title);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            KeyboardUtils.hideSoftInput(getActivity());
        }
    }

}
