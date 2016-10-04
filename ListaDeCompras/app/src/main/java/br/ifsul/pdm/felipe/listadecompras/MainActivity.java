package br.ifsul.pdm.felipe.listadecompras;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    private ArrayList<Produto> produto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        produto =new ArrayList<Produto>();

        produto.add(new Produto("Arroz", 2.50));
        produto.add(new Produto("Feijao", 3.50));
        produto.add(new Produto("Leite", 2.89));
        produto.add(new Produto("Carne", 18.99));
        produto.add(new Produto("Pao", 5.45));
        produto.add(new Produto("Maca", 5.58));
        produto.add(new Produto("Tomate", 3.45));
        produto.add(new Produto("Cebola", 2.43));

        ArrayAdapter<Produto> adpProduto = new Adaptador(this, R.layout.layout_produto, produto);

        ListView lv = (ListView) findViewById(R.id.listViewProdutos);
        lv.setAdapter(adpProduto);

        String teste = "[{'nome':'MARCIOd','cpf':'uiyiuy'}," +
                "{'nome':'deeded','cpf':'uh'}," +
                "{'nome':'tutut','cpf':'978987'}]";

       // parseResult(teste);
    }

    //teste vinicius
    private void parseResult(String result) {

        try {
            JSONArray response = new JSONArray(result);

            String[] motoristas = new String[response.length()];

            for (int i = 0; i < response.length(); i++) {
                JSONObject motorista = response.getJSONObject(i);
                String nomeMotorista = motorista.getString("nome");

                motoristas[i] = nomeMotorista;
            }

            Log.i("Script", "Nomes motoristas: " + motoristas[0] + " - " + motoristas[1]);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void calculaTotal(View v)
    {
        double valorTotal = 0;
        for (Produto p : produto)
        {
            if (p.checado)
                valorTotal += p.valor;
        }

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat formato = NumberFormat.getCurrencyInstance(ptBr);

        Toast.makeText(getApplicationContext(), "Total de produtos: " + formato.format(valorTotal), Toast.LENGTH_SHORT).show();

    }
}
