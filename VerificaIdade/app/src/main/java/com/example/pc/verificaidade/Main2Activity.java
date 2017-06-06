package com.example.pc.verificaidade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {

    TextView tvMensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        tvMensagem = (TextView) findViewById(R.id.tvMensagem);

        Bundle extras = getIntent().getExtras();
        String nome = extras.getString("nome");
        String sobrenome = extras.getString("sobrenome");
        int idade = extras.getInt("idade");

        if (idade >= 18){
            tvMensagem.setText("Olá "+ nome + " " + sobrenome + ". Você é maior de idade.");
        } else {
            tvMensagem.setText("Olá "+ nome + " " + sobrenome + ". Você é menor de idade.");
        }
    }
}
