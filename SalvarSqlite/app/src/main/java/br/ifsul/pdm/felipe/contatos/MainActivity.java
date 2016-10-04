package br.ifsul.pdm.felipe.contatos;

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

import br.ifsul.pdm.felipe.salvarsqlite.R;


public class MainActivity extends ActionBarActivity {

    private ContatoDAO fonteDados;
    ListView lv;
    List<Contato> values;
    Adaptador adaptador;

    private void atualizaLista(){
        values = fonteDados.pegarTodosContatos();
        adaptador = new Adaptador(this, R.layout.item_da_lista, values);
        lv.setAdapter(adaptador);;
    }

    public void atualizaListaComBusca(View v){
        values = fonteDados.buscaContato(etNome.getText().toString(), etEmail.getText().toString(), etTelefone.getText().toString());
        adaptador = new Adaptador(this, R.layout.item_da_lista, values);
        lv.setAdapter(adaptador);;
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

    EditText etNome;
    EditText etTelefone;
    EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNome = (EditText) findViewById(R.id.etNome);
        etTelefone = (EditText) findViewById(R.id.etTelefone);
        etEmail  = (EditText) findViewById(R.id.etEmail);

        lv = (ListView) findViewById(R.id.lista);

        fonteDados = new ContatoDAO(this);
        try {
            fonteDados.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fonteDados.excluirContato(values.get(position));
                atualizaLista();
            }
        });
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

    @Override
    protected void onPause(){
        fonteDados.close();
        super.onPause();
    }

    public void onClickAdicionar(View v) throws SQLException {
        String nome = etNome.getText().toString();
        String telefone = etTelefone.getText().toString();
        String email = etEmail.getText().toString();

        if (nome.trim().isEmpty() || telefone.trim().isEmpty() || email.trim().isEmpty()){
            if (nome.trim().isEmpty())
                etNome.setError("Digite um nome");
            if (telefone.trim().isEmpty())
                etTelefone.setError("Digite um telefone");
            if (email.trim().isEmpty())
                etEmail.setError("Digite um email");
        } else {
            fonteDados.criarContato(nome, telefone, email);
            atualizaLista();
            etNome.getText().clear();
            etTelefone.getText().clear();
            etEmail.getText().clear();
        }
    }
}
