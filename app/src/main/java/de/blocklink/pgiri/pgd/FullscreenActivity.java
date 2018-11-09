package de.blocklink.pgiri.pgd;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.widget.Button;
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
        myWebView = (WebView) findViewById(R.id.webView);

        FloatingActionButton closePage = (FloatingActionButton) findViewById(R.id.close);
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
            Toast.makeText(FullscreenActivity.this, "Error occurred while loading the page!!", Toast.LENGTH_LONG).show();
        }
    }
}
