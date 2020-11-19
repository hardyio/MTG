package com.yio.trade.mvp.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.StringUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.yio.mtg.trade.R;
import com.yio.trade.bean.WebConfigBean;
import com.yio.trade.common.Const;
import com.yio.trade.widgets.CustomWebView;

import butterknife.BindView;

public class PureWebActivity extends BaseActivity {

    private ValueCallback<Uri> uploadMessage;
    public ValueCallback<Uri[]> uploadMessageArr;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 101;
    private final static int READ_EXTERNAL_STORAGEREQUESTCODE = 10;

    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.webView)
    WebView webView;

    private boolean rewriteTitle;
    private boolean webBack;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_pure_web; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        ImmersionBar.with(this)
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.colorPrimary)
                .init();
        initTitle();
        initWebView();
        initNavigation();
        initParams();
    }

    private void initParams() {
        String json = getIntent().getStringExtra("json");
        WebConfigBean webConfigBean = GsonUtils.fromJson(json, WebConfigBean.class);
        String title = webConfigBean.getTitle();
        tvTitle.setText(title);
        boolean hasTitleBar = webConfigBean.isHasTitleBar();
        if (!hasTitleBar) {
            toolbar.setVisibility(View.GONE);
        }
        rewriteTitle = webConfigBean.isRewriteTitle();
        webBack = webConfigBean.isWebBack();
        String titleTextColor = webConfigBean.getTitleTextColor();
        //标题字体颜色
        tvTitle.setTextColor(Color.parseColor(titleTextColor));
        String stateBarTextColor = webConfigBean.getStateBarTextColor();
        BarUtils.setStatusBarLightMode(this, stateBarTextColor.equals("black"));
        String titleColor = webConfigBean.getTitleColor();
        toolbar.setBackgroundColor(Color.parseColor(titleColor));
        BarUtils.setStatusBarColor(this, Color.parseColor(titleColor), true);

        String url = webConfigBean.getUrl();
        String html = webConfigBean.getHtml();
        String postData = webConfigBean.getPostData();
        if (!TextUtils.isEmpty(html)) {
            webView.loadDataWithBaseURL("", html, "text/html", "utf-8", null);
        } else {
            if (!TextUtils.isEmpty(postData)) {
                webView.postUrl(url, postData.getBytes());
            } else {
                webView.loadUrl(url);
            }
        }
    }

    private void initNavigation() {
        ivLeft.setOnClickListener(v -> {
            if (webView.canGoBack() && webBack) {
                webView.goBack();
            } else {
                finish();
            }
        });
    }

    private void initTitle() {
        ivLeft.setImageResource(R.mipmap.ic_arrow_back_white_24dp);
        // 跑马灯必须加
        tvTitle.setSelected(true);
        tvTitle.setFocusable(true);
        tvTitle.setFocusableInTouchMode(true);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings webSetting = webView.getSettings();
        webSetting.setAllowFileAccess(true);
        // webSetting.setLayoutAlgorithm(IX5WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        //禁用缩放功能,不显示右下角的缩放按钮
//        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setSaveFormData(false);
        webSetting.setSavePassword(false);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAllowContentAccess(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        //开启 Application Caches 功能
        webSetting.setAppCacheEnabled(true);
        webView.setWebChromeClient(new ChromeClient());
        webView.setWebViewClient(new MyWebViewClient());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //点击回退键时，不会退出浏览器而是返回网页上一页
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack() && webBack) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CustomWebView.REQUEST_SELECT_FILE:
                if (uploadMessageArr == null)
                    return;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    uploadMessageArr.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                }
                uploadMessageArr = null;
                break;
            case CustomWebView.FILECHOOSER_RESULTCODE:
                if (null == uploadMessage)
                    return;
                // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
                // Use RESULT_OK only if you're implementing WebView inside an Activity
                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
                break;
            default:
                break;
        }
    }

    private class ChromeClient extends WebChromeClient {

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (tvTitle != null && !TextUtils.isEmpty(title) && rewriteTitle) {
                tvTitle.setText(title);
            }
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(PureWebActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //没有权限则申请权限
//                        choosefiletype =1;
//                        uploadMsgone = uploadMsg;
//                        sone = "File Chooser";
                    ActivityCompat.requestPermissions(PureWebActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGEREQUESTCODE);
                } else {
                    fileone(uploadMsg, "File Chooser");
                }
            } else {
                //小于6.0，不用申请权限，直接执行
                fileone(uploadMsg, "File Chooser");
            }

        }

        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(PureWebActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //没有权限则申请权限
//                        choosefiletype =1;
//                        uploadMsgone = uploadMsg;
//                        sone = "File Browser";
                    ActivityCompat.requestPermissions(PureWebActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGEREQUESTCODE);
                } else {
                    fileone(uploadMsg, "File Browser");
                }
            } else {
                //小于6.0，不用申请权限，直接执行
                fileone(uploadMsg, "File Browser");
            }

        }

        //For Android 4.1 only
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(PureWebActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //没有权限则申请权限
//                        choosefiletype =1;
//                        uploadMsgone = uploadMsg;
//                        sone = "File Browser";
                    ActivityCompat.requestPermissions(PureWebActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGEREQUESTCODE);
                } else {
                    fileone(uploadMsg, "File Browser");
                }
            } else {
                //小于6.0，不用申请权限，直接执行
                fileone(uploadMsg, "File Browser");
            }

        }

        // For Lollipop 5.0+ Devices
        @Override
        public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(PureWebActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //没有权限则申请权限
//                        choosefiletype = 2;
//                        filePathCallbacktwo = filePathCallback;
//                        fileChooserParamstwo = fileChooserParams;
                    ActivityCompat.requestPermissions(PureWebActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGEREQUESTCODE);
                    return false;
                } else {
                    return fileTwo(filePathCallback, fileChooserParams);
                }
            } else {
                //小于6.0，不用申请权限，直接执行
                return fileTwo(filePathCallback, fileChooserParams);
            }

        }

    }

    private boolean fileTwo(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        if (uploadMessageArr != null) {
            uploadMessageArr.onReceiveValue(null);
            uploadMessageArr = null;
        }
        uploadMessageArr = filePathCallback;
        try {
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                intent = fileChooserParams.createIntent();
            }
            startActivityForResult(intent, REQUEST_SELECT_FILE);
        } catch (ActivityNotFoundException e) {
            uploadMessageArr = null;
            Toast.makeText(getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    //    private ValueCallback<Uri> uploadMsgone;
//    private String sone;
    private void fileone(ValueCallback<Uri> uploadMsg, String s) {
        uploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, s), FILECHOOSER_RESULTCODE);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            String scheme = uri.getScheme();
            if (TextUtils.isEmpty(scheme)) return true;
            if (scheme.equals("nativeapi")) {
                //如定义nativeapi://showImg是用来查看大图，这里添加查看大图逻辑
                return true;
            } else if (scheme.equals("http") || scheme.equals("https")) {
                //处理http协议
                if (StringUtils.equals(Uri.parse(url).getHost(), Const.Url.WAN_ANDROID)) {
                    // 内部网址，不拦截，用自己的webview加载
                    return false;
                } else {
                    //跳转外部浏览器
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    return true;
                }
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }


        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            //回调发生在子线程中,不能直接进行UI操作
            return super.shouldInterceptRequest(view, url);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }

    }

}
