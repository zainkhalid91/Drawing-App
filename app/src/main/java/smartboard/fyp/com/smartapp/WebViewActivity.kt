package smartboard.fyp.com.smartapp

import android.app.ProgressDialog
import android.os.Bundle
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import smartboard.fyp.com.smartapp.WebViewActivity

class WebViewActivity : AppCompatActivity() {
    var webView: WebView? = null
    var close: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.web_view_activity)
        webView = findViewById(R.id.webView)
        startWebView("https://www.google.com.pk/")
    }

    private fun startWebView(url: String) {
        webView!!.webViewClient = object : WebViewClient() {
            var progressDialog: ProgressDialog? = null
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onLoadResource(view: WebView, url: String) {
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = ProgressDialog(this@WebViewActivity)
                    progressDialog!!.setMessage("Loading...")
                    progressDialog!!.show()
                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                try {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                        progressDialog = null
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        }
        webView!!.settings.javaScriptEnabled = true
        webView!!.loadUrl(url)
        webView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView!!.canGoBack()) {
            webView!!.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (webView!!.copyBackForwardList().currentIndex > 0) {
            webView!!.goBack()
        } else {
            super.onBackPressed()
        }
    }
}