package com.robo.instant.instantapplication.feature;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.robo.instant.instantapplication.feature.common.util.NetworkUtil;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private static final String WEB_VIEW_URL = "https://www.buzzfeed.com/tasty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        Intent appLinkIntent = getIntent();

        if (appLinkIntent != null && appLinkIntent.getData() != null)
            handleIntent(appLinkIntent);
        else
            loadWebView(WEB_VIEW_URL);
    }

    private void initView() {

        mWebView = (WebView) findViewById(R.id.web_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    private void handleIntent(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
            loadWebView(appLinkData.toString());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void loadWebView(String url) {
        if (NetworkUtil.isAvailable(this)) {
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
            mWebView.setWebChromeClient(new WebChromeClient());
            mWebView.setFocusable(false);
            mWebView.setFocusableInTouchMode(false);
            mWebView.setLongClickable(false);

            mWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    mProgressBar.setVisibility(View.GONE);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    view.loadUrl(request.getUrl().getPath());
                    return true;
                }

            });

            mWebView.loadUrl(url);
        } else
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
    }

    private void clearWebView() {
        if (mWebView != null) {
            mWebView.stopLoading();
            mWebView.loadUrl("about:blank");
            mWebView.reload();
            mWebView.clearCache(true);
            mWebView = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearWebView();
    }
}

