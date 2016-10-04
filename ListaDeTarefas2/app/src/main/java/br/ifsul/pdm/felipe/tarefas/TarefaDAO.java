package br.ifsul.pdm.felipe.tarefas;

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
public class TarefaDAO {
    private SQLiteDatabase database;
    private MeuDBHelper dbHelper;
    private String[] todasColunas ={MeuDBHelper.COLUMN_ID, MeuDBHelper.COLUMN_TAREFA};

    public TarefaDAO(Context context){
        dbHelper = new MeuDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public Tarefas criarTarefa(String tarefa) {
        ContentValues values = new ContentValues();
        values.put(MeuDBHelper.COLUMN_TAREFA, tarefa);

        long insertId = database.insert(MeuDBHelper.TABLE_TAREFAS, null, values);

        Cursor cursor = database.query(MeuDBHelper.TABLE_TAREFAS, todasColunas, MeuDBHelper.COLUMN_ID + " = " + insertId, null, null, null, null);

        cursor.moveToFirst();
        Tarefas novaTarefa = cursor2Tarefa(cursor);
        cursor.close();
        return novaTarefa;
    }

    public List<Tarefas> pegarTodasTarefas(){
        List<Tarefas> tarefas = new ArrayList<Tarefas>();
        Cursor cursor = database.query(MeuDBHelper.TABLE_TAREFAS, todasColunas, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Tarefas tarefa = cursor2Tarefa(cursor);
            tarefas.add(tarefa);
            cursor.moveToNext();
        }

        cursor.close();
        return tarefas;
    }

    public void excluirTarefa(Tarefas tarefa){
        long id = tarefa.id;
        database.delete(MeuDBHelper.TABLE_TAREFAS, MeuDBHelper.COLUMN_ID + " = " + id, null);
    }

    private Tarefas cursor2Tarefa(Cursor cursor) {
        Tarefas tarefa = new Tarefas(cursor.getLong(0), cursor.getString(1));
        return tarefa;
    }
}
