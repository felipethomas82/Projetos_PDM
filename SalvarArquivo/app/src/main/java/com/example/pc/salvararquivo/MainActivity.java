package com.example.pc.salvararquivo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    String NOME_DO_ARQUIVO = "arquivo";
    String string = "hello world";
    EditText entrada1;
    Button btnSalvar;
    Button btnFechar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        entrada1 = (EditText) findViewById(R.id.entrada1);
        btnSalvar = (Button) findViewById(R.id.btnSalvar);
        btnFechar = (Button) findViewById(R.id.btnFechar);
        
        string = lerArquivoDoArmazenamentoInterno();
        
        if (string != null) {
            entrada1.setText(string);
        } else {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }


    public void onClickSalvar(View v) {
        FileOutputStream fos;
        try {
            fos = openFileOutput(NOME_DO_ARQUIVO, Context.MODE_PRIVATE);
            string = entrada1.getText().toString();
            fos.write(string.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String lerArquivoDoArmazenamentoInterno() {
        String eol = System.getProperty("line.separator");
        BufferedReader input = null;
        try {
            input = new BufferedReader(new InputStreamReader(openFileInput(NOME_DO_ARQUIVO)));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = input.readLine()) != null) {
                buffer.append(line + eol);
            }
            input.close();
            return buffer.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
