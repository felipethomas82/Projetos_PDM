package br.ifsul.pdm.felipe.tsi_5an;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class teste extends ActionBarActivity {

    TextView tvTeste2;
    EditText caixa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste);

        tvTeste2 = new TextView(this);
        //TextView tvTeste2 = (TextView) findViewById(R.id.tvHello);
        tvTeste2 = (TextView) findViewById(R.id.tvHello);
        tvTeste2.setText("Meu Hello World!");
        //setContentView(tvTeste2);

        caixa = (EditText) findViewById(R.id.txtTeste);
        //caixa = (EditText) findViewById(R.id.txtTeste);
        //String textoCaixa = caixa.getText().toString();
        //tvTeste2.setText(textoCaixa);
        //setContentView(tvTeste2);
    }

    public void trocarTexto(View v)
    {
        String textoCaixa = caixa.getText().toString();
        //declarando com Double (objeto), ocupa muito mais memoria do sistema, o ideal é usar o
        //tipo primitivo (double) e usar o metodo de classe Double.toString())
        //Double nro = Double.parseDouble(textoCaixa);
        double nro = Double.parseDouble(textoCaixa);
        nro *= 2;
        //tvTeste2.setText(textoCaixa);
        //tvTeste2.setText(nro.toString());
        tvTeste2.setText(Double.toString(nro));

        //jeito POG de converter para string, concatena o nro a uma string que ele converte automaticamente
        //tvTeste2.setText(nro + "");
    }


}
