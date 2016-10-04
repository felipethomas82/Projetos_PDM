package br.ifsul.pdm.felipe.tarefas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by PC on 08/06/2015.
 */
public class MeuDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_TAREFAS = "tarefas";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TAREFA = "tarefa";

    public static final String DATABASE_NAME = "arquivodobanco.bd";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_CREATE = "create table "
            + TABLE_TAREFAS + "("
            + COLUMN_ID + " integer primary key autoincrement,  "
            + COLUMN_TAREFA + " text not null);";

    public MeuDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAREFAS);
        onCreate(db);
    }
}
