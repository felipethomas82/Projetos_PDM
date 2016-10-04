package com.example.pc.trocaatividade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Atividade1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividade1);

        Button btn = (Button) findViewById(R.id.btnAbreOutraAtividade);

//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intencao = new Intent(Atividade1.this, Atividade2.class);
//                startActivity(intencao);
//            }
//        });
    }

    public void clickBotao(View v) {
        Intent intencao = new Intent(Atividade1.this, Atividade2.class);
//        intencao.putExtra("pokemon", "Mewtwo");
//        intencao.putExtra("pokemon3", "Pikachu");

        Bundle sacola = new Bundle();
        sacola.putString("pokemon", "Mewtwo");
        sacola.putString("pokemon3", "Pikachu");
        intencao.putExtras(sacola);

        startActivity(intencao);
    }
}
