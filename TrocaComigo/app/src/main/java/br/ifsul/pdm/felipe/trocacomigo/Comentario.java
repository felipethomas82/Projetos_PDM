package br.ifsul.pdm.felipe.trocacomigo;

/**
 * Created by PC on 12/07/2015.
 */
public class Comentario {
    protected String pathPhotoUser;
    protected String comment;
    protected String nomeUsuario;

    Comentario (String pathPhotoUser, String comment, String nomeUsuario){
        this.pathPhotoUser = pathPhotoUser;
        this.comment = comment;
        this.nomeUsuario = nomeUsuario;
    }
}
