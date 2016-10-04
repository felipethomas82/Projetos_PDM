package br.ifsul.pdm.felipe.gruposanguineo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;


public class AtvCompatibilidade extends ActionBarActivity {

    TextView txtRecebe;
    TextView txtDoa;
    TextView txtTipoSelecionado;
    String tipoSanguineo;
    String fatorRh;

    public static String PREFERENCIAS = "minhasPreferencias";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atv_compatibilidade);

        txtDoa = (TextView) findViewById(R.id.txtDoaParaOsGrupos);
        txtRecebe = (TextView) findViewById(R.id.txtRecebeDosGrupos);
        txtTipoSelecionado = (TextView) findViewById(R.id.txtTipoSelecionado);

        Intent intent = getIntent();
        Bundle params = intent.getExtras();

        tipoSanguineo = params.getString("tipo");
        fatorRh = params.getString("fator");

        txtTipoSelecionado.setText("Tipo selecionado: " + tipoSanguineo + fatorRh);

        String recebeDosGrupos = "";
        String doaPara = "";

        switch (tipoSanguineo){
            case "O" : if (fatorRh.equals("+")) {
                                recebeDosGrupos = "O+, O-";
                                doaPara = "O+, A+, B+, AB+";
                            }
                            else {
                                recebeDosGrupos = "O-";
                                doaPara = "Todos grupos sanguineos.";
                            }
                            break;

            case "A" : if (fatorRh.equals("+")) {
                                recebeDosGrupos = "O+, O-, A+, A-";
                                doaPara = "A+, AB+";
                            }
                            else {
                                recebeDosGrupos = "O-, A-";
                                doaPara = "A-, A+, AB-, AB+";
                            }
                            break;

            case "B" : if (fatorRh.equals("+")) {
                                recebeDosGrupos = "O+, O-, B+, B-";
                                doaPara = "B+, AB+";
                            }
                            else {
                                recebeDosGrupos = "O-, B-";
                                doaPara = "B-, B+, AB-, AB+";
                            }
                            break;

            case "AB" : if (fatorRh.equals("+")) {
                                recebeDosGrupos = "Recebe de todos";
                                doaPara = "AB+";
                            }
                            else {
                                recebeDosGrupos = "O-, A-, B-, AB-";
                                doaPara = "AB-, AB+";
                            }
                            break;
        }

        txtDoa.setText(doaPara);
        txtRecebe.setText(recebeDosGrupos);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_atv_compatibilidade, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            //anula os valores salvos
            SharedPreferences.Editor editor = getSharedPreferences(PREFERENCIAS, MODE_PRIVATE).edit();
            editor.putString("tipo", null);
            editor.putString("fator", null);
            editor.commit();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
