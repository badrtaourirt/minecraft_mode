package com.nextmcpeapppss.mcpexrayvision;

import org.json.JSONException;

public interface AdsManagerListener {
    void onBanAdFailed() throws JSONException;
    void onBanAdLoaded();

    void onIntAdFailed();
    void onIntAdLoaded();
    void onIntInterstitialFailed();
    void onIntInterstitialDismissed();

    void onNatAdFailed();
    void onNatAdLoaded();
}
