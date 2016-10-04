package br.ifsul.pdm.felipe.listadecompras;

/**
 * Created by PC on 11/05/2015.
 */
public class Produto {

    //usar o checado para informar ao android qual produto foi clicado (checked)
    public boolean checado;
    public String nomeProduto;
    public Double valor;

    Produto (String nomeProduto, Double valor)
    {
        this.nomeProduto = nomeProduto;
        this.valor = valor;
        this.checado = false;
    }
}
