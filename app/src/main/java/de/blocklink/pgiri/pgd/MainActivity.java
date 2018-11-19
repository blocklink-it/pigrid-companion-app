package de.blocklink.pgiri.pgd;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import de.blocklink.pgiri.pgd.Fragment.AboutFragment;
import de.blocklink.pgiri.pgd.Fragment.PiListFragment;
import de.blocklink.pgiri.pgd.Helper.PrefManager;
import de.blocklink.pgiri.pgd.Helper.UrlHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView; // for navigation menu
    private PrefManager prefManager; // for handling app help

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        prefManager = new PrefManager(this);

        FloatingActionButton searchPie = findViewById(R.id.search);
        searchPie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySelectedMenuPage(R.id.discoverPi);
            }
        });

        displaySelectedMenuPage(R.id.discoverPi); // call and opens the PiListFragment as default page
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //calling the method displaySelectedMenuPage and passing the id of selected menu
        displaySelectedMenuPage(id);
        return true;
    }

    // function to open fragment or Activity according to menu selected
    private void displaySelectedMenuPage(int menuId) {
        Fragment fragment = null;

        if (menuId == R.id.discoverPi) {
            fragment = new PiListFragment();

        } else if (menuId == R.id.piGridShop) {
            callWebView(UrlHelper.shopUrl);

        } else if (menuId == R.id.help) {
            callWebView(UrlHelper.helpUrl);

        } else if (menuId == R.id.piSpplier) {
            callFullScreenWebView();

        } else if (menuId == R.id.appHelp) {
            prefManager.setHomeBackBtnPressed(true);
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);

        } else if (menuId == R.id.about) {
            fragment = new AboutFragment();

        } /*else if (menuId == R.id.settings) {
            fragment = new SettingFragment();

        }*/

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    // function to call the activity which loads the eb view inside app
    private void callWebView(String url) {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(WebViewActivity.ARG_URL, url);
        startActivity(intent);
    }

    // function to call Full Screen Activity which opens web view on full page
    private void callFullScreenWebView() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        Intent intent = new Intent(this, FullscreenActivity.class);
        intent.putExtra(FullscreenActivity.URL, UrlHelper.piSupplier);
        startActivity(intent);
    }

    // function accessbile from fragments to set the title bar
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    // function to set the discover Pi menu selected in nav drawer
    public void setMenuSelected() {
        navigationView.getMenu().getItem(0).setChecked(true); // discover pi is in first index
    }
}
