package com.example.pc.listadecomprasinf4at;

/**
 * Created by PC on 12/05/2016.
 */
public class Produto {

    public boolean checado;
    public String nomeProduto;
    public Double valor;

    Produto (String nomeProduto, Double valor) {
        this.nomeProduto = nomeProduto;
        this.valor = valor;
        this.checado = false;
    }

    Produto () {
    }

}
