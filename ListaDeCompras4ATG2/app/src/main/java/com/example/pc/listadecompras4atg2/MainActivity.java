package com.example.pc.listadecompras4atg2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Produto> produtos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        produtos = new ArrayList<Produto>();

        produtos.add(new Produto("Arroz", 2.50));
        produtos.add(new Produto("Feijao", 3.50));
        produtos.add(new Produto("Leite", 3.00));
        produtos.add(new Produto("Carne", 22.70));
        produtos.add(new Produto("Pao", 6.00));
        produtos.add(new Produto("Tomate", 2.50));
        produtos.add(new Produto("Cebola", 1.50));
        produtos.add(new Produto("Batata", 3.30));
        produtos.add(new Produto("Arroz", 2.50));
        produtos.add(new Produto("Feijao", 3.50));
        produtos.add(new Produto("Leite", 3.00));
        produtos.add(new Produto("Carne", 22.70));
        produtos.add(new Produto("Pao", 6.00));
        produtos.add(new Produto("Tomate", 2.50));
        produtos.add(new Produto("Cebola", 1.50));
        produtos.add(new Produto("Batata", 3.30));
        produtos.add(new Produto("Arroz", 2.50));
        produtos.add(new Produto("Feijao", 3.50));
        produtos.add(new Produto("Leite", 3.00));
        produtos.add(new Produto("Carne", 22.70));
        produtos.add(new Produto("Pao", 6.00));
        produtos.add(new Produto("Tomate", 2.50));
        produtos.add(new Produto("Cebola", 1.50));
        produtos.add(new Produto("Batata", 3.30));
        produtos.add(new Produto("Arroz", 2.50));
        produtos.add(new Produto("Feijao", 3.50));
        produtos.add(new Produto("Leite", 3.00));
        produtos.add(new Produto("Carne", 22.70));
        produtos.add(new Produto("Pao", 6.00));
        produtos.add(new Produto("Tomate", 2.50));
        produtos.add(new Produto("Cebola", 1.50));
        produtos.add(new Produto("Batata", 3.30));

        ArrayAdapter<Produto> adpProduto =
                new Adaptador(this, R.layout.lista, produtos);

        ListView lv = (ListView) findViewById(R.id.lvProduto);
        lv.setAdapter(adpProduto);



    }
}
