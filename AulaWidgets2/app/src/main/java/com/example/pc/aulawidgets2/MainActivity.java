package com.example.pc.aulawidgets2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText etIdade;
    TextView tvIdadeDigitada;
    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etIdade = (EditText) findViewById(R.id.etIdade);
        tvIdadeDigitada = (TextView) findViewById(R.id.txIdadeDigitada);
        btnOk = (Button) findViewById(R.id.btnOk);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idade = etIdade.getText().toString();
                idade += " anos";
                tvIdadeDigitada.setText(idade);
            }
        });
    }
}
