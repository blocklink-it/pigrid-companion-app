package de.blocklink.pgiri.pgd;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * An activity representing a single Pie detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link PieListActivity}.
 */
public class PieDetailActivity extends AppCompatActivity {

    public static final String ARG_ITEM_ID = "piIP";
    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        this.myWebView =  (WebView) findViewById(R.id.webView);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.setUpWebView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, PieListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpWebView(){
        String piIP =  getIntent().getStringExtra(ARG_ITEM_ID);
        if(piIP != null){
            WebSettings webSettings = this.myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            this.myWebView.setWebViewClient(new PieDetailActivity.WebViewController());
            this.myWebView.loadUrl(piIP);
        }
    }

    public class WebViewController extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
