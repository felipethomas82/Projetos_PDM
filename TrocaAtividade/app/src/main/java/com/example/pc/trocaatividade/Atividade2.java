package com.example.pc.trocaatividade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Atividade2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividade2);

        Intent intencao = getIntent();
        Bundle sacola = intencao.getExtras();

        String pokemon = sacola.getString("pokemon3", "Valor padrao");

        TextView tvPokemon = (TextView) findViewById(R.id.tvParametro);
        tvPokemon.setText(pokemon);
    }
}
