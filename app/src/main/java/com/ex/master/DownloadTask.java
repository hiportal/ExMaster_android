package com.ex.master;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ex.master.util.LogUtil;

public class DownloadTask extends AsyncTask<Void, Void, String> {
    public static String TAG = DownloadTask.class.getSimpleName();

    Context context;
    String url = "";
    String userAgent = "";
    String fileName = "";
    String mineType = "";
    Activity view = null;

    public DownloadTask(Context context, String url, String userAgent, String fileName, String mineType, Activity view){
        this.context = context;
        this.url = url;
        this.userAgent = userAgent;
        this.fileName = fileName;
        this.mineType = mineType;
        this.view = view;
    }

    @Override
    protected String doInBackground(Void... params) {
        LogUtil.d(TAG, "----- doInBackground() - params : "+params);
        String result = "";

        DownloadThread downloadThread = new DownloadThread(context, url, userAgent, fileName, mineType, view);
        downloadThread.run();

        LogUtil.d(TAG, "----- doInBackground() - result : "+result);
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        LogUtil.d(TAG, "----- onPostExecute() - result : "+result);
        Toast.makeText(context, "파일을 다운로드합니다. \n[ 내파일 > Download ] 폴더에서 확인하세요.", Toast.LENGTH_LONG).show();
    }

}
