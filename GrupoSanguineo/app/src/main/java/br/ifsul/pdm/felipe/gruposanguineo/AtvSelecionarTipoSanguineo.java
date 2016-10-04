package br.ifsul.pdm.felipe.gruposanguineo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;


public class AtvSelecionarTipoSanguineo extends ActionBarActivity {

    RadioGroup rgTipoSanguineo;
    RadioGroup rgFatorRh;

    public static String PREFERENCIAS = "minhasPreferencias";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atv_selecionar_tipo_sanguineo);

        rgTipoSanguineo = (RadioGroup) findViewById(R.id.rgTipoSanguineo);
        rgFatorRh = (RadioGroup) findViewById(R.id.rgFatorRh);

        SharedPreferences preferences = getSharedPreferences(PREFERENCIAS, MODE_PRIVATE);
        String tipoSanguineo = preferences.getString("tipo", null);
        if (tipoSanguineo != null) {
            String fator = preferences.getString("fator", null);

            chamaActivity(tipoSanguineo, fator);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_atv_selecionar_tipo_sanguineo, menu);
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

    public void btnCompSangClick(View v){
        String tipoSanguineo = "";
        switch (rgTipoSanguineo.getCheckedRadioButtonId()){
            case R.id.rbA : tipoSanguineo = "A";
                break;
            case R.id.rbB : tipoSanguineo = "B";
                break;
            case R.id.rbO : tipoSanguineo = "O";
                break;
            case R.id.rbAB : tipoSanguineo = "AB";
        }

        String fatorRh = "";
        switch (rgFatorRh.getCheckedRadioButtonId()) {
            case  R.id.rbPositivo : fatorRh = "+";
                break;
            case R.id.rbNegativo : fatorRh = "-";
                break;
        }

        //salva no sharedpreferences
        SharedPreferences.Editor editor = getSharedPreferences(PREFERENCIAS, MODE_PRIVATE).edit();
        editor.putString("tipo", tipoSanguineo);
        editor.putString("fator", fatorRh);
        editor.commit();

        chamaActivity(tipoSanguineo, fatorRh);

    }

    public void chamaActivity(String tipoSanguineo, String fatorRh)
    {
        Bundle params = new Bundle();
        params.putString("tipo", tipoSanguineo);
        params.putString("fator", fatorRh);

        Intent intent = new Intent();
        intent.setClass(AtvSelecionarTipoSanguineo.this, AtvCompatibilidade.class);
        intent.putExtras(params);

        startActivity(intent);
    }
}
