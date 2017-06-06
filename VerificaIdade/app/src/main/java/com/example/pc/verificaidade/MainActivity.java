package com.example.pc.verificaidade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText etNome;
    EditText etSobrenome;
    EditText etIdade;
    Button btnOutraAtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNome = (EditText) findViewById(R.id.etNome);
        etSobrenome = (EditText) findViewById(R.id.etSobrenome);
        etIdade = (EditText) findViewById(R.id.etIdade);
        btnOutraAtv = (Button) findViewById(R.id.btnProximaAtividade);

        btnOutraAtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intencao = new Intent(MainActivity.this, Main2Activity.class);

                Bundle extras = new Bundle();
                extras.putString("nome", etNome.getText().toString());
                extras.putString("sobrenome", etSobrenome.getText().toString());
                extras.putInt("idade", Integer.parseInt( etIdade.getText().toString() ) );

                intencao.putExtras(extras);

                startActivity(intencao);
            }
        }); //fim do ClickListener

        //-------------------- FIM AULA PASSAGEM PARAMETROS ------------------------

        //AULA Guardiao do Texto

        EditText etTextoDigitado = (EditText) findViewById(R.id.etTextoDigitado);
        final TextView tvTextotrocado = (TextView) findViewById(R.id.tvTextoTrocado);

        etTextoDigitado.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tvTextotrocado.setText(editable);
            }
        });

    }
}
