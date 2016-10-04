package br.ifsul.pdm.felipe.guardiaodotexto;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    TextView textResultado;
    EditText txtCPF2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textResultado = (TextView) findViewById(R.id.txtResultado);
        txtCPF2 = (EditText) findViewById(R.id.txtCPF);

        MeuTextWatcher meuTxt = new MeuTextWatcher();
        txtCPF2.addTextChangedListener(meuTxt);

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


    class MeuTextWatcher implements TextWatcher
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //s = é o texto que mudou
            Log.d("TESTE", s.toString()+ " start: " + start + " before: " + before + " count " + count);
            //remove os pontos e traços da string
            String sCPF = s.toString().replace(".", "").replace("-", "");
            CPF cpf = new CPF();
            if (sCPF.length() == 11)
            {
                if (cpf.validaCPF(sCPF))
                {
                    textResultado.setTextColor(Color.GREEN);
                    textResultado.setText("CPF válido");
                }
                else
                {
                    textResultado.setTextColor(Color.RED);
                    textResultado.setText("CPF inválido");
                }
            }
            else
            {
                textResultado.setTextColor(Color.BLUE);
                textResultado.setText("Quantidade de digitos menor ou maior que 11");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            //Log.d("TESTE", s.toString());
        }
    }


    class CPF {

        private String calcDigVerif(String num) {
            Integer primDig, segDig;
            int soma = 0, peso = 10;
            for (int i = 0; i < num.length(); i++)
                soma += Integer.parseInt(num.substring(i, i + 1)) * peso--;

            if (soma % 11 == 0 | soma % 11 == 1)
                primDig = new Integer(0);
            else
                primDig = new Integer(11 - (soma % 11));

            soma = 0;
            peso = 11;
            for (int i = 0; i < num.length(); i++)
                soma += Integer.parseInt(num.substring(i, i + 1)) * peso--;

            soma += primDig.intValue() * 2;
            if (soma % 11 == 0 | soma % 11 == 1)
                segDig = new Integer(0);
            else
                segDig = new Integer(11 - (soma % 11));

            return primDig.toString() + segDig.toString();
        }

        private int calcSegDig(String cpf, int primDig) {
            int soma = 0, peso = 11;
            for (int i = 0; i < cpf.length(); i++)
                soma += Integer.parseInt(cpf.substring(i, i + 1)) * peso--;

            soma += primDig * 2;
            if (soma % 11 == 0 | soma % 11 == 1)
                return 0;
            else
                return 11 - (soma % 11);
        }

        public boolean validaCPF(String cpf) {
            if (cpf.length() != 11)
                return false;

            String numDig = cpf.substring(0, 9);
            return calcDigVerif(numDig).equals(cpf.substring(9, 11));
        }
    }

}
