package com.example.pc.listadecompras4atg2;

/**
 * Created by PC on 12/05/2016.
 */
public class Produto {

    public String nome;
    public Double valor;
    public boolean checado;

    public Produto(String nome, Double valor) {
        this.nome = nome;
        this.valor = valor;
        this.checado = false;
    }
}
