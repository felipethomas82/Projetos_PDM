package br.ifsul.pdm.felipe.contatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 08/06/2015.
 */
public class ContatoDAO {
    private SQLiteDatabase database;
    private MeuDBHelper dbHelper;
    private String[] todasColunas ={MeuDBHelper.COLUMN_ID, MeuDBHelper.COLUMN_NOME, MeuDBHelper.COLUMN_TELEFONE, MeuDBHelper.COLUMN_EMAIL};

    public ContatoDAO(Context context){
        dbHelper = new MeuDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public Contato criarContato(String nome, String telefone, String email) {
        ContentValues values = new ContentValues();
        values.put(MeuDBHelper.COLUMN_NOME, nome);
        values.put(MeuDBHelper.COLUMN_TELEFONE, telefone);
        values.put(MeuDBHelper.COLUMN_EMAIL, email);

        long insertId = database.insert(MeuDBHelper.TABLE_CONTATOS, null, values);

        Cursor cursor = database.query(MeuDBHelper.TABLE_CONTATOS, todasColunas, MeuDBHelper.COLUMN_ID + " = " + insertId, null, null, null, null);

        cursor.moveToFirst();
        Contato novoContato = cursor2Contato(cursor);
        cursor.close();
        return novoContato;
    }

    public List<Contato> pegarTodosContatos(){
        List<Contato> contatos = new ArrayList<Contato>();
        Cursor cursor = database.query(MeuDBHelper.TABLE_CONTATOS, todasColunas, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Contato contato = cursor2Contato(cursor);
            contatos.add(contato);
            cursor.moveToNext();
        }

        cursor.close();
        return contatos;
    }

    public List<Contato> buscaContato(String nome, String email, String telefone){
        String filtro;
        filtro = MeuDBHelper.COLUMN_NOME + " like '%" + nome + "%' AND " +
                MeuDBHelper.COLUMN_TELEFONE + " like '%" + telefone + "%' AND " +
                MeuDBHelper.COLUMN_EMAIL + " like '%" + email + "%'";

        List<Contato> contatos = new ArrayList<Contato>();



        Cursor cursor = database.query(MeuDBHelper.TABLE_CONTATOS, todasColunas, filtro, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Contato contato = cursor2Contato(cursor);
            contatos.add(contato);
            cursor.moveToNext();
        }

        cursor.close();
        return contatos;
    }



    public void excluirContato(Contato contato){
        long id = contato.id;
        database.delete(MeuDBHelper.TABLE_CONTATOS, MeuDBHelper.COLUMN_ID + " = " + id, null);

        String aux = "palavra";
        int pos = 0;
        while (aux.indexOf("a", pos) != -1){

        };
    }

    private Contato cursor2Contato(Cursor cursor) {
        Contato contato = new Contato(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
        return contato;
    }
}
