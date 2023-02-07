package com.nextmcpeapppss.mcpexrayvision;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;


import org.json.JSONException;

import java.io.File;

public class bonusfinal extends AppCompatActivity implements AdsManagerListener {
    AdsManager adsManager;
    private static final String TAG = "MEOW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonusfinal);
        getSupportActionBar().hide();
        adsManager = new AdsManager(this, this, this);
        if(adsManager.OnOff == 1) {
            adsManager.LoadNative();
        }
    }

    public void onButtonClick(View view) {
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+  Configs.MOD_FILE_BONUS_NAME;
        String str = Configs.MOD_FILE_BONUS_NAME.contains("mcpack") ? "application/mcpack" : "application/mcworld";
        Intent intent1 = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(bonusfinal.this, BuildConfig.APPLICATION_ID+".provider", new File(filePath));
        intent1.setDataAndType(uri,str);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent1);
        } catch (Exception e) {
            Log.e(TAG, "ERRORKA: " + e);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.error).setMessage(R.string.error_content).setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(bonusfinal.this, bonusdownload.class);
        startActivity(i);
        finish();
        adsManager.DestoryNative();
    }
    public void onButtonClickRate(View view) {
        final String rateapp = getPackageName();
        Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + rateapp));
        startActivity(intent1);
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
        Intent i = new Intent(this, bonusactivity.class);
        startActivity(i);
        finish();
        adsManager.DestoryNative();
    }

    @Override
    public void onIntInterstitialDismissed() {
        Intent i = new Intent(this, bonusactivity.class);
        startActivity(i);
        finish();
        adsManager.DestoryNative();
    }

    @Override
    public void onNatAdFailed() {

    }

    @Override
    public void onNatAdLoaded() {

    }
}