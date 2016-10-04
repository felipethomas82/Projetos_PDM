package provapdm.jeansarlon.fidelize;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;

public class UserWallet extends AppCompatActivity {
    private WalletDAO fonteDados;
    ListView lv;
    AdapterWallet adaptador;
    List<Wallet> values;
    TextView tvTitleUserWallet;


    private void updateList(){
        values = fonteDados.getAll();
        boolean q = values.isEmpty();
        adaptador = new AdapterWallet(this, R.layout.item_list_wallet, values);
        lv.setAdapter(adaptador);
    }


    @Override
    protected void onResume() {
        try {
            fonteDados.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        updateList();

        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_wallet);

        tvTitleUserWallet = (TextView) findViewById(R.id.tvUserWalletTitle);
        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/Billabong.ttf");
        tvTitleUserWallet.setTypeface(face);

        fonteDados = new WalletDAO(this);
        lv = (ListView) findViewById(R.id.lvWallet);

        try {
            fonteDados.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        updateList();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Wallet wp = values.get(position);

                Intent intent = new Intent(UserWallet.this,UserSendCoin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("id", wp.company_id);
                intent.putExtra("company", wp.companyName);
                intent.putExtra("amount", wp.amount);
                startActivity(intent);

            }
        });
    }
}
