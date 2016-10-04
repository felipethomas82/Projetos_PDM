package br.ifsul.pdm.felipe.calculadora;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;


public class MainActivity extends ActionBarActivity {

    TextView txtCalculadora;

    float valor1, resultado;

    String operador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCalculadora = new TextView(this);
        //faz o bind da caixa de texto
        txtCalculadora = (TextView) findViewById(R.id.txtDisplay);
        txtCalculadora.setText("");
        valor1 = 0;
        operador = new String();
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

    public void insereNumero(View v)
    {
        if (resultado != 0) {
            valor1 = resultado;
            txtCalculadora.setText("0");
        }

        //faz o cast do v para button
        Button b = (Button) v;
        String textoDisplay = (String)txtCalculadora.getText();


        if (textoDisplay.equals("0.0") || textoDisplay.equals("0")) {
            textoDisplay = "";
        }

        if (b.getText().equals(",")) {
            if (!(textoDisplay.contains(",")))
                textoDisplay += b.getText();
        }
        else
            textoDisplay += b.getText();

        //txtCalculadora.setText(Integer.toString(1));
        txtCalculadora.setText(textoDisplay);
    }

    public void clickOperador(View v)
    {
        Button b = (Button) v;
        if (!b.getText().equals("="))
            operador = b.getText().toString();

        String textoDisplay = txtCalculadora.getText().toString();
        //troca virgula por ponto
        textoDisplay = textoDisplay.replace(",",".");
        if (valor1 == 0) {
            valor1 = Float.parseFloat(textoDisplay);
            txtCalculadora.setText("0");
        }
        else
        {
            float valor2 = Float.parseFloat(textoDisplay);
            switch (operador){
                case "+": resultado = valor1 + valor2;
                    valor1 = 0;
                    break;
                case "-": resultado = valor1 - valor2;
                    valor1 = 0;
                    break;
                case "*": resultado = valor1 * valor2;
                    valor1 = 0;
                    break;
                case "/": resultado = valor1 / valor2;
                    valor1 = 0;
                    break;
            }
        }

        NumberFormat numberF =  NumberFormat.getNumberInstance();
        txtCalculadora.setText(numberF.format(resultado));

    }

    public void limpar(View v)
    {
        valor1=0;
        resultado = 0;
        txtCalculadora.setText("0");
    }
}
