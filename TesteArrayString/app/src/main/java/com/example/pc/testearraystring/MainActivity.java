package com.example.pc.testearraystring;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        SharedPreferences preferencias = this.getSharedPreferences("preferencia", MODE_PRIVATE);
        if (preferencias.getString("email", null) != null && !preferencias.getString("email", null).isEmpty() ){
            //chama outra activity
            Intent intent = new Intent(this, Main2Activity.class);
            startActivity(intent);
        }

        //salvar preferencia - email
        Button btnSalvarPreferencia = (Button) findViewById(R.id.button);
        btnSalvarPreferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ((EditText) findViewById(R.id.editText)).getText().toString();
                SharedPreferences preferencia = context.getSharedPreferences("preferencia", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferencia.edit();
                editor.putString("email", email);
                editor.commit();
            }
        });


        List<String> restricoes = new ArrayList<String>();
        restricoes.add("Ovo");
        restricoes.add("Veganismo");

        ((TextView) findViewById(R.id.textView)).setText(restricoes.toString());
    }
}
