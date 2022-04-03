package com.gerwalex.lib.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.gerwalex.lib.databinding.WebviewBinding;

/**
 * zeigt eine interne html-Seite. Im Intent wird unter ASSETHTML der Name des html-files erwartet
 * und im Verzeichnis assets/html gesucht.
 */
public class WebViewActivity extends AppCompatActivity {
    public static final String ASSETHTML = "ASSETHTML";
    private static final String path = "file:///android_asset/html/";
    private WebviewBinding binding;

    @Override
    public void onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = WebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String htmlSeite = getIntent().getExtras().getString(ASSETHTML);
        binding.webView.loadUrl(path + htmlSeite);
    }
}
