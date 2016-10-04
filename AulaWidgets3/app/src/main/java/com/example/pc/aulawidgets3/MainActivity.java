package com.example.pc.aulawidgets3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText etIdade;
    Button btn;
    TextView tvIdadeDigitada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etIdade = (EditText) findViewById(R.id.etIdade);
        btn = (Button) findViewById(R.id.btnOk);
        tvIdadeDigitada = (TextView) findViewById(R.id.txIdadeDigitada);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idade = etIdade.getText().toString();
                idade += " anos";

                tvIdadeDigitada.setText(idade);
            }
        });
    }
}
