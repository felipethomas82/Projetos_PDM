package br.ifsul.pdm.felipe.tarefas;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.List;

import br.ifsul.pdm.felipe.listadetarefas.R;


public class MainActivity extends ActionBarActivity {

    private TarefaDAO fonteDados;
    ListView lv;
    List<Tarefas> values;
    Adaptador adaptador;


    private void atualizaLista(){
        values = fonteDados.pegarTodasTarefas();
        adaptador = new Adaptador(this, R.layout.item_da_lista, values);
        lv.setAdapter(adaptador);
    }


    @Override
    protected void onResume(){
        try {
            fonteDados.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    EditText etTarefa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTarefa = (EditText) findViewById(R.id.etTarefa);

        lv = (ListView) findViewById(R.id.lvTarefas);

        fonteDados = new TarefaDAO(this);
        try {
            fonteDados.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fonteDados.excluirTarefa(values.get(position));
                atualizaLista();
            }
        });
    }

    @Override
    protected void onPause(){
        fonteDados.close();
        super.onPause();
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

    public void onClickAdicionar(View v) throws SQLException {
        String tarefa = etTarefa.getText().toString();

        if (tarefa.trim().isEmpty())
            etTarefa.setError("Digite uma tarefa");
        else {
            fonteDados.criarTarefa(tarefa);
            atualizaLista();
            etTarefa.getText().clear();
        }
    }
}
