package provapdm.jeansarlon.fidelize;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

public class UserSendCoin extends AppCompatActivity {
    private WalletDAO fonteDados;
    private EditText etZcoinDiscount;
    private TextView tvCompanyName;
    private Button btSend;

    private Integer amount;
    private Long company_id;
    private String companyName;
    Integer toDiscount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_send_coin);
        fonteDados = new WalletDAO(this);

        etZcoinDiscount = (EditText) findViewById(R.id.et_sendCoin_zcoin);
        tvCompanyName = (TextView) findViewById(R.id.tv_sendCoin_companyName);
        btSend = (Button) findViewById(R.id.bt_sendCoin_send);

        try {
            fonteDados.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            company_id = extras.getLong("id", 0);
            companyName = extras.getString("company", "nada");
            amount = extras.getInt("amount", 0);

            tvCompanyName.setText(companyName);
        }

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etZcoinDiscount.getText().toString().isEmpty() || Integer.parseInt(etZcoinDiscount.getText().toString()) == 0) {
                    etZcoinDiscount.setError("Digite um Valor Inteiro Positivo");
                } else {
                    if (Integer.parseInt(etZcoinDiscount.getText().toString()) > amount ){
                        Toast.makeText(UserSendCoin.this,"Não há saldo suficiente", Toast.LENGTH_SHORT).show();

                    }else {

                        Integer toDiscount = Integer.parseInt(etZcoinDiscount.getText().toString());
                        Wallet obj = new Wallet(company_id, amount - toDiscount, companyName);
                        fonteDados.update(obj);

                        Toast.makeText(UserSendCoin.this, toDiscount + " Zcoins descontado", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    }
}
