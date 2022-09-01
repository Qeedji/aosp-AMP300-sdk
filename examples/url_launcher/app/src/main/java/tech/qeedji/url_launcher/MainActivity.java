package tech.qeedji.url_launcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import tech.qeedji.host.webview.HostWebview;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "url_launcher";

    private Context mContext = null;
    private AppSharedPreferences mAppSharedPreferences = null;
    private WebView mWebView = null;
    private HostWebview mHost = null;

    private Handler mHandler = null;
    private String mUrl = null;
    private boolean mRefresh = AppSharedPreferences.DEFAULT_AUTOREFRESH_URL_ENABLED;
    private long mInterval = AppSharedPreferences.DEFAULT_AUTOREFRESH_URL_INTERVAL;
    private Thread mThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        Thread.setDefaultUncaughtExceptionHandler(
                new CustomExceptionHandler(mContext, MainActivity.class));
        mHandler = new Handler();
        mAppSharedPreferences = new AppSharedPreferences(mContext);
        mAppSharedPreferences.init();
        mUrl = mAppSharedPreferences.getString(AppSharedPreferences.PREF_URL, "");
        if ((mUrl == null) || "".equals(mUrl)) {
            mUrl = AppSharedPreferences.DEFAULT_URL;
        }
        mRefresh = mAppSharedPreferences.getBoolean(AppSharedPreferences.PREF_AUTOREFRESH_URL_ENABLED,
                AppSharedPreferences.DEFAULT_AUTOREFRESH_URL_ENABLED);
        mInterval = mAppSharedPreferences.getLong(AppSharedPreferences.PREF_AUTOREFRESH_URL_INTERVAL,
                AppSharedPreferences.DEFAULT_AUTOREFRESH_URL_INTERVAL);
        mWebView = new WebView(getApplicationContext());
        TypedValue val = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorBackground, val, true);
        mWebView.setBackgroundColor(val.data);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        mHost = new HostWebview(this, mWebView);
        mWebView.addJavascriptInterface(mHost, "Host");
        mWebView.setWebViewClient(new _WebViewClient());
        mWebView.setWebChromeClient(new _WebChromeClient());
        mWebView.setDownloadListener(new _DownloadListener());
        Log.d(TAG, "Load url " + mUrl);
        mWebView.clearCache(true);
        mWebView.loadUrl(mUrl);
        setContentView(mWebView);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        mHost.onNewIntent(intent);
        super.onNewIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mRefresh) {
            mThread = new Thread("Thread Content Url") {
                @Override
                public void run() {
                    Log.d(TAG, "Thread run");
                    try {
                        sleep(mInterval * 1000);
                        while (true) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "Reload " + mUrl);
                                    mWebView.loadUrl(mUrl);
                                }
                            });
                            sleep(mInterval * 1000);
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "Thread exiting");
                    }
                    Log.d(TAG, "Thread exit");
                }
            };
            mThread.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try
        {
            if (mThread != null) {
                mThread.interrupt();
                mThread.join();
            }
        } catch (Exception e) {
            Log.e(TAG, "Thread join failed");
        }
    }

    private class _WebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request,
                                    WebResourceError error) {
            if ((error != null) && (error.getDescription()!=null) && (error.getDescription().toString()!=null)) {
                Log.e(TAG, "onReceivedError " + error.getDescription().toString());
            } else {
                Log.e(TAG, "onReceivedError unknown error");
            }
        }
        @Override
        public void onReceivedHttpAuthRequest(WebView view,
                                              HttpAuthHandler handler, String host, String realm) {
            String[] t = getUsernameAndPassword(host);
            if (t != null) {
                handler.proceed(t[0], t[1]);
            }
        }
    }

    private class _WebChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.d(TAG, "\"" + consoleMessage.message() + "\", line: "
                    + consoleMessage.lineNumber() + ", source: " + consoleMessage.sourceId());
            return super.onConsoleMessage(consoleMessage);
        }
    }

    private void toast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class _DownloadListener  implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Log.d(TAG, "Download " + url + " (mimetype : " + mimetype + ") is not implemented");
            toast(url + " unreachable");
        }
    }

    private String[] getUsernameAndPassword(String host) {
        try {
            if ((host == null) || ("".equals(host))) {
                return null;
            }
            if ((mUrl == null) || ("".equals(mUrl))) {
                return null;
            }
            if (mUrl.contains(host) == false) {
                return null;
            }
            String credential = mAppSharedPreferences.getString(
                    AppSharedPreferences.PREF_CREDENTIAL, null);
            String basename = "tech.qeedji.url_launcher.credential." + credential + ".prefs";
            SharedPreferences prefs = mContext.getSharedPreferences(basename, Context.MODE_PRIVATE);
            String type = prefs.getString("type", "");
            if ("HttpAuth".equalsIgnoreCase(type)
                || "native".equalsIgnoreCase(type)) {
                String[] ret = {"", ""};
                ret[0] = prefs.getString("username", "");
                ret[1] = prefs.getString("password", "");
                return ret;
            }
        } catch (Exception e) {
            Log.e(TAG, "getUsernameAndPassword failed " + e.getMessage());
        }
        return null;
    }
}
