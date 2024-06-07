package cr.ac.una.gimenayjeff

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val webView = findViewById<WebView>(R.id.webView)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url ?: "")
                return true
            }
        }
        webView.settings.javaScriptEnabled = true

        val url = intent.getStringExtra("url")
        Log.d("WebViewActivity", "URL recibida: $url")
        if (url != null) {
            webView.loadUrl(url)
        } else {
            Log.e("WebViewActivity", "URL es null")
        }
    }
}
