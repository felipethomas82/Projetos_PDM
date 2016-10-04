package br.ifsul.pdm.felipe.imageview;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class MainActivityBotoes extends ActionBarActivity {

    EditText txtTexto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_botoes);

        txtTexto = (EditText) findViewById(R.id.texto2);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_botoes, menu);
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

    public void onClickChamaActivity(View v)
    {
        //deve-se passar o nome da classe
        Intent intencao = new Intent(this, MainActivitySegunda.class);
        intencao.putExtra("textoCaixa", txtTexto.getText());
        //depois de criada a intencao, efetiva ela com start
        startActivity(intencao);
    }
}
