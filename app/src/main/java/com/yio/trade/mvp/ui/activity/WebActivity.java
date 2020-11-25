package com.yio.trade.mvp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.gyf.immersionbar.ImmersionBar;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.yio.mtg.trade.R;
import com.yio.trade.bean.SignInBean;
import com.yio.trade.common.Const;
import com.yio.trade.di.component.DaggerWebComponent;
import com.yio.trade.model.Article;
import com.yio.trade.model.BannerImg;
import com.yio.trade.mvp.contract.WebContract;
import com.yio.trade.mvp.presenter.WebPresenter;
import com.yio.trade.utils.UIUtils;
import com.yio.trade.utils.WebViewClient;
import com.yio.trade.widgets.CustomWebView;

import butterknife.BindView;

import static android.view.KeyEvent.KEYCODE_BACK;
import static com.jess.arms.utils.Preconditions.checkNotNull;

public class WebActivity extends BaseActivity<WebPresenter> implements WebContract.View {

    public static final int TYPE_ARTICLE = 1;
    public static final int TYPE_BANNER = 2;
    public static final int TYPE_URL = 3;
    private static final int RC_SIGN_IN = 200;

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
    @BindView(R.id.webView)
    CustomWebView webView;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.ivRefresh)
    ImageView ivRefresh;
    @BindView(R.id.ivForward)
    ImageView ivForward;

    private String mUrl = Const.Url.WAN_ANDROID;
    private String mTitle = "";
    private int forbid;
    private String jsMethodName = "";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private String openGoogleJson;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerWebComponent //如找不到该类,请编译一下项目
                .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_web; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        ImmersionBar.with(this)
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.colorPrimary)
                .init();
        initParams();
        initTitle();
        initWebView();
        initNavigation();
        initGoogleSignIn();
    }

    private void initGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    private void initParams() {
        Intent intent = getIntent();
        if (null != intent) {
            int startType = intent.getIntExtra(Const.Key.KEY_WEB_PAGE_TYPE, 0);
            switch (startType) {
                case 0:
                    ToastUtils.showShort("Illegal parameter");
                    killMyself();
                    break;
                case TYPE_ARTICLE:
                    Article article = intent.getParcelableExtra(Const.Key.KEY_WEB_PAGE_DATA);
                    if (article == null) {
                        showMessage("Parameter error");
                        killMyself();
                        return;
                    }
                    mUrl = article.getLink();
                    mTitle = article.getTitle();
                    break;
                case TYPE_BANNER:
                    BannerImg bannerImg = intent.getParcelableExtra(Const.Key.KEY_WEB_PAGE_DATA);
                    if (bannerImg == null) {
                        showMessage("Parameter error");
                        killMyself();
                        return;
                    }
                    mUrl = bannerImg.getUrl();
                    mTitle = bannerImg.getTitle();
                    break;
                case TYPE_URL:
                default:
                    mUrl = intent.getStringExtra(Const.Key.KEY_WEB_PAGE_URL);
                    mTitle = intent.getStringExtra(Const.Key.KEY_WEB_PAGE_TITLE);
                    break;
            }
        }
    }

    private void initNavigation() {
        ivBack.setOnClickListener(v -> {
            if (webView.canGoBack()) {
                webView.goBack();
            }
        });
        ivForward.setOnClickListener(v -> {
            if (webView.canGoForward()) {
                webView.goForward();
            }
        });
        ivRefresh.setOnClickListener(v -> webView.reload());
    }

    private void initTitle() {
        ivLeft.setImageResource(R.mipmap.ic_arrow_back_white_24dp);
        // 跑马灯必须加
        tvTitle.setSelected(true);
        tvTitle.setFocusable(true);
        tvTitle.setFocusableInTouchMode(true);
        tvTitle.setText(mTitle);
        ivRight.setVisibility(View.GONE);
        ivRight.setImageResource(R.drawable.ic_more_vert);
        ivLeft.setOnClickListener(v -> killMyself());
    }

    public CustomWebView getWebView() {
        return webView;
    }

    public void showTitleBar(boolean visible) {
        runOnUiThread(() -> toolbar.setVisibility(visible ? View.VISIBLE : View.GONE));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        //设置垂直滚动条
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.setScrollBarSize(UIUtils.dp2px(this, 20));
        //支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
//        webView.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        //如果不设置WebViewClient，请求会跳转系统浏览器
        webView.setWebViewClient(new WebViewClient(WebActivity.this, mUrl));
        webView.loadUrl(mUrl);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //点击回退键时，不会退出浏览器而是返回网页上一页
        if ((keyCode == KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        finish();
    }

    @Override
    public void updateCollectStatus(boolean collect, Article article) {
//        ivRight.setImageResource(collect ? R.drawable.ic_like_fill : R.drawable.ic_like);
        article.setCollect(collect);
    }

    @Override
    public void getTokenSuccess(String host, String token1, String token2, String url) {
        if (!TextUtils.isEmpty(token1)) {
            CookieManager.getInstance().setCookie(host, "token1=" + token1 + ";expires=1; path=/");
        }
        if (!TextUtils.isEmpty(token2)) {
            CookieManager.getInstance().setCookie(host, "token2=" + token2 + ";expires=1; path=/");
        }
        if (TextUtils.isEmpty(token1) && TextUtils.isEmpty(token2)) {
            webView.loadUrl(url);
        }
    }

    @Override
    public void onBackPressed() {
        if (this.forbid == 1 || TextUtils.isEmpty(jsMethodName)) {
            return;
        }
        if (!TextUtils.isEmpty(jsMethodName)) {
            String javascript = "javascript:" + jsMethodName + "()";
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                webView.loadUrl(javascript);
            } else {
                webView.evaluateJavascript(javascript, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //此处为 js 返回的结果
                    }
                });
            }
        }
        super.onBackPressed();
    }

    public void setShouldForbidBackPress(int forbid) {
        this.forbid = forbid;
    }

    public void setBackPressJSMethod(String methodName) {
        this.jsMethodName = methodName;
    }

    public void googleSignIn(String json) {
        this.openGoogleJson = json;
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CustomWebView.REQUEST_SELECT_FILE:
                if (webView.uploadMessageArr == null)
                    return;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    webView.uploadMessageArr.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                }
                webView.uploadMessageArr = null;
                break;
            case CustomWebView.FILECHOOSER_RESULTCODE:
                if (null == webView.uploadMessage)
                    return;
                // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
                // Use RESULT_OK only if you're implementing WebView inside an Activity
                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                webView.uploadMessage.onReceiveValue(result);
                webView.uploadMessage = null;
                break;
            case RC_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (!TextUtils.isEmpty(this.openGoogleJson)) {
                        SignInBean signInBean = GsonUtils.fromJson(this.openGoogleJson, SignInBean.class);
                        String id = account.getId();
                        String displayName = account.getDisplayName();
                        String email = account.getEmail();
                        assert mPresenter != null;
                        mPresenter.getToken(id, displayName, email, signInBean.getSign(), "1", signInBean.getHost());
                    }
                } catch (Exception e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Exception", e);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String url = intent.getStringExtra(Const.Key.KEY_WEB_PAGE_URL);
        if (!TextUtils.isEmpty(url) && webView != null) {
            webView.loadUrl(url);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                        // ...
                    }
                });
    }

}
