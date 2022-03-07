package com.ex.master;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.webkit.CookieManager;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.ex.master.util.LogUtil;
import com.google.android.gms.common.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class DownloadThread extends Thread {
    public static String TAG = DownloadThread.class.getSimpleName();

    Context context;
    String mUrl = "";
    String userAgent = "";
    String fileName = "";
    String mineType = "";
    Activity view = null;

    public DownloadThread(Context context, String url, String userAgent, String fileName, String mineType, Activity view) {
        LogUtil.d(TAG, "----- DownloadThread() - _url : "+url);
        LogUtil.d(TAG, "----- DownloadThread() - _fileName : "+fileName);
        LogUtil.d(TAG, "----- DownloadThread() - mineType : "+mineType);
        this.context = context;
        this.mUrl = url;
        this.userAgent = userAgent;
        this.fileName = fileName;
        this.mineType = mineType;
        this.view = view;
    }

    @Override
    public void run() {
        try{
            URL url = new URL(mUrl);
            LogUtil.d(TAG, "----- run() - url.getAuthority : "+url.getAuthority());
            HttpsURLConnection uCon = (HttpsURLConnection)url.openConnection();
            String cookies = CookieManager.getInstance().getCookie(mUrl);
            uCon.setRequestProperty("cookie", cookies);
            uCon.setRequestProperty("Content-Type", mineType);
            uCon.setRequestProperty("User-Agent", userAgent);

            uCon.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            LogUtil.d(TAG, "----- run() - Environment.getDownloadCacheDirectory : "+ Environment.getDownloadCacheDirectory());
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

            File file = new File(path+"/"+fileName);

            InputStream is = (InputStream)uCon.getInputStream();
            OutputStream os = new FileOutputStream(file);

            IOUtils.copyStream(is, os);
            is.close();
            os.flush();
            os.close();

            LogUtil.d(TAG, "----- run() - FILE CHECK : "+file.exists());
            LogUtil.d(TAG, "----- run() - FILE PATH : "+file.getAbsolutePath());
            LogUtil.d(TAG, "----- run() - file is alive : "+file.exists());

            if(file.exists()) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
//                i.setDataAndType(FileProvider.getUriForFile(context, "com.ex.mobid.fileprovider", file), "application/vnd.ms-excel");
                i.setDataAndType(FileProvider.getUriForFile(context, "com.ex.master.fileprovider", file), " "+"image/jpeg");
                LogUtil.d(TAG, "----- run() - intent : "+i);
//                view.startActivity(i);
            }

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
