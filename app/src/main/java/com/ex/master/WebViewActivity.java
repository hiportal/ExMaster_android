package com.ex.master;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.ex.master.util.LogUtil;
import com.ex.master.util.SSLConnect;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static com.ex.master.util.CommonConstant.MOB_SERVER_URL;
import static com.ex.master.util.CommonConstant.MOB_TITLE;
import static com.ex.master.util.CommonConstant.NETWORK_TYPE_NOT_CONNECTED;
import static com.ex.master.util.CommonConstant.NET_CONNECTIVITY_CHANGE;
import static com.ex.master.util.CommonConstant.NET_WIFI_STATE_CHANGE;
import static com.ex.master.util.CommonConstant.NET_WIFI_SUPPLICANT_CONNECTION_CHANGE;
import static com.ex.master.util.CommonUtil.checkNetworkStatus;

public class WebViewActivity extends AppCompatActivity {

    public static String TAG = WebViewActivity.class.getSimpleName();
    Context context = WebViewActivity.this;

    private String url = "";
    CookieManager cookieManager;
    private WebView webview;
//    private WebView mWebView = null;

    private ValueCallback mFilePathCallback;
    private final static int FILECHOOSER_NORMAL_REQ_CODE = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "***** onCreate()");

        setContentView(R.layout.activity_webview);
        webview = (WebView)findViewById(R.id.webview);

        WebSettings mWebSettings = webview.getSettings();
        webview = (WebView) findViewById(R.id.webview);

        SSLConnect.trustAllHosts();
        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new AndroidBridge(), "android");
        webview.setWebViewClient(new ExWebViewClient());
        webview.setWebChromeClient(new ExWebChromeClient());
        webview.setDownloadListener(new ExWebViewClient());


        webview.getSettings().setLoadWithOverviewMode(true);      // WebView 화면크기에 맞춰주는 구문1 - 반드시 구문2와 같이 써야 맞춰짐
        webview.getSettings().setUseWideViewPort(true);      // WebView 화면크기에 맞춰주는 구문2 - 반드시 구문1과 같이 써야 맞춰짐
        webview.getSettings().setBuiltInZoomControls(false);   // 화면 확대/축소 버튼 여부
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); // javascript가 window.open()을 사용할 수 있도록 설정
        webview.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND); // 플러그인을 사용할 수 있도록 설정
        webview.getSettings().setSupportMultipleWindows(true); // 여러개의 윈도우를 사용할 수 있도록 설정

        //webview.getSettings().setDomStorageEnabled(true);    // 로컬 저장소 허용여부

        webview.getSettings().setLightTouchEnabled(true);
        webview.getSettings().setSavePassword(true);
        webview.getSettings().setSaveFormData(true);

        webview.setWebContentsDebuggingEnabled(true);
        //webview.clearCache(true);      // WebView 사용 시 캐시 제거 구문1
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);      // WebView 사용 시 캐시 제거 구문2
//        webview.setDownloadListener(new ExWebViewClient());

        LogUtil.d(TAG, "***** onCreate() - url : "+MOB_SERVER_URL);

        if (Build.VERSION.SDK_INT < LOLLIPOP) {
            CookieSyncManager.createInstance(this);
        }
        setCookieAllow(cookieManager, webview);

        //네트워크 상태 체크를 위한 BroadcastReceiver 등록
        NetworkBroadcastReceiver receiver = new NetworkBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NET_CONNECTIVITY_CHANGE);
        intentFilter.addAction(NET_WIFI_STATE_CHANGE);
        intentFilter.addAction(NET_WIFI_SUPPLICANT_CONNECTION_CHANGE);
        WebViewActivity.this.registerReceiver(receiver, intentFilter);

        navigateUrl(MOB_SERVER_URL);
    }


    private void setCookieAllow(CookieManager cookieManager, WebView webView) {
        try {
            cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.removeSessionCookie();
            if (Build.VERSION.SDK_INT >= LOLLIPOP) {
                webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                cookieManager.setAcceptThirdPartyCookies(webView, true);
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("[Exception] WebViewActivity >> setCookieAllow()");
        }
    }


    // 웹뷰 URL 이동 수행
    private void navigateUrl(String url) {
        LogUtil.d(TAG, "***** navigateUrl() - url : "+url);
        final String goUrl = url;
        if ((null != url) && (!"".equals(url.trim()))) {
            webview.post(new Runnable()
            {
                @Override
                public void run() {
                    webview.loadUrl(goUrl);
                }
            });
        }
    }


    private class ExWebViewClient extends WebViewClient implements DownloadListener {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            LogUtil.d(TAG,"***** onPageStarted() - url : "+url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
            LogUtil.d(TAG,"***** onDownloadStart() - url : "+url);
            LogUtil.d(TAG,"***** onDownloadStart() - userAgent : "+userAgent);
            LogUtil.d(TAG,"***** onDownloadStart() - contentDisposition : "+contentDisposition);
            LogUtil.d(TAG,"***** onDownloadStart() - mimeType : "+mimeType);

            String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
            fileName = fileName.substring(0, fileName.lastIndexOf(";")).trim();
            LogUtil.d(TAG,"***** onDownloadStart() - fileName : "+fileName);

            if(url.startsWith("https")) {
                //https 인 경우
                LogUtil.d(TAG,"***** onDownloadStart() - https ");
                DownloadTask downloadTask = new DownloadTask(context, url, userAgent, fileName, mimeType, WebViewActivity.this);
                downloadTask.execute();

            } else {
                //http 인 경우
                LogUtil.d(TAG,"***** onDownloadStart() - http ");
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                contentDisposition = contentDisposition.substring(0, contentDisposition.lastIndexOf(";"));
                request.setMimeType(mimeType);
                //------------------------COOKIE!!------------------------
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                //------------------------COOKIE!!------------------------
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading file...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType).trim());
//                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);

                Toast.makeText(context, "파일을 다운로드합니다. \n[ 내파일 > Download ] 폴더에서 확인하세요.", Toast.LENGTH_LONG).show();

            }


        }

        @Override
        public void onLoadResource(WebView view, String url) {
//            LogUtil.d(TAG, "***** onLoadResource() - url : "+url);
            super.onLoadResource(view, url);

            if(url.contains("main/load-page?pageName=intro/idFind")
                || url.contains("main/load-page?pageName=intro/pwFind")
                || url.contains("main/load-page?pageName=intro/registerAgree")) {
                LogUtil.d(TAG, "***** onLoadResource() - url : "+url);

                view.clearCache(true);      // WebView 사용 시 캐시 제거 구문1
                view.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);      // WebView 사용 시 캐시 제거 구문2
                setCookieAllow(cookieManager, webview);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            LogUtil.d(TAG, "***** shouldOverrideUrlLoading() - request.getUrl : "+request.getUrl());
            String mUrl = request.getUrl().toString();
//            http://175.214.44.153:8085/mater/main/load-page?pageName=intro/idFind
//            pageName=intro/pwFind

            if(mUrl.contains(MOB_SERVER_URL+"main/load-page?pageName=intro/idFind")
                || mUrl.contains(MOB_SERVER_URL+"main/load-page?pageName=intro/pwFind")
                || mUrl.contains("main/load-page?pageName=intro/registerAgree")) {
                LogUtil.d(TAG,"***** shouldOverrideUrlLoading() - idFind_pwFind");

                view.clearCache(true);      // WebView 사용 시 캐시 제거 구문1
                view.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);      // WebView 사용 시 캐시 제거 구문2
                setCookieAllow(cookieManager, webview);

                return false;

            } else {
                return super.shouldOverrideUrlLoading(view, request);
            }

//            return super.shouldOverrideUrlLoading(view, request);
        }


        @Override
        public void onReceivedError(WebView webview, int errorCode, String description, String failingUrl) {
            super.onReceivedError(webview, errorCode, description, failingUrl);
            LogUtil.d(TAG, "***** onReceivedError() - failingUrl : "+failingUrl);
            LogUtil.d(TAG, "***** onReceivedError() - errorCode : "+errorCode);
            switch (errorCode) {
                case ERROR_AUTHENTICATION:
                case ERROR_BAD_URL:
                case ERROR_CONNECT:
                case ERROR_FAILED_SSL_HANDSHAKE:
                case ERROR_FILE:
                case ERROR_FILE_NOT_FOUND:
                case ERROR_HOST_LOOKUP:
                case ERROR_IO:
                case ERROR_PROXY_AUTHENTICATION:
                case ERROR_REDIRECT_LOOP:
                case ERROR_TIMEOUT:
                case ERROR_TOO_MANY_REQUESTS:
                case ERROR_UNKNOWN:
                case ERROR_UNSUPPORTED_AUTH_SCHEME:
                    //case ERROR_UNSUPPORTED_SCHEME:
                    webview.loadUrl("about:blank");

                    final Dialog dialog = new Dialog(context, R.style.CustomDialog);
                    final LayoutInflater layoutInflater = getLayoutInflater();

                    View customMessage = layoutInflater.inflate(R.layout.layout_dialog, null);
                    final Button btn_confirm = (Button) customMessage.findViewById(R.id.btn_confirm);
                    final TextView dialog_content = (TextView)customMessage.findViewById(R.id.dialog_content);
                    dialog_content.setText("통신이 원활하지 않습니다. 다시 시도해주세요.");
                    btn_confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //앱 종료
                            WebViewActivity.this.finishAffinity();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    });
                    dialog.setCancelable(false);
                    dialog.setContentView(customMessage);
                    dialog.show();

                    break;
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//            super.onReceivedSslError(view, handler, error);
            LogUtil.d(TAG, "***** onReceivedSslError() - error : "+error);
            handler.proceed();
        }
    }


    public class ExWebChromeClient extends WebChromeClient {

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            new AlertDialog.Builder(view.getContext())
                    .setTitle(MOB_TITLE)
                    .setMessage(message)
                    .setPositiveButton("OK",
                            new AlertDialog.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm();
                                }
                            })
                    .setCancelable(false)
                    .create()
                    .show();
            return true;
            //return super.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            new AlertDialog.Builder(view.getContext())
                    .setTitle(MOB_TITLE)
                    .setMessage(message)
                    .setPositiveButton("Yes", new AlertDialog.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setNegativeButton("No", new AlertDialog.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
            return true;
//            return super.onJsConfirm(view, url, message, result);
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            LogUtil.d(TAG, "***** onConsoleMessage() - onConsoleMessage : "+consoleMessage.message());
            LogUtil.d(TAG, "***** onConsoleMessage() - onConsoleMessage sourceId : "+consoleMessage.sourceId());
            if(consoleMessage.sourceId().startsWith("https://nice.checkplus.co.kr/")) {
                webview.clearCache(true);
                webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            }

            return super.onConsoleMessage(consoleMessage);
        }

        //Android 5.0 이상 카메라 - input type="file" 태그를 선택했을 때 반응
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            LogUtil.d(TAG, "***** onShowFileChooser()");
            //Callback 초기화
            //return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);

            /* 파일 업로드 */
            if(mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            }
            mFilePathCallback = filePathCallback;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, 0);
            return true;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        LogUtil.d(TAG, "***** onActivityResult() - requestCode : "+requestCode);
        LogUtil.d(TAG, "***** onActivityResult() - resultCode : "+resultCode);
        LogUtil.d(TAG, "***** onActivityResult() - data : "+data);
        switch(requestCode) {
            case FILECHOOSER_NORMAL_REQ_CODE:
                if(resultCode == RESULT_OK) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
//                        filePathCallbackDocu = null;
                    }else{
                        mFilePathCallback.onReceiveValue(new Uri[]{data.getData()});
                    }
                    mFilePathCallback = null;
                } else {
                    if(mFilePathCallback != null) {
                        mFilePathCallback.onReceiveValue(null);
                        mFilePathCallback = null;
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public class AndroidBridge {

        @JavascriptInterface
        public void goAppFinish(){

            LogUtil.d(TAG, "***** goAppFinish()");
            //앱 종료
            WebViewActivity.this.finishAffinity();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
//        mWebView.goBack();
        LogUtil.d(TAG, "onBackPressed() - url : "+webview.getUrl());
        if (webview.getUrl().equals(MOB_SERVER_URL) || webview.getUrl().equals(MOB_SERVER_URL+"main/load-page?pageName=main/main")) {
            navigateUrl("javascript:f_historyGo()");

        } else if(webview.getUrl().contains("intro/idFind_Checkplus?")
                || webview.getUrl().contains("intro/pwFind_Checkplus?")
                || webview.getUrl().contains("intro/checkplus_main?")) {
            navigateUrl(MOB_SERVER_URL);
        }else {
            webview.goBack();
        }
    }


    /* NetworkBroadcastReceiver - 네트워크 상태 체크를 위한 BroadcastReceiver 클래스 */
    public class NetworkBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String strAction = intent.getAction();
            LogUtil.d(TAG, "***** onReceive() - action : "+strAction);

            //인터넷이 연결안되어있을 때 애 종료 팝업 표출
            if(checkNetworkStatus(context) == NETWORK_TYPE_NOT_CONNECTED) {
                final Dialog dialog = new Dialog(context, R.style.CustomDialog);
                final LayoutInflater layoutInflater = getLayoutInflater();

                View customMessage = layoutInflater.inflate(R.layout.layout_dialog, null);
                final Button btn_confirm = (Button) customMessage.findViewById(R.id.btn_confirm);
                final TextView dialog_content = (TextView)customMessage.findViewById(R.id.dialog_content);
                dialog_content.setText("네트워크 상태가 원활하지 않습니다.");
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //앱 종료
                        WebViewActivity.this.finishAffinity();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
                dialog.setCancelable(false);
                dialog.setContentView(customMessage);
                dialog.show();
            }

        }
    }

}
