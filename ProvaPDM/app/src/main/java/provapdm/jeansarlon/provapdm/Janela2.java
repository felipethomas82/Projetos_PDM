package provapdm.jeansarlon.provapdm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Janela2 extends AppCompatActivity {
    private final String NOME_PREF = "preferencia";
    ImageView ivResult;
    TextView tvResult;
    Button btFechar;

    Integer controle;
    Integer VouM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_janela2);
        ivResult = (ImageView) findViewById(R.id.ivBannerResult);
        tvResult = (TextView) findViewById(R.id.tvtextResult);
        btFechar = (Button) findViewById(R.id.btFechar);

        Bundle extras = getIntent().getExtras();
        controle = extras.getInt("controle");

        if (controle == 1){
            VouM = extras.getInt("prefVerdade");
            VouM++;
            ivResult.setImageResource(R.drawable.verdade);
            tvResult.setText("Você votou verdade!!");

            SharedPreferences pref = getSharedPreferences(NOME_PREF,0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("prefVerdade",VouM);
            editor.commit();

        }else{
            VouM = extras.getInt("prefMentira");
            VouM++;

            ivResult.setImageResource(R.drawable.mentira);
            tvResult.setText("Você votou mentira!!");

            SharedPreferences pref = getSharedPreferences(NOME_PREF,0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("prefMentira", VouM);
            editor.commit();
        }
        btFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencao = new Intent(Janela2.this, MainActivity.class);
                intencao.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intencao.putExtra("voltando",0);
                startActivity(intencao);
            }
        });
    }
}
