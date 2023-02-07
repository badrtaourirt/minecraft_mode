package com.nextmcpeapppss.mcpexrayvision;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import org.json.JSONException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class bonusdownload extends AppCompatActivity implements AdsManagerListener {
    AdsManager adsManager;
    public Button popup_btn;
    public TextView popup_percent;
    public TextView popup_speed;
    public TextView popup_text;
    public ProgressBar progressBar;
    public Dialog downloadPopUp;
    public ImageView image;
    public String image_url;
    public Dialog waitingads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonusdownload);
        getSupportActionBar().hide();

        adsManager = new AdsManager(this, this, this);
        if(adsManager.OnOff == 1) {
            adsManager.LoadInt();
            adsManager.LoadNative();
        }

        Dialog dialog = new Dialog(this);
        this.downloadPopUp = dialog;
        dialog.setContentView(R.layout.download_window);
        this.downloadPopUp.setCanceledOnTouchOutside(false);
        this.downloadPopUp.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        this.downloadPopUp.getWindow().setLayout(-1, -2);
        this.popup_text = (TextView) this.downloadPopUp.findViewById(R.id.popup_text);
        this.popup_btn = (Button) this.downloadPopUp.findViewById(R.id.popup_btn);
        this.progressBar = (ProgressBar) this.downloadPopUp.findViewById(R.id.progressBar);
        this.popup_percent = (TextView) this.downloadPopUp.findViewById(R.id.percent);
        this.popup_speed = (TextView) this.downloadPopUp.findViewById(R.id.speed);

        this.image = (ImageView) findViewById(R.id.image);
        this.image_url = Configs.BONUS_MOD_IMAGE;
        Glide.with((FragmentActivity) this).load(this.image_url).thumbnail(Glide.with((FragmentActivity) this).load(Integer.valueOf((int) R.drawable.spinner))).fitCenter().transition(DrawableTransitionOptions.withCrossFade()).into(this.image);


    }
    public void onButtonClick(View view) {
        this.downloadPopUp.show();
        this.popup_text.setText(R.string.downloading);
        this.popup_btn.setEnabled(false);
        this.popup_btn.setBackground(getResources().getDrawable(R.drawable.btn_border_inactive));

        ValueAnimator animator = ValueAnimator.ofInt(0, progressBar.getMax());
        animator.setDuration(8000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation){
                progressBar.setProgress((Integer)animation.getAnimatedValue());
                popup_percent.setText(Integer.toString((Integer)animation.getAnimatedValue()) + "%");
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                copyAssets(bonusdownload.this,Configs.MOD_FILE_BONUS_NAME);
                bonusdownload.this.popup_btn.setEnabled(true);
                bonusdownload.this.popup_btn.setBackground(bonusdownload.this.getResources().getDrawable(R.drawable.btn_border));
                bonusdownload.this.popup_btn.setText(R.string.next);
                popup_text.setText(R.string.downloaded);
                // start your activity here
            }
        });
        animator.start();
    }

    public void onNextButtonClick(View view) {
        if (this.downloadPopUp.isShowing()) {
            this.downloadPopUp.dismiss();
        }
        if(adsManager.OnOff == 1) {
            waitingads = new Dialog(bonusdownload.this);
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
            Intent i = new Intent(this, bonusfinal.class);
            startActivity(i);
            finish();
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(bonusdownload.this, bonusactivity.class);
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
            Intent i = new Intent(this, bonusfinal.class);
        startActivity(i);
        finish();
        adsManager.DestoryNative();
    }

    @Override
    public void onIntInterstitialDismissed() {
            Intent i = new Intent(this, bonusfinal.class);
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

    public static void copyAssets(Context context, String filename) {
        AssetManager assetManager = context.getAssets();

        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open("mods/" + filename);
            out = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/"+ filename);
            copyFile(in, out);
        } catch(IOException e) {
            Log.e("tag", "Failed to copy asset file: " + filename, e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                    in = null;
                } catch (IOException e) {

                }
            }
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                    out = null;
                } catch (IOException e) {

                }
            }
        }

    }
    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}