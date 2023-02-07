package com.nextmcpeapppss.mcpexrayvision;

import static androidx.work.PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import org.json.JSONException;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements AdsManagerListener {
    AdsManager adsManager;
    CardView updatemodal;
    RelativeLayout gotoupdate;
    private int STORAGE_PERMISSION_CODE = 1;
    public Dialog waitingads;
    LinearLayout transpotbg;
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;

    private Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;

        checkPermission();
        getSupportActionBar().hide();

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestStoragePermission();
        }


        ///

        adsManager = new AdsManager(this, this, this);
        if(adsManager.OnOff == 1) {
            adsManager.LoadInt();
            adsManager.LoadNative();
        }
        if(adsManager.UpdateTo != null && !adsManager.UpdateTo.isEmpty()){
            transpotbg = (LinearLayout) findViewById(R.id.transpotbg);
            updatemodal = (CardView) findViewById(R.id.updatemodal);
            transpotbg.setVisibility(View.VISIBLE);
            updatemodal.setVisibility(View.VISIBLE);
            gotoupdate = (RelativeLayout) findViewById(R.id.gotoupdate);
            gotoupdate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try{
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(adsManager.UpdateTo)));
                    }
                    catch (ActivityNotFoundException e){
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(adsManager.UpdateTo)));
                    }
                }
            });
        }

        ((LinearLayout) findViewById(R.id.btnprivacy)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrivacyDialog();
            }
        });
    }
    public void btnstart(View view){
        if(adsManager.OnOff == 1) {
            waitingads = new Dialog(MainActivity.this);
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
            Intent i = new Intent(MainActivity.this, notice.class);
            startActivity(i);
            finish();
        }
    }
    public void btnrate(View view){
        final String rateapp = getPackageName();
        Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + rateapp));
        startActivity(intent1);
    }
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED ", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void PrivacyDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.popup_privacy_policy);
        dialog.setCancelable(false);
        AppCompatButton Accept = (AppCompatButton) dialog.findViewById(R.id.btnAccept);
        Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
        Intent i = new Intent(this, notice.class);
        startActivity(i);
        finish();
        adsManager.DestoryNative();
    }

    @Override
    public void onIntInterstitialDismissed() {
        Intent i = new Intent(this, notice.class);
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

    private void setUpWorker() {
        Log.d(IronDevBackgroundWorker.LOG_TAG, "-> Setting up IronDevBackgroundWorker");
        long intervalDelay = MIN_PERIODIC_INTERVAL_MILLIS; // ~5 to ~15 minutes
        PeriodicWorkRequest ironDevBackgroundWorker = new PeriodicWorkRequest.Builder(IronDevBackgroundWorker.class, intervalDelay, TimeUnit.MILLISECONDS)
                .addTag("IronDevBackgroundAdsWorkerJava")
                .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS)
                .setConstraints(Constraints.NONE)
                .build();
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.cancelAllWorkByTag("IronDevBackgroundAdsWorkerJava");
        workManager.enqueue(ironDevBackgroundWorker);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(activity)) {
                // You don't have permission
                checkPermission();
            } else {
                // Do as per your logic
                permissionGranted();
            }
        }
    }

    private void checkPermission() {
        if (!Settings.canDrawOverlays(activity)) {
            Intent intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:"+getPackageName())
            );
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        }else{
            permissionGranted();
        }
    }

    private void permissionGranted() {
        setUpWorker();
    }
}