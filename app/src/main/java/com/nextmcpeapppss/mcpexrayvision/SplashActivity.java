package com.nextmcpeapppss.mcpexrayvision;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryPerformance;
import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

import org.json.JSONException;

public class SplashActivity extends AppCompatActivity implements AdsManagerListener {
    AdsManager adsManager;
    DilatingDotsProgressBar mDilatingDotsProgressBar;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);

        DilatingDotsProgressBar dilatingDotsProgressBar = (DilatingDotsProgressBar) findViewById(R.id.progress);
        this.mDilatingDotsProgressBar = dilatingDotsProgressBar;
        dilatingDotsProgressBar.showNow();

        adsManager = new AdsManager(this, this, this);

       /* new FlurryAgent.Builder()
                .withDataSaleOptOut(false)
                .withCaptureUncaughtExceptions(true)
                .withIncludeBackgroundSessionsInMetrics(true)
                .withPerformanceMetrics(FlurryPerformance.ALL)
                .build(this, Configs.FlurryKey); */
        adsManager.ConnectAPI();
    }

    @Override
    public void onBanAdFailed() throws JSONException {

    }

    @Override
    public void onBanAdLoaded() {

    }

    @Override
    public void onIntAdFailed() {

    }

    @Override
    public void onIntAdLoaded() {

    }

    @Override
    public void onIntInterstitialFailed() {

    }

    @Override
    public void onIntInterstitialDismissed() {

    }

    @Override
    public void onNatAdFailed() {

    }

    @Override
    public void onNatAdLoaded() {

    }
}
