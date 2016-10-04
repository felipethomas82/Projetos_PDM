package com.example.pc.shared_preferences;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String NOME_PREFS = "preferencias";
    EditText etEntrada;
    Button btnEncerrar;
    Button btnSalvar;
    String textoParaSalvar = "Digite algo aqui";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEntrada = (EditText) findViewById(R.id.etEntrada);
        btnSalvar = (Button) findViewById(R.id.btnSalvar);
        btnEncerrar = (Button) findViewById(R.id.btnEncerrar);

        SharedPreferences settings = (SharedPreferences) getSharedPreferences(NOME_PREFS, MODE_PRIVATE);
        textoParaSalvar = settings.getString("textoEntrada1", "Valor padrao");

        etEntrada.setText(textoParaSalvar);
    }


    public void onClickSalvar(View v) {
        textoParaSalvar = etEntrada.getText().toString();
        SharedPreferences prefs = (SharedPreferences) getSharedPreferences(NOME_PREFS, MODE_PRIVATE);

        Editor editor = ((SharedPreferences) prefs).edit();
        editor.putString("textoEntrada1", textoParaSalvar);
        editor.commit();
    }

    public void onClickSair(View v) {
        finish();
    }
}
