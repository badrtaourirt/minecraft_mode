package com.nextmcpeapppss.mcpexrayvision;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.kobakei.ratethisapp.RateThisApp;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DetailActivity extends AppCompatActivity implements AdsManagerListener {
    AdsManager adsManager;
    ImageView closeButton;
    private Dialog infoPopUp;
    private Dialog ratePopUp;
    private Button rate_btn;
    private Button rate_popup_btn;
    private RatingBar ratingBar;
    private TextView downloads;
    private TextView views;
    private TextView version;
    private TextView title;
    private TextView description;
    SliderView sliderView;
    public Dialog waitingads;

    private List<SliderItem> sliderItemList = new ArrayList();
    private GalleryAdapter GalleryAdapter = new GalleryAdapter(this, this.sliderItemList);
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().hide();
        adsManager = new AdsManager(this, this, this);
        if(adsManager.OnOff == 1) {
            adsManager.LoadInt();
            adsManager.LoadNative();
        }

        this.closeButton = (ImageView) findViewById(R.id.popup_close);
        Dialog dialog = new Dialog(this);
        this.infoPopUp = dialog;
        dialog.setContentView(R.layout.popup_window);
        this.infoPopUp.setCanceledOnTouchOutside(true);
        this.infoPopUp.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        this.infoPopUp.getWindow().setLayout(-1, -2);
        Dialog dialog2 = new Dialog(this);
        this.ratePopUp = dialog2;
        dialog2.setContentView(R.layout.rate_popup_window);
        this.ratePopUp.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        this.rate_btn = (Button) findViewById(R.id.rate_btn);
        this.rate_popup_btn = (Button) this.ratePopUp.findViewById(R.id.rate_popup_btn);
        RatingBar ratingBar = (RatingBar) this.ratePopUp.findViewById(R.id.ratingBar);
        this.ratingBar = ratingBar;
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar2, float f, boolean z) {
            }
        });
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
        version.setText(Configs.MOD_VERSION);
        title = (TextView) findViewById(R.id.title);
        title.setText(Configs.MOD_NAME);
        description = (TextView) findViewById(R.id.description);

        if (Build.VERSION.SDK_INT >= 24) {
            description.setText(Html.fromHtml(Configs.Description,1));
        } else {
            description.setText(Html.fromHtml(Configs.Description));
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
        sliderItem.setImageUrl(Configs.MOD_IMAGE);
        this.sliderItemList.add(sliderItem);
        this.GalleryAdapter.notifyDataSetChanged();
        this.sliderView.setSliderAdapter(this.GalleryAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(DetailActivity.this, notice.class);
        startActivity(i);
        finish();
        adsManager.DestoryNative();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void download_click(View view) {
        this.infoPopUp.show();
    }

    public void onPopupButtonClick(View view) {
        if (this.infoPopUp.isShowing()) {
            this.infoPopUp.dismiss();
        }
        if(adsManager.OnOff == 1) {
            waitingads = new Dialog(DetailActivity.this);
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
            Intent i = new Intent(this, DownloadActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void onRateButtonClick(View view) {
        this.ratePopUp.show();
        this.ratePopUp.getWindow().setLayout(-1, -2);
    }

    public void onRatePopupButtonClick(View view) {
        if (this.ratePopUp.isShowing()) {
            this.ratePopUp.dismiss();
        }
        this.rate_btn.setText(getBaseContext().getString(R.string.vote_counted));
        this.rate_btn.setEnabled(false);
        Toast.makeText(this, getBaseContext().getString(R.string.vote_thanks), Toast.LENGTH_LONG).show();
    }

    public void onCloseClick(View view) {
        if (infoPopUp.isShowing()) {
            infoPopUp.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
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
        Intent i = new Intent(this, DownloadActivity.class);
        startActivity(i);
        finish();
        adsManager.DestoryNative();
    }

    @Override
    public void onIntInterstitialDismissed() {
        Intent i = new Intent(this, DownloadActivity.class);
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
