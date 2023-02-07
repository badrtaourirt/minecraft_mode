package com.nextmcpeapppss.mcpexrayvision;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.sdk.AppLovinSdk;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.onesignal.OneSignal;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.mobileads.util.TestDeviceUtil;
import com.tradplus.ads.open.TradPlusSdk;
import com.tradplus.ads.open.banner.TPBanner;
import com.tradplus.ads.open.interstitial.InterstitialAdListener;
import com.tradplus.ads.open.interstitial.TPInterstitial;
import com.tradplus.ads.open.nativead.TPNative;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.banners.BannerView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AdsManager {

    public static String UpdateTo,OneSignalId,AdmobInt,AdmobNative,AdmobBanner,UnityGameId,ApplovinInt,ApplovinNative,ApplovinBanner,TradAdAppId,TradAdBanner,TradAdInter,TradAdNative;
    public static  int OnOff,SelectedNetwork;

    public Context context;
    public Activity activity;
    public String TAG = "AdsManager";
    public AdsManagerListener adslistner;

    //Admob
    private InterstitialAd mInterstitialAd;
    private FrameLayout adContainerView;
    public NativeAd AdmobNativeAd ;

    //TradePlus
    public TPBanner tpBanner;
    public ViewGroup adContainer;
    public TPInterstitial mTpInterstitial;
    public TPNative tpNative;
    public ViewGroup TradePlusNatContainer;

    //Applovin

    private MaxInterstitialAd interstitialAd;
    private int retryAttempt;
    private MaxAdView adView;
    private MaxNativeAdLoader nativeAdLoader;
    private MaxAd             nativeAd;

    //Unity
    BannerView UnityBanner;
    RelativeLayout UnityBannerView;
    private static AdsManager mInstance = null;

    // API Creditional
    private String DecryptionKey = "ot6dI3SiBZ6IbW5tPOtvJ1d0x2WgZB1F";
    private String AppPackage = BuildConfig.APPLICATION_ID;
    private String BASE_URL = "https://dailydoseofquotes.com/apps/app1.json";

    public static String VPN_CONTROLLER_JSON_LINK = "https://json.irondev.center/test/webview_vpn_controller.json";
    private long secondsRemaining;

    public AdsManager(Context _context, Activity _Activity, AdsManagerListener mAdslistner){
        activity = _Activity;
        context = _context;
        adslistner = mAdslistner;
    }

    public void ConnectAPI(){
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, BASE_URL,
                response -> {
                    try {
                        getData(response);
                    }catch (Exception e){
                        Intent i = new Intent(activity, MainActivity.class);
                        context.startActivity(i);
                    }
                }, error -> {
            Intent i = new Intent(activity, MainActivity.class);
            context.startActivity(i);
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("DECRYPTION_KEY", DecryptionKey);
                params.put("PACKAGENAME", AppPackage);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void getData(String response){
        try {
            JSONObject ApiData = new JSONObject(response);

            OneSignalId = ApiData.getString("OneSignalId");
            OnOff = ApiData.getInt("OnOff");
            UpdateTo = ApiData.getString("UpdateTo");

            TradAdAppId = ApiData.getString("TradAdAppId");
            TradAdNative = ApiData.getString("TradAdNative");
            TradAdInter = ApiData.getString("TradAdInter");
            TradAdBanner = ApiData.getString("TradAdBanner");


            AdmobInt = ApiData.getString("AdmobInt");
            AdmobNative = ApiData.getString("AdmobNative");
            AdmobBanner = ApiData.getString("AdmobBanner");

            UnityGameId = ApiData.getString("UnityGameId");

            ApplovinBanner = ApiData.getString("ApplovinBanner");
            ApplovinInt = ApiData.getString("ApplovinInt");
            ApplovinNative = ApiData.getString("ApplovinNative");

            SelectedNetwork = ApiData.getInt("SelectedNetwork");

            if(OnOff == 1){
                InitSdks(SelectedNetwork);
            }
            if(!OneSignalId.isEmpty()){
                OneSignal.initWithContext(context);
                OneSignal.setAppId(OneSignalId);
                OneSignal.promptForPushNotifications();
            }
            createTimer(3L);
        }catch (Exception e){
            Intent i = new Intent(activity, nonet.class);
            context.startActivity(i);
        }
    }

    public void createTimer(long j) {
        final TextView textView = (TextView) activity.findViewById(R.id.timer);
        new CountDownTimer(j * 1000, 1000L) {
            @Override
            public void onTick(long j2) {
                secondsRemaining = (j2 / 1000) + 1;
                TextView textView2 = textView;
                textView2.setText(activity.getString(R.string.splash_counter) + " " + secondsRemaining);
            }

            @Override
            public void onFinish() {
                secondsRemaining = 0L;
                textView.setText(activity.getString(R.string.done));
                android.app.Application application = activity.getApplication();
                activity.startActivity(new Intent(context, MainActivity.class));
                activity.finish();
            }
        }.start();
    }
    public void InitSdks(int Network){

        switch (Network){
            case 1:
                MobileAds.initialize(context);
                break;
            case 2:
                TestDeviceUtil.getInstance().setNeedTestDevice(true);
                TradPlusSdk.initSdk(context,TradAdAppId);
                break;
            case 3:
                // Make sure to set the mediation provider value to "max" to ensure proper functionality
                AppLovinSdk.getInstance(context).setMediationProvider( "max" );
                AppLovinSdk.initializeSdk(context);
                break;
            case 4:
                UnityAds.initialize(context, UnityGameId, true, new IUnityAdsInitializationListener() {
                    @Override
                    public void onInitializationComplete() {
                    }

                    @Override
                    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message){
                    }
                });
                break;
        }
    }

    //Main Functions

    public void LoadNative(){
        switch (SelectedNetwork){
            case 1:
                LoadAdmobNative();
                break;
            case 2:
                LoadTradPlusAdNative();
                break;
            case 3:
                LoadApplovinNative();
                break;
        }
    }
    public void LoadInt(){
        switch (SelectedNetwork){
            case 1:
                LoadAdmobInterstitial();
                break;
            case 2:
                LoadTradPlusAdInterstitial();
                break;
            case 3:
                LoadApplovinInterstitial();
                break;
            case 4:
                LoadUnityInt();
                break;
        }
    }

    public void ShowInterstitial(){
        switch (SelectedNetwork){
            case 1:
                ShowAdmobInterstitial();
                break;
            case 2:
                ShowTradPlusAdInterstitial();
                break;
            case 3:
                ShowApplovinInterstitial();
                break;
            case 4:
                ShowUnityInt();
                break;
        }
    }

    public void DestoryNative(){
        if(TradePlusNatContainer != null){
            tpNative.onDestroy();
        }
        if(nativeAdLoader != null){
            nativeAdLoader.destroy();
        }
        if(AdmobNativeAd != null){
            AdmobNativeAd.destroy();
        }
    }

    //Admob

    public void LoadAdmobInterstitial(){
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(context,AdmobInt, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded International");
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                adslistner.onIntInterstitialDismissed();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                                adslistner.onIntInterstitialFailed();
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                        adslistner.onNatAdFailed();
                    }
                });
    }
    public void ShowAdmobInterstitial(){
        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);
        } else {
            adslistner.onIntInterstitialFailed();
        }
    }
    public void LoadAdmobNative(){
        String AdmobNativeId = AdmobNative;
        AdLoader adLoader = new AdLoader.Builder(context, AdmobNativeId).forNativeAd(
                new NativeAd.OnNativeAdLoadedListener()  {
                    @Override
                    public void onNativeAdLoaded(NativeAd NativeAd) {
                        boolean isDestroyed = false;
                        if (isDestroyed || activity.isFinishing() || activity.isChangingConfigurations()) {
                            NativeAd.destroy();
                            return;
                        }
                        AdmobNativeAd = NativeAd;
                        FrameLayout frameLayout = activity.findViewById(R.id.AdmobNative);
                        NativeAdView AdmobadView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.admob_unified, null);
                        populateNativeAdView(NativeAd, AdmobadView);
                        frameLayout.removeAllViews();
                        frameLayout.addView(AdmobadView);
                        adslistner.onNatAdLoaded();
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {

                    }
                }).build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    public void populateNativeAdView(com.google.android.gms.ads.nativead.NativeAd nativeAd, com.google.android.gms.ads.nativead.NativeAdView myAdView) {
        myAdView.setMediaView(myAdView.findViewById(R.id.ad_media));
        myAdView.setHeadlineView(myAdView.findViewById(R.id.ad_headline));
        myAdView.setBodyView(myAdView.findViewById(R.id.ad_body));
        myAdView.setCallToActionView(myAdView.findViewById(R.id.ad_call_to_action));
        myAdView.setIconView(myAdView.findViewById(R.id.ad_app_icon));
        myAdView.setPriceView(myAdView.findViewById(R.id.ad_price));
        myAdView.setStarRatingView(myAdView.findViewById(R.id.ad_stars));
        myAdView.setStoreView(myAdView.findViewById(R.id.ad_store));
        myAdView.setAdvertiserView(myAdView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        ((TextView) myAdView.getHeadlineView()).setText(nativeAd.getHeadline());
        myAdView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            myAdView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            myAdView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) myAdView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            myAdView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            myAdView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) myAdView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            myAdView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) myAdView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            myAdView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            myAdView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            myAdView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) myAdView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            myAdView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            myAdView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) myAdView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            myAdView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) myAdView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            myAdView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            myAdView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) myAdView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            myAdView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        myAdView.setNativeAd(nativeAd);
    }

    //TradPlusAd

    public void LoadTradPlusAdInterstitial(){
        mTpInterstitial = new TPInterstitial(context,TradAdInter);
        mTpInterstitial.setAdListener(new InterstitialAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                adslistner.onIntAdLoaded();
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                //  Called when a rewarded ad is clicked.
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                // Called when a rewarded ad starts playing.
            }

            @Override
            public void onAdFailed(TPAdError tpAdError) {
                adslistner.onNatAdFailed();
            }


            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                adslistner.onIntInterstitialDismissed();
            }
            @Override
            public void onAdVideoError(TPAdInfo tpAdInfo, TPAdError tpAdError) {

            }
            @Override
            public void onAdVideoStart(TPAdInfo tpAdInfo) {

            }

            @Override
            public void onAdVideoEnd(TPAdInfo tpAdInfo) {

            }
        });

        mTpInterstitial.loadAd();
    }
    public void ShowTradPlusAdInterstitial(){
        if(mTpInterstitial != null && mTpInterstitial.isReady()){
            mTpInterstitial.showAd(activity,TradAdInter);
        }else{
            adslistner.onIntInterstitialFailed();
        }
    }
    public void LoadTradPlusAdNative(){
        TradePlusNatContainer = activity.findViewById(R.id.TradPlusNative);
        tpNative = new TPNative(context,TradAdNative);
        tpNative.setAdListener(new com.tradplus.ads.open.nativead.NativeAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                // Called when the ad for the given adUnitId has loaded.
                // Show an Ad
                tpNative.getNativeAd().showAd(TradePlusNatContainer, R.layout.tradeplusnative, "");
                adslistner.onNatAdLoaded();
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                // Called when a native ad is clicked.
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                // Called when a native ad starts playing.
            }

            @Override
            public void onAdShowFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {

            }

            @Override
            public void onAdLoadFailed(TPAdError tpAdError) {

            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                // Called when a native ad is closed.
            }
        });
        tpNative.loadAd();
    }

    // Applovin

    public void LoadApplovinInterstitial(){
        interstitialAd = new MaxInterstitialAd( ApplovinInt, activity);
        interstitialAd.setListener(new MaxAdListener(){
            @Override
            public void onAdLoaded(MaxAd ad) {
                retryAttempt = 0;
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {
                adslistner.onIntInterstitialDismissed();
            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                retryAttempt++;
                long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, retryAttempt ) ) );

                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        interstitialAd.loadAd();
                    }
                }, delayMillis );
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                adslistner.onIntInterstitialFailed();
            }
        });
        // Load the first ad
        interstitialAd.loadAd();
    }
    public void ShowApplovinInterstitial(){
        if ( interstitialAd.isReady() ){
            interstitialAd.showAd();
        }else{
            adslistner.onIntInterstitialFailed();
        }
    }
    public void LoadApplovinNative(){
        FrameLayout nativeAdContainer = activity.findViewById(R.id.applovinnative);
        nativeAdLoader = new MaxNativeAdLoader( ApplovinNative, context );
        nativeAdLoader.setNativeAdListener( new MaxNativeAdListener()
        {
            @Override
            public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad)
            {
                // Clean up any pre-existing native ad to prevent memory leaks.
                if ( nativeAd != null )
                {
                    nativeAdLoader.destroy( nativeAd );
                }
                // Save ad for cleanup.
                nativeAd = ad;
                // Add ad view to view.
                nativeAdContainer.removeAllViews();
                nativeAdContainer.addView( nativeAdView );
            }

            @Override
            public void onNativeAdLoadFailed(final String adUnitId, final MaxError error)
            {

            }

            @Override
            public void onNativeAdClicked(final MaxAd ad)
            {
                // Optional click callback
            }
        } );

        nativeAdLoader.loadAd();
    }
    //Unity

    public void LoadUnityInt(){
        UnityAds.load("Interstitial_Android", loadListener);
    }
    public void ShowUnityInt(){
        UnityAds.show((Activity)activity, "Interstitial_Android", new UnityAdsShowOptions(), showListener);
    }

    private IUnityAdsLoadListener loadListener = new IUnityAdsLoadListener() {
        @Override
        public void onUnityAdsAdLoaded(String placementId) {

        }

        @Override
        public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
            adslistner.onNatAdFailed();
        }
    };

    private IUnityAdsShowListener showListener = new IUnityAdsShowListener() {
        @Override
        public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
            adslistner.onIntInterstitialFailed();
        }

        @Override
        public void onUnityAdsShowStart(String placementId) {
        }

        @Override
        public void onUnityAdsShowClick(String placementId) {

        }

        @Override
        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
            adslistner.onIntInterstitialDismissed();
        }
    };
}

