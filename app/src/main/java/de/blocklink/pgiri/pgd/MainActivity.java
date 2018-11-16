package de.blocklink.pgiri.pgd;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import de.blocklink.pgiri.pgd.Fragment.AboutFragment;
import de.blocklink.pgiri.pgd.Fragment.PiListFragment;
import de.blocklink.pgiri.pgd.Fragment.SettingFragment;
import de.blocklink.pgiri.pgd.Helper.PrefManager;
import de.blocklink.pgiri.pgd.Helper.UrlHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        prefManager = new PrefManager(this);

        FloatingActionButton searchPie = (FloatingActionButton) findViewById(R.id.search);
        searchPie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySelectedMenuPage(R.id.discoverPi);
            }
        });

        displaySelectedMenuPage(R.id.discoverPi);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            this.displaySelectedMenuPage(R.id.settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //calling the method displaySelectedMenuPage and passing the id of selected menu
        displaySelectedMenuPage(item.getItemId());
        return true;
    }

    private void displaySelectedMenuPage(int menuId) {
        Fragment fragment = null;

        if (menuId == R.id.discoverPi) {
            navigationView.getMenu().getItem(0).setChecked(true);
            fragment = new PiListFragment();

        } else if (menuId == R.id.piGridShop) {
            callWebView(UrlHelper.shopUrl);

        } else if (menuId == R.id.help) {
            callWebView(UrlHelper.helpUrl);

        } else if (menuId == R.id.piSpplier) {
            callWebView(UrlHelper.piSupplier);

        } else if (menuId == R.id.appHelp) {
            prefManager.setHomeBackBtnPressed(true);
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);

        } else if (menuId == R.id.about) {
            fragment = new AboutFragment();

        } else if (menuId == R.id.settings) {
            fragment = new SettingFragment();

        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void callWebView(String url) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(WebViewActivity.ARG_URL, url);
        startActivity(intent);
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
