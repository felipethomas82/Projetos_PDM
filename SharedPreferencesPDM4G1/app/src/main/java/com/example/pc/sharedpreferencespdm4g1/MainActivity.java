package com.example.pc.sharedpreferencespdm4g1;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String NOME_PREFS = "preferencias";
    EditText et;
    Button btnSalvar;
    Button btnFechar;
    String textoParaSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = (EditText) findViewById(R.id.entrada1);
        btnSalvar = (Button) findViewById(R.id.botao_salvar);
        btnFechar = (Button) findViewById(R.id.botao_fechar);

        //restaurar dados
        SharedPreferences settings = getSharedPreferences(NOME_PREFS, 0);
        textoParaSalvar = settings.getString("textoEntrada1", "Valor padrão");

        et.setText(textoParaSalvar);
    }

    public void onClickSalvar(View v) {
        textoParaSalvar = et.getText().toString();

        SharedPreferences prefs = getSharedPreferences(NOME_PREFS, 0);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("textoEntrada1", textoParaSalvar);
        editor.commit();

        Toast.makeText(MainActivity.this, "Valor salvo com sucesso", Toast.LENGTH_SHORT).show();
    }
}
