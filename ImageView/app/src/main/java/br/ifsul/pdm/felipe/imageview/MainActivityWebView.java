package br.ifsul.pdm.felipe.imageview;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;


public class MainActivityWebView extends ActionBarActivity {

    EditText endereco;
    WebView wvPagina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_web_view);

        endereco = (EditText) findViewById(R.id.txtEndereco);
        wvPagina = (WebView) findViewById(R.id.webView);

        //habilita JS e controles de Zoom
        wvPagina.getSettings().setJavaScriptEnabled(true);
        wvPagina.getSettings().setBuiltInZoomControls(true);

        //carrega a pagina dentro do webView do aplicativo, se omitir abre no navegador padrão
        wvPagina.setWebViewClient(new WebViewClient());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_web_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getUrl(String url)
    {
        if (url.startsWith("http://"))
            return url;
        else
            return "http://" + url;
    }

    public void clickOk(View v)
    {
        String Uri = this.getUrl(endereco.getText().toString());

        wvPagina.loadUrl(Uri);
    }
}
