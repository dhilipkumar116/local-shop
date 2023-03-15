package com.example.localshop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class TermsandCondActivity extends AppCompatActivity {

    private String useroradmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsand_cond);

        useroradmin = getIntent().getStringExtra("useroradmin");
        WebView webView = findViewById(R.id.terms_and_cond);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.loadUrl("file:///android_asset/shop.html");
    }
}
