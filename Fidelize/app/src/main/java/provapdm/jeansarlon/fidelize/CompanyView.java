package provapdm.jeansarlon.fidelize;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;

public class CompanyView extends AppCompatActivity {
    private WalletDAO fonteDados;
    TextView tvCompanyName;
    EditText tvAmout;
    Button btSend;

    long companyID;
    String companyname;
    Integer amount;
    Wallet value;


    protected void onResume(){
        try {
            fonteDados.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_view);

        tvCompanyName = (TextView) findViewById(R.id.tvCompanyName);
        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/Billabong.ttf");
        tvCompanyName.setTypeface(face);

        tvAmout = (EditText) findViewById(R.id.etAmount);
        btSend = (Button) findViewById(R.id.btSend);

        fonteDados = new WalletDAO(this);

        try {
            fonteDados.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            companyname = extras.getString("nome", "Não veio");
            companyID = extras.getLong("id", 0);
            tvCompanyName.setText(companyname);
        }

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = Integer.parseInt(tvAmout.getText().toString());

                if (tvAmout.getText().toString().isEmpty() || amount == 0) {
                    tvAmout.setError("Digite um Valor");
                } else {
                    value = fonteDados.getItemWallet(companyID);
                    if (value != null) {
                        value.amount += amount;
                        fonteDados.update(value);
                    } else {
                        Wallet w = new Wallet(companyID, amount, companyname);
                        fonteDados.store(w);
                    }
                    Toast.makeText(CompanyView.this, "Adicionado: " + amount, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
