package com.example.caroline.prova_pedro_pdm1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {
    static String PREFERENCIA = "preferencia";
    static String P_total = "total";
    static String P_verdade = "verdade";
    static String P_mentira = "mentira";
   // static String Pver = "porcentoVerdade";
   // static String Pment = "porcentoMentira";
    ImageView img;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setContentView(R.layout.activity_main2);
        textView = (TextView) findViewById(R.id.textView);
        img = (ImageView) findViewById(R.id.imagem);
        mostrar_dados();
    }

    public void mostrar_dados() {
        Intent intencao = getIntent();
        Bundle dados = intencao.getExtras();
        int aux = dados.getInt("V");
        if (aux == 0) {
        // se for verdade
            img.setImageResource(R.drawable.fantastico_detetive_virtual_2);//imagem verdade
            textView.setText("VERDADE");// texto da verdade
            SharedPreferences prefs = (SharedPreferences) getSharedPreferences(PREFERENCIA, MODE_PRIVATE);
            Integer Aux_total = prefs.getInt(P_total, 0) + 1;//conta
            Integer auxi = prefs.getInt(P_verdade, 0);
           // Pver = auxi/Aux_total;
          //  Integer centver = prefs.getInt(Pver, 0);

            SharedPreferences.Editor editor = ((SharedPreferences) prefs).edit();
            editor.putInt(P_total, Aux_total);
            editor.putInt(P_verdade, auxi + 1);
            //editor.putInt(Pver, centver);
            editor.commit();
        } else if (aux == 1) {
        //senão, se for mentira
            img.setImageResource(R.drawable.mentira1);//imagem mentira
            textView.setText("MENTIRA");//texto da mentira
            SharedPreferences prefs = (SharedPreferences) getSharedPreferences(PREFERENCIA, MODE_PRIVATE);
            Integer Aux_total = prefs.getInt(P_total, 0) + 1;
            Integer auxi = prefs.getInt(P_mentira, 0) + 1;
            SharedPreferences.Editor editor = ((SharedPreferences) prefs).edit();
            editor.putInt(P_total, Aux_total);
            editor.putInt(P_mentira, auxi);
            editor.commit();
        }
    }

    public void voltar(View v){
        Intent intent = new Intent(Main2Activity.this,MainActivity.class);
        intent.putExtra("texto","texto");
        startActivity(intent);
    }

}
