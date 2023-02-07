package com.nextmcpeapppss.mcpexrayvision;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;

public class WorkerInterstitialActivity extends AppCompatActivity {

    Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        activity = this;

        //Toast.makeText(getApplicationContext(), "WorkerInterstitialActivity Opened!", Toast.LENGTH_LONG).show();


        initApplovin(this, () -> loadAndShowApplovinInterstitial(activity));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void loadAndShowApplovinInterstitial(Activity activity) {
        String applovin_interstitial_ad_id = "94e793e03a825168";
        MaxInterstitialAd applovinInterstitialAd = new MaxInterstitialAd(applovin_interstitial_ad_id,
                activity
        );
        applovinInterstitialAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                Log.d(IronDevBackgroundWorker.LOG_TAG, "-> Applovin Interstitial is Ready");
                applovinInterstitialAd.showAd();
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                Log.d(IronDevBackgroundWorker.LOG_TAG, "-> Applovin Interstitial displayed successfully!");
                finish();
            }

            @Override
            public void onAdHidden(MaxAd ad) {
                Log.d(IronDevBackgroundWorker.LOG_TAG, "-> Applovin Interstitial closed.");
            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                Log.d(IronDevBackgroundWorker.LOG_TAG, "-> Failed to load Applovin Interstitial!");
                if (error != null) {
                    Log.d(IronDevBackgroundWorker.LOG_TAG, "-> Reason: " + error.getMessage());
                }
                finish();
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                Log.d(IronDevBackgroundWorker.LOG_TAG, "-> Failed to display Applovin Interstitial!");
                finish();
            }
        });
        // Load the first ad
        applovinInterstitialAd.loadAd();
    }

    private void initApplovin(Activity activity, IronDevBackgroundWorker.RidouxCallBack ridouxCallBack){
        Log.d(IronDevBackgroundWorker.LOG_TAG, "-> init Applovin");
        AppLovinSdk.getInstance( activity ).setMediationProvider("max");
        AppLovinSdk.getInstance( activity ).initializeSdk(config -> {
            // AppLovin SDK is initialized, start loading ads
            ridouxCallBack.done();
        });
    }

    @Override
    public void onBackPressed() {
        //Block back button
    }
}