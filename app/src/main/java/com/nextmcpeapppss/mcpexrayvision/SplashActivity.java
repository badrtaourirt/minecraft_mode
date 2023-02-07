package com.nextmcpeapppss.mcpexrayvision;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryPerformance;
import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.OpenVPNThread;
import de.blinkt.openvpn.core.VpnStatus;
import kotlin.random.Random;

public class SplashActivity extends AppCompatActivity implements AdsManagerListener {
    AdsManager adsManager;
    DilatingDotsProgressBar mDilatingDotsProgressBar;

    boolean vpnStart = false;
    private Activity activity;
    private CheckInternetConnection connection;
    private OpenVPNThread vpnThread = new OpenVPNThread();
    private OpenVPNService vpnService = new OpenVPNService();
    private final int requestCodePermissionForNetworkAccess = 2000;
    private boolean activate_vpn;
    private String vpn_username;
    private String vpn_password;
    private String vpn_country;
    private String vpn_server;
    private int retry_vpn_counter = -1;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);

        DilatingDotsProgressBar dilatingDotsProgressBar = (DilatingDotsProgressBar) findViewById(R.id.progress);
        this.mDilatingDotsProgressBar = dilatingDotsProgressBar;
        dilatingDotsProgressBar.showNow();


        activity = this;
        new GetOutJsonData().execute();
    }

    private void goNextActivity() {
        adsManager = new AdsManager(this, this, this);
       /* new FlurryAgent.Builder()
                .withDataSaleOptOut(false)
                .withCaptureUncaughtExceptions(true)
                .withIncludeBackgroundSessionsInMetrics(true)
                .withPerformanceMetrics(FlurryPerformance.ALL)
                .build(this, Configs.FlurryKey); */
        adsManager.ConnectAPI();
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

    }

    @Override
    public void onIntInterstitialDismissed() {

    }

    @Override
    public void onNatAdFailed() {

    }

    @Override
    public void onNatAdLoaded() {

    }

    @SuppressLint("StaticFieldLeak")
    private class GetOutJsonData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(AdsManager.VPN_CONTROLLER_JSON_LINK);
            if (jsonStr != null) {
                try {
                    Log.e("IronDevMaVPN", "response = " + jsonStr);
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONObject vpn = jsonObj.getJSONObject("vpn");
                    activate_vpn = vpn.getBoolean("activate_vpn");
                    vpn_username = vpn.getString("vpn_username");
                    vpn_password = vpn.getString("vpn_password");
                    vpn_country = vpn.getString("vpn_country");
                    if(retry_vpn_counter == -1)
                        retry_vpn_counter = vpn.getInt("retry_vpn_counter");
                    else
                        retry_vpn_counter--;
                    JSONArray serversList = vpn.getJSONArray("serversList");
                    int randomIndex = Random.Default.nextInt(serversList.length());
                    String randomServer = serversList.getString(randomIndex);
                    vpn_server = sh.makeServiceCall(randomServer);
                }catch (Exception e){}
                finally {
                    /*
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            showToast("retry_vpn_counter: " + retry_vpn_counter);
                        }
                    });
                     */

                    if(activate_vpn && retry_vpn_counter > 0){
                        connection = new CheckInternetConnection();
                        // Checking is vpn already running or not
                        isServiceRunning();
                        VpnStatus.initLogCache(activity.getCacheDir());
                        prepareVpn();
                    }else{
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                goNextActivity();
                            }
                        });
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    /**
     * Prepare for vpn connect with required permission
     */
    private void prepareVpn() {
        if (!vpnStart) {
            if (getInternetStatus()) {
                // Checking permission for network monitor
                Intent intent = VpnService.prepare(getApplicationContext());

                if (intent != null) {
                    startActivityForResult(intent, requestCodePermissionForNetworkAccess);
                } else startVpn();//have already permission

            } else {

                // No internet connection available
                showToast("you have no internet connection !!");
            }

        }
		/*
		else if (stopVpn()) {
			// VPN is stopped, show a Toast message.
			showToast("Disconnect Successfully");
		}
		*/
    }

    /**
     * Show toast message
     * @param message: toast message
     */
    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Start the VPN
     */
    private void startVpn() {
        try {
            // .ovpn file
            String country = vpn_country;
            String username = vpn_username;
            String password = vpn_password;

            //showToast(vpn_server);

            OpenVpnApi.startVpn(getApplicationContext(), vpn_server, country, username, password);

            // Update log
            vpnStart = true;

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Internet connection status.
     */
    public boolean getInternetStatus() {
        return connection.netCheck(getApplicationContext());
    }

    /**
     * Get service status
     */
    public void isServiceRunning() {
        setStatus(OpenVPNService.getStatus());
    }

    /**
     * Status change with corresponding vpn connection status
     * @param connectionState
     */
    public void setStatus(String connectionState) {
        Log.d("ridoux_log", connectionState);
        if (connectionState!= null)
            switch (connectionState) {
                case "DISCONNECTED":
                    vpnStart = false;
                    OpenVPNService.setDefaultStatus();
                    break;
                case "CONNECTED":
                    vpnStart = true;// it will use after restart this activity
                    goNextActivity();
                    break;
                case "WAIT":
                    break;
                case "AUTH":
                    break;
                case "RECONNECTING":
                    break;
                case "NONETWORK":
                    break;
                case "AUTH_FAILED":
                    new GetOutJsonData().execute();
                    break;
            }

    }

    /**
     * Receive broadcast message
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                setStatus(intent.getStringExtra("state"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Stop vpn
     * @return boolean: VPN status
     */
    public static boolean stopVpn() {
        try {
            OpenVPNThread.stop();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Taking permission for network access
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == requestCodePermissionForNetworkAccess) {
            if (resultCode == RESULT_OK) {
                //Permission granted, start the VPN
                startVpn();
            } else {
                //showToast("Permission Deny !! ");
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        goNextActivity();
                    }
                });
            }
        }
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(activity).registerReceiver(broadcastReceiver, new IntentFilter("connectionState"));
        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

}
