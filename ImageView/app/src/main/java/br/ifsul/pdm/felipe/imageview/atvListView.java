package br.ifsul.pdm.felipe.imageview;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class atvListView extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atv_list_view);
        final ArrayAdapter<String> adaptador;

        String[] s = new String[] {"Primeiro", "Segundo", "Terceiro", "Quarto", "Quinto", "Primeiro", "Segundo", "Terceiro", "Quarto", "Quinto", "Primeiro", "Segundo", "Terceiro", "Quarto", "Quinto", "Primeiro", "Segundo", "Terceiro", "Quarto", "Quinto"};
        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, s);

        ListView lv = (ListView) findViewById(R.id.listView);

        lv.setAdapter(adaptador);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;

                String s = tv.getText().toString();
                Toast.makeText(getApplicationContext(),"Você clicou em:" + s, Toast.LENGTH_SHORT).show();
            }
        });

        EditText etBusca = (EditText) findViewById(R.id.editTextSearch);
        etBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                adaptador.getFilter().filter(cs.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_atv_list_view, menu);
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
}
