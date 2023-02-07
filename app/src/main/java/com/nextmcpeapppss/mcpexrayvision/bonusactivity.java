package com.nextmcpeapppss.mcpexrayvision;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.kobakei.ratethisapp.RateThisApp;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class bonusactivity extends AppCompatActivity implements AdsManagerListener {
    AdsManager adsManager;
    private TextView downloads;
    private TextView views;
    private TextView version;
    private TextView title;
    private TextView description;
    SliderView sliderView;
    private List<SliderItem> sliderItemList = new ArrayList();
    private GalleryAdapter GalleryAdapter = new GalleryAdapter(this, this.sliderItemList);
    public Dialog waitingads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonusactivity);

        getSupportActionBar().hide();
        adsManager = new AdsManager(this, this, this);
        if(adsManager.OnOff == 1) {
            adsManager.LoadInt();
            adsManager.LoadNative();
        }

        RateThisApp.Config config = new RateThisApp.Config(1, 1);
        config.setTitle(R.string.rate_title);
        config.setMessage(R.string.rate_message);
        config.setYesButtonText(R.string.rate_rate);
        config.setNoButtonText(R.string.rate_thanks);
        config.setCancelButtonText(R.string.rate_cancel);
        RateThisApp.init(config);
        RateThisApp.onCreate(this);
        new Random().nextInt(10);


        // Fill random Info
        downloads = (TextView) findViewById(R.id.downloads);
        Random random = new Random();
        int randomNumber = random.nextInt(10000- 1000) + 1000;
        downloads.setText(Integer.toString(randomNumber));
        views = (TextView) findViewById(R.id.views);
        views.setText(Integer.toString(new Random().nextInt((19412 - 1233) + 1) + 1233));
        version = (TextView) findViewById(R.id.version);
        version.setText(Configs.MOD_BONUS_VERSION);
        title = (TextView) findViewById(R.id.title);
        title.setText(Configs.MOD_BONUS_NAME);
        description = (TextView) findViewById(R.id.description);

        if (Build.VERSION.SDK_INT >= 24) {
            description.setText(Html.fromHtml(Configs.BonusDescription,1));
        } else {
            description.setText(Html.fromHtml(Configs.BonusDescription));
        }

        SliderView sliderView = (SliderView) findViewById(R.id.imageSlider);
        this.sliderView = sliderView;
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        this.sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        this.sliderView.setAutoCycleDirection(2);
        this.sliderView.setIndicatorSelectedColor(-1);
        this.sliderView.setIndicatorUnselectedColor(-7829368);
        this.sliderView.setScrollTimeInSec(3);
        this.sliderView.setAutoCycle(true);
        this.sliderView.startAutoCycle();
        SliderItem sliderItem = new SliderItem();
        sliderItem.setImageUrl(Configs.BONUS_MOD_IMAGE);
        this.sliderItemList.add(sliderItem);
        this.GalleryAdapter.notifyDataSetChanged();
        this.sliderView.setSliderAdapter(this.GalleryAdapter);
    }

    public void download_click(View view) {
        if(adsManager.OnOff == 1) {
            waitingads = new Dialog(bonusactivity.this);
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
            Intent i = new Intent(this, bonusdownload.class);
            startActivity(i);
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(bonusactivity.this, FinalActivity.class);
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
            Intent i = new Intent(this, bonusdownload.class);
        startActivity(i);
        finish();
        adsManager.DestoryNative();
    }

    @Override
    public void onIntInterstitialDismissed() {
            Intent i = new Intent(this, bonusdownload.class);
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