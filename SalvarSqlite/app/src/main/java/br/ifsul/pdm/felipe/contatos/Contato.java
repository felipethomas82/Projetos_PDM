package br.ifsul.pdm.felipe.contatos;

/**
 * Created by PC on 08/06/2015.
 */
public class Contato {

    long id;
    String nome;
    String telefone;
    String email;

    public Contato(long id, String nome, String telefone, String email){
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
    }
}
