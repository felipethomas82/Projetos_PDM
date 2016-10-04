package com.example.pc.mostrandoimagens;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class ActivityWebView extends AppCompatActivity {

    private Button btnIr;
    private EditText etEnderecoUrl;
    private WebView wvPagina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_web_view);

        btnIr = (Button) findViewById(R.id.btnIr);
        etEnderecoUrl = (EditText) findViewById(R.id.etEnderecoUrl);
        wvPagina = (WebView) findViewById(R.id.wvPagina);

        wvPagina.getSettings().setJavaScriptEnabled(true);
        wvPagina.getSettings().setBuiltInZoomControls(true);

        //deve-se setar um novo webViewClient, caso contrário, a página abre no navegador padrão
        wvPagina.setWebViewClient(new WebViewClient());
    }

    private String getUrl(){
        String URL = etEnderecoUrl.getText().toString();
        if (URL.startsWith("http://")) {
            return URL;
        } else {
            return "http://" + URL;
        }
    }

    public void onClickir(View v) {
        wvPagina.loadUrl(getUrl());
    }

}
