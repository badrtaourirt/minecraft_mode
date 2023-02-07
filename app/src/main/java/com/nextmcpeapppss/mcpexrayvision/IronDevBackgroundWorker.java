package com.nextmcpeapppss.mcpexrayvision;


import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


public class IronDevBackgroundWorker extends Worker {

    Context appContext;
    static String LOG_TAG = "irondev_background_ads_worker";

    public IronDevBackgroundWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        appContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        return startWorker();
    }

    private Result startWorker() {
        Intent i = new Intent(appContext, WorkerInterstitialActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        appContext.startActivity(i);
        return Result.success();
    }

    interface RidouxCallBack{
        void done();
    }
}
