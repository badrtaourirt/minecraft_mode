package com.nextmcpeapppss.mcpexrayvision;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import org.json.JSONException;

import java.util.Objects;

public class notice extends AppCompatActivity implements AdsManagerListener {
    AdsManager adsManager;
    public ImageView image;
    public String image_url;
    public Dialog waitingads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        getSupportActionBar().hide();


        adsManager = new AdsManager(this, this, this);
        if(adsManager.OnOff == 1) {
            adsManager.LoadInt();
            adsManager.LoadNative();
        }

        this.image = (ImageView) findViewById(R.id.image);
        this.image_url = Configs.MOD_IMAGE;
        Glide.with((FragmentActivity) this).load(this.image_url).thumbnail(Glide.with((FragmentActivity) this).load(Integer.valueOf((int) R.drawable.spinner))).fitCenter().transition(DrawableTransitionOptions.withCrossFade()).into(this.image);


        ((Button) findViewById(R.id.btnstartmod)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adsManager.OnOff == 1) {
                    waitingads = new Dialog(notice.this);
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
                    Intent i = new Intent(notice.this, DetailActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(notice.this, MainActivity.class);
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
        Intent i = new Intent(this, DetailActivity.class);
        startActivity(i);
        finish();
        adsManager.DestoryNative();
    }

    @Override
    public void onIntInterstitialDismissed() {
        Intent i = new Intent(this, DetailActivity.class);
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