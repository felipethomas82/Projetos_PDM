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

public class MainActivity extends AppCompatActivity {
    private final String NOME_PREF = "preferencia";
    ImageView ivBanner;
    Button btVerdade;
    Button btMentira;
    Button btReset;
    TextView tvResultVerdade;
    TextView tvResultMentira;
    TextView tvVotosTotais;

    Integer prefVerdade;
    Integer prefMentira;
    Integer votosTotais;

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivBanner = (ImageView) findViewById(R.id.ivBanner);
        btVerdade = (Button) findViewById(R.id.btVerdade);
        btMentira = (Button) findViewById(R.id.btMentira);
        btReset = (Button) findViewById(R.id.btReset);
        tvResultMentira = (TextView) findViewById(R.id.tvResultMentira);
        tvResultVerdade = (TextView) findViewById(R.id.tvResultVerdade);
        tvVotosTotais = (TextView) findViewById(R.id.tvVotosTotais);

        ivBanner.setImageResource(R.drawable.detetive);

        //fazOqueTemQueFazer();


        btVerdade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencao = new Intent(MainActivity.this, Janela2.class);
                intencao.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intencao.putExtra("prefVerdade", prefVerdade);
                intencao.putExtra("controle", 1);
                startActivity(intencao);
            }
        });
        btMentira.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencao = new Intent(MainActivity.this, Janela2.class);
                intencao.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intencao.putExtra("prefMentira", prefMentira);
                intencao.putExtra("controle", 2);
                startActivity(intencao);
            }
        });
        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer reset = 0;
                SharedPreferences.Editor editor = pref.edit();
//
                editor.clear();
                editor.commit();
                tvResultVerdade.setText(reset.toString());
                tvResultMentira.setText(reset.toString());
                tvVotosTotais.setText(reset.toString());

            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            fazOqueTemQueFazer();
        }

    }

    public float porc(Integer total, Integer parc){
        if (total != 0) {
            return (parc * 100) / total;
        }
        return 0;
    };

    public void fazOqueTemQueFazer(){
        pref = getSharedPreferences(NOME_PREF, 0);
        prefVerdade = pref.getInt("prefVerdade", 0);
        prefMentira = pref.getInt("prefMentira",0);
        votosTotais = prefMentira + prefVerdade;

        String pv = String.format("%.1f",porc(votosTotais, prefVerdade));
        String pm = String.format("%.1f",porc(votosTotais, prefMentira));


        tvResultVerdade.setText(prefVerdade.toString() + "  " + pv + "%");
        tvResultMentira.setText(prefMentira.toString()  + "  " + pm + "%");
        tvVotosTotais.setText(votosTotais.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();

        fazOqueTemQueFazer();
    }
}
