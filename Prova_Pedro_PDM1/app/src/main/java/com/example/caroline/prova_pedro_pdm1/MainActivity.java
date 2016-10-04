package com.example.caroline.prova_pedro_pdm1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    static String PREFERENCIA = "preferencia";
    String P_mentira= "mentira";
    String P_verdade="verdade";
    String P_total="total";
    Integer p_verdade=0;
    Integer p_mentira=0;
    Integer p_total=0;
    String Pver="porcentoVerdade";
    String Pment="porcentoMentira";
    Integer pVer=0;
    Integer pMent=0;
    TextView porcverdade;
    TextView porcmentira;
    TextView verdade;
    TextView mentira;
    TextView total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main);
        verdade = (TextView)findViewById(R.id.verdade);
        mentira = (TextView)findViewById(R.id.mentira);
        porcverdade = (TextView)findViewById(R.id.porcverdade);
        porcmentira = (TextView)findViewById(R.id.porcmentira);
        total = (TextView)findViewById(R.id.total);

        SharedPreferences settings=(SharedPreferences)getSharedPreferences(PREFERENCIA,MODE_PRIVATE);
        p_mentira = settings.getInt(P_mentira,0);
        mentira.setText("Mentira: " + p_mentira.toString());
        pMent = settings.getInt(Pment,0);
        porcmentira.setText(""+pMent.toString());
        p_verdade = settings.getInt(P_verdade,0);
        verdade.setText("Verdade: "+p_verdade.toString());
        pVer = settings.getInt(Pver,0);
        porcverdade.setText("" +pVer.toString());
        p_total = settings.getInt(P_total,0);
        total.setText("Total: "+p_total.toString());
    }

    public void verdade(View v){
        Intent intencao = new Intent(MainActivity.this,Main2Activity.class);
        Bundle dados = new Bundle();
        dados.putInt("V", 0);
        intencao.putExtras(dados);
        startActivity(intencao);
    }

    public void mentira(View v ){
        Intent intencao = new Intent(MainActivity.this,Main2Activity.class);
        Bundle dados = new Bundle();
        dados.putInt("V",1);
        intencao.putExtras(dados);
        startActivity(intencao);
    }

    public void zerar(View v){
        SharedPreferences prefs = (SharedPreferences) getSharedPreferences(PREFERENCIA, MODE_PRIVATE);
        SharedPreferences.Editor editor = ((SharedPreferences) prefs).edit();
        editor.putInt(P_total,0);
        editor.putInt(P_mentira,0);
        editor.putInt(P_verdade,0);
        editor.commit();
        verdade.setText("Verdade: 0");
        total.setText("Total : 0");
        mentira.setText("Mentira: 0");
    }



}
