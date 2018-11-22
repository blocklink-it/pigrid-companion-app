package de.blocklink.pigrid;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    public static final String URL = "url";
    private WebView myWebView;
    private ProgressBar pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_fullscreen);

        pd = findViewById(R.id.progressBar);
        pd.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
        myWebView = findViewById(R.id.webView);

        FloatingActionButton closePage = findViewById(R.id.close);
        closePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });

        this.setUpWebView();
    }

    private void setUpWebView(){
        String url =  getIntent().getStringExtra(URL);
        if(url != null){
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);

            myWebView.setWebViewClient(new FullscreenActivity.WebViewController());
            myWebView.loadUrl(url);

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

    public class WebViewController extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            pd.setVisibility(View.VISIBLE);
        }

        public void onPageFinished(WebView view, String url) {
            pd.setVisibility(View.GONE);
        }

        public void onReceivedError (WebView view, WebResourceRequest request, WebResourceError error){
            pd.setVisibility(View.GONE);
            Toast.makeText(FullscreenActivity.this, getString(R.string.page_load_error), Toast.LENGTH_LONG).show();
        }
    }
}
