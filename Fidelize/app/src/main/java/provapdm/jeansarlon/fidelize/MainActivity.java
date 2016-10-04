package provapdm.jeansarlon.fidelize;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CompanyDAO fonteDados;
    ListView lv;
    List<Company> values;
    Adapter adaptador;
    Button btUser;
    TextView tvTitle;

    private void updateList(){
        values = fonteDados.getAllCompanies();
        adaptador = new Adapter(this, R.layout.item_list, values);
        lv.setAdapter(adaptador);
    }


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
        setContentView(R.layout.activity_main);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/Billabong.ttf");
        tvTitle.setTypeface(face);
        btUser = (Button) findViewById(R.id.btUser);

        fonteDados = new CompanyDAO(this);
        lv = (ListView) findViewById(R.id.list);
        try {
            fonteDados.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        updateList();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Company cp = values.get(position);
//                fonteDados.destroyCompany(cp);
//                updateList();

                Intent intent = new Intent(MainActivity.this, CompanyView.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("nome", cp.nome);
                intent.putExtra("id", cp.id);
                startActivity(intent);

            }
        });

        btUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserView.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }
}
