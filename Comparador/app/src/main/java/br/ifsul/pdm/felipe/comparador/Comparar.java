package br.ifsul.pdm.felipe.comparador;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.text.ParseException;


public class Comparar extends ActionBarActivity {

    EditText qtd1, qtd2, preco1, preco2;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparar);

        qtd1 = new EditText(this);
        qtd2 = new EditText(this);
        preco1 = new EditText(this);
        preco2 = new EditText(this);

        qtd1 = (EditText) findViewById(R.id.txtQtd1);
        qtd2 = (EditText) findViewById(R.id.txtQtd2);
        preco1 = (EditText) findViewById(R.id.txtPreco1);
        preco2 = (EditText) findViewById(R.id.txtPreco2);

        result = new TextView(this);
        result = (TextView) findViewById(R.id.txtResultado);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comparar, menu);
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

    public void comparaPrecos(View v)
    {
        double q1;
        double q2;
        double pr1;
        double pr2;

        NumberFormat numberF = NumberFormat.getNumberInstance();

        try {
            q1 = numberF.parse(qtd1.getText().toString()).doubleValue();
        } catch (NumberFormatException e) {
            qtd1.setError("Informe um valor válido");
            return;
        } catch (ParseException e) {
            e.printStackTrace();
            qtd1.setError("Informe um valor válido");
            return;
        }

        try {
            q2 = numberF.parse(qtd2.getText().toString()).doubleValue();
        } catch (NumberFormatException e) {
            qtd2.setError("Informe um valor válido");
            return;
        }catch (ParseException e) {
            e.printStackTrace();
            qtd2.setError("Informe um valor válido");
            return;
        }

        try {
            pr1 = numberF.parse(preco1.getText().toString()).doubleValue();
        } catch (NumberFormatException e)
        {
            preco1.setError("Informe um valor válido");
            return;
        } catch (ParseException e) {
            e.printStackTrace();
            preco1.setError("Informe um valor válido");
            return;
        }

        try
        {
            pr2 = numberF.parse(preco2.getText().toString()).doubleValue();
        }
        catch (NumberFormatException e)
        {
            preco2.setError("Informe um valor válido");
            return;
        } catch (ParseException e) {
            e.printStackTrace();
            preco2.setError("Informe um valor válido");
            return;
        }


        pr1 = pr1 / q1;
        pr2 = pr2 / q2;

        if (pr1 < pr2)
        {
            result.setText("Produto 1 mais barato");
        }
        else
        {
            result.setText("Produto 2 mais barato");
        }
    }
}
