package com.example.pc.listacomplexacomfiltro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lvPessoas;
    ArrayList<Pessoa> pessoas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvPessoas = (ListView) findViewById(R.id.lvPessoas);

        pessoas = new ArrayList<Pessoa>();
        pessoas.add(new Pessoa("Felipe", "felipe@felipe.com"));
        pessoas.add(new Pessoa("Fulano", "fulano@gmail.com"));
        pessoas.add(new Pessoa("Ciclano", "ciclano@gmail.com"));
        pessoas.add(new Pessoa("Fulano2", "fulano2@gmail.com"));
        pessoas.add(new Pessoa("Fulaninho", "fulaninho@gmail.com"));

        ArrayAdapter<Pessoa> adpPessoa = new Adaptador(this, R.layout.pessoa, pessoas);

        lvPessoas.setAdapter(adpPessoa);
    }
}
