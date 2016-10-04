package com.example.pc.primeiraaulapdm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MinhaPrimeiraAtividade extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minha_primeira_atividade);

        TextView tvAloMundo = (TextView) findViewById(R.id.etAloMundo);
        tvAloMundo.setText("Alo Mundo PDM I");
    }

}
