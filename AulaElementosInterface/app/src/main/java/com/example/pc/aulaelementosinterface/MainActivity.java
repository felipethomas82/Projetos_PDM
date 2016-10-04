package com.example.pc.aulaelementosinterface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    TextView tvTrocaSemBotao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTrocaSemBotao = (TextView) findViewById(R.id.tvTrocaSemBotao);
        tratarMudancaTexto();

        Button btnChamaToast = (Button) findViewById(R.id.btnChamaToast);
        btnChamaToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,
                        "Teste de Toast", Toast.LENGTH_SHORT).show();
            }
        });
    }//onCreate

    public void clickBotaoTrocaTexto(View v){

        EditText etTroca = (EditText) findViewById(R.id.etTrocaTexto);
        String texto = etTroca.getText().toString();

        TextView tvTroca = (TextView) findViewById(R.id.tvTrocaTexto);
        tvTroca.setText(texto);

    }

    private void tratarMudancaTexto() {
        EditText etTrocaSemBotao = (EditText)
                findViewById(R.id.etTrocaSemBotao);

        etTrocaSemBotao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Log.d("TESTE", editable.toString());
                tvTrocaSemBotao.setText(editable.toString());
            }
        });

    }//fim tratarMudancaTexto
}
