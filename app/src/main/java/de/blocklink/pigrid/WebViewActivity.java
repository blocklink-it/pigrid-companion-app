package de.blocklink.pigrid;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WebViewActivity extends AppCompatActivity {

    public static final String ARG_URL = "url"; // url to pass to load on Webview
    private WebView myWebView;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar); // adds back button

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        this.myWebView = findViewById(R.id.webView);
        pb = findViewById(R.id.pBar);
        // changes the custom color of the spinner
        pb.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
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

    // function to setup zooming control
    private void setUpWebView() {
        String piIP = getIntent().getStringExtra(ARG_URL);
        if (piIP != null) {
            WebSettings webSettings = myWebView.getSettings(); // gets web settings
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

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            /*  String url = request.getUrl().toString();*/
            return false;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            pb.setVisibility(View.VISIBLE); // shows loading spinner
        }

        public void onPageFinished(WebView view, String url) {
            pb.setVisibility(View.GONE);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // Handle the error
            pb.setVisibility(View.GONE); // hides spinner
            Toast.makeText(WebViewActivity.this, getString(R.string.page_load_error), Toast.LENGTH_LONG).show();
        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
            pb.setVisibility(View.GONE); // hides spinner
            onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            Toast.makeText(WebViewActivity.this, getString(R.string.page_load_error), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        // Check if the key event was the forward button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_FORWARD) && myWebView.canGoForward()) {
            myWebView.goForward();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
}
