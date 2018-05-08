package com.adhestudio.newseuspeshaderformcpe;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;


public class MainActivity extends AppCompatActivity
{
    private InterstitialAd interstitial;

    private Activity mActivity;
    private Context mContext;

    private RelativeLayout mRootLayout;
    private WebView view;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();
        mActivity = MainActivity.this;

        mRootLayout = findViewById(R.id.root_layout);
        view = findViewById(R.id.view);

        AdView adView = findViewById(R.id.banner);
        adView.loadAd(new AdRequest.Builder().build());

        checkPermission();

        String url = "file:///android_asset/dashboard.html";
        view.loadUrl(url);
        view.getSettings().setJavaScriptEnabled(true);
        view.loadUrl("javascript:clickFunction()");
        view.setWebViewClient(new WebViewClient()
        {
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:clickFunction()");
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        interstitial = new InterstitialAd(MainActivity.this);
        interstitial.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitial.loadAd(adRequest);
        interstitial.setAdListener(new AdListener()
                                   {
                                       @Override
                                       public void onAdLoaded() {
                                           runOnUiThread(new Runnable() {
                                               @Override
                                               public void run() {
                                                   if (interstitial.isLoaded())
                                                   {
                                                       interstitial.show();
                                                   }
                                               }
                                           });
                                       }
                                   }
        );

        view.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDescription, String mimetype, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                String fileName = URLUtil.guessFileName(url, contentDescription, mimetype);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                DownloadManager dManageer = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dManageer.enqueue(request);
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (view.canGoBack()) {
                        view.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    protected void checkPermission()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    // show an alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setMessage("Write external storage permission is required.");
                    builder.setTitle("Please grant permission");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CONTEXT_INCLUDE_CODE);
                        }
                    });
                    builder.setNeutralButton("Cancel",null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CONTEXT_INCLUDE_CODE);
                }
            }else {
                // Permission already granted
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch(requestCode){
            case CONTEXT_INCLUDE_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // Permission granted
                }else {
                    // Permission denied
                }
            }
        }
    }

    public void loadInterstitial()
    {

    }

//    public void loadInterstitial() {
//        hitung++;
//        if (loadingIklan){
//            mInterstitialAd = new InterstitialAd(this);
//            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
//            AdRequest adRequest = new AdRequest.Builder()
//                    .setRequestAgent("android_studio:ad_template").build();
//            mInterstitialAd.loadAd(adRequest);
//            loadingIklan=false;
//        }
//        if (hitung==0){
//            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
//                mInterstitialAd.show();
//                loadingIklan=true;
//            }
//        }
//    }
}
