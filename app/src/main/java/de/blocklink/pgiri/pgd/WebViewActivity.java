package de.blocklink.pgiri.pgd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewActivity extends AppCompatActivity {

    public static final String ARG_URL = "piIP";
    private ProgressDialog pd;
    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        this.myWebView = (WebView) findViewById(R.id.webView);
        pd = ProgressDialog.show(this, "PGD", "Loading...", true);
        this.setUpWebView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                //pd.dismiss();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpWebView() {
        String piIP = getIntent().getStringExtra(ARG_URL);
        if (piIP != null) {
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);

            myWebView.setWebViewClient(new WebViewActivity.WebViewController());
            myWebView.loadUrl(piIP);
        }
    }

    public class WebViewController extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            pd.show();
        }

        public void onPageFinished(WebView view, String url) {
            if (pd.isShowing()) {
                pd.dismiss();
            }
        }

        public void onReceivedError (WebView view, WebResourceRequest request, WebResourceError error){
            pd.dismiss();
            Toast.makeText(WebViewActivity.this, "Error occurred while loading the page!!", Toast.LENGTH_LONG).show();
        }
    }
}
