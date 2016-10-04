package com.example.pc.aulawidgets;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText etPeso;
    EditText etAltura;

    Button btnCalcular;

    TextView tvResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPeso = (EditText) findViewById(R.id.etPeso);
        etAltura = (EditText) findViewById(R.id.etAltura);

        tvResultado = (TextView) findViewById(R.id.txtResultado);

        btnCalcular = (Button) findViewById(R.id.btnCalcular);

        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double peso, altura, imc;

                peso = Double.valueOf(etPeso.getText().toString());

                altura = Double.valueOf(etAltura.getText().toString());

                imc = peso / (altura * altura);

                if (imc < 20)
                    tvResultado.setText("Abaixo do peso");
                else if ((imc >= 20) && (imc < 25))
                    tvResultado.setText("Peso normal");
                else if ((imc >= 25) && (imc < 30))
                    tvResultado.setText("Acima do peso");
                else if ((imc >= 30) && (imc < 34))
                    tvResultado.setText("Obeso");
                else
                    tvResultado.setText("Muito obeso");
            }
        });

        
    }
}
