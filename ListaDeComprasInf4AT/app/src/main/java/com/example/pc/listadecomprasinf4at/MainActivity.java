package com.example.pc.listadecomprasinf4at;

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

        produtos.add(new Produto("Arroz", 3.5));
        produtos.add(new Produto("Feijao", 2.5));
        produtos.add(new Produto("Leite", 2.99));
        produtos.add(new Produto("Carne", 22.59));
        produtos.add(new Produto("Pao", 6.79));
        produtos.add(new Produto("Maca", 4.5));
        produtos.add(new Produto("Tomate", 2.5));
        produtos.add(new Produto("Cebola", 4.5));produtos.add(new Produto("Arroz", 3.5));
        produtos.add(new Produto("Feijao", 2.5));
        produtos.add(new Produto("Leite", 2.99));
        produtos.add(new Produto("Carne", 22.59));
        produtos.add(new Produto("Pao", 6.79));
        produtos.add(new Produto("Maca", 4.5));
        produtos.add(new Produto("Tomate", 2.5));
        produtos.add(new Produto("Cebola", 4.5));produtos.add(new Produto("Arroz", 3.5));
        produtos.add(new Produto("Feijao", 2.5));
        produtos.add(new Produto("Leite", 2.99));
        produtos.add(new Produto("Carne", 22.59));
        produtos.add(new Produto("Pao", 6.79));
        produtos.add(new Produto("Maca", 4.5));
        produtos.add(new Produto("Tomate", 2.5));
        produtos.add(new Produto("Cebola", 4.5));produtos.add(new Produto("Arroz", 3.5));
        produtos.add(new Produto("Feijao", 2.5));
        produtos.add(new Produto("Leite", 2.99));
        produtos.add(new Produto("Carne", 22.59));
        produtos.add(new Produto("Pao", 6.79));
        produtos.add(new Produto("Maca", 4.5));
        produtos.add(new Produto("Tomate", 2.5));
        produtos.add(new Produto("Cebola", 4.5));

        ArrayAdapter<Produto> adpProdutos =
                new AdaptadorLista(this, R.layout.lista, produtos);

        ListView lv = (ListView) findViewById(R.id.lvListaCompras);
        lv.setAdapter(adpProdutos);

    }
}
