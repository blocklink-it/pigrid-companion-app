package de.blocklink.pigrid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Make sure this is before calling super.onCreate
        setTheme(R.style.AppTheme); // setup Theme for launcher
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, WelcomeActivity.class); // calls main activity after app launches
        startActivity(intent);
        finish();
    }
}
