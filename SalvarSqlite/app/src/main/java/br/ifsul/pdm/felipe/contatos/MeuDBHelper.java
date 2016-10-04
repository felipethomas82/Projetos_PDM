package br.ifsul.pdm.felipe.contatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by PC on 08/06/2015.
 */
public class MeuDBHelper extends SQLiteOpenHelper{

    public static final String TABLE_CONTATOS = "contatos";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NOME = "nome";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_TELEFONE = "telefone";

    public static final String DATABASE_NAME = "arquivodobanco.bd";
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_CREATE = "create table "
            + TABLE_CONTATOS + "("
            + COLUMN_ID + " integer primary key autoincrement,  "
            + COLUMN_NOME + " text not null, "
            + COLUMN_TELEFONE + " text not null, "
            + COLUMN_EMAIL + " text not null);";

    public MeuDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTATOS);
        onCreate(db);
    }
}
