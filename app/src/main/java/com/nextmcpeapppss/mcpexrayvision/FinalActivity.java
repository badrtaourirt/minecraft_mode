package com.nextmcpeapppss.mcpexrayvision;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import com.suddenh4x.ratingdialog.AppRating;
import com.suddenh4x.ratingdialog.preferences.RatingThreshold;

import org.json.JSONException;

import java.io.File;
import java.util.Objects;

public class FinalActivity extends AppCompatActivity implements AdsManagerListener {
    AdsManager adsManager;
    private static final String TAG = "MEOW";
    public Dialog waitingads;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_final);
        getSupportActionBar().hide();
        adsManager = new AdsManager(this, this, this);
        if(adsManager.OnOff == 1) {
            adsManager.LoadInt();
            adsManager.LoadNative();


            new AppRating.Builder(this)
                    .setMinimumLaunchTimes(0)
                    .setMinimumDays(0)
                    .setMinimumLaunchTimesToShowAgain(0)
                    .setMinimumDaysToShowAgain(0)
                    .setRatingThreshold(RatingThreshold.FOUR)
                    .setCancelable(true)
                    .useGoogleInAppReview()
                    .showIfMeetsConditions();
        }

}

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void go_to_bonus(View view) {
        if(adsManager.OnOff == 1) {
            waitingads = new Dialog(FinalActivity.this);
            waitingads.setContentView(R.layout.waitads_dialog);
            ((Window) Objects.requireNonNull(waitingads.getWindow())).setBackgroundDrawable(new ColorDrawable(0));
            waitingads.setCanceledOnTouchOutside(false);
            waitingads.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adsManager.ShowInterstitial();
                }
            }, 700);
        }else{
            Intent i = new Intent(this, bonusactivity.class);
            startActivity(i);
            finish();
        }
    }

    public void onButtonClick(View view) {
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+  Configs.MOD_FILE_NAME;
        String str = Configs.MOD_FILE_BONUS_NAME.contains("mcpack") ? "application/mcpack" : "application/mcworld";
        Intent intent1 = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(FinalActivity.this, BuildConfig.APPLICATION_ID+".provider", new File(filePath));
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
        Intent i = new Intent(FinalActivity.this, DownloadActivity.class);
        startActivity(i);
        finish();
        adsManager.DestoryNative();
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
