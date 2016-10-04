package provapdm.jeansarlon.fidelize;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "database.bd";
    public static final int DATABASE_VERSION = 4;

    public static final String TABLE_COMPANY = "company";
    public static final String COMPANY_ID = "_id";
    public static final String COMPANY_NAME = "name";

    public static final String TABLE_WALLET = "wallet";
    public static final String WALLET_COMPANY_ID = "_company_id";
    public static final String WALLET_AMOUNT = "amount";


    public static final String COMPANY_CREATE = "create table "
            + TABLE_COMPANY + "("
                + COMPANY_ID + " integer primary key autoincrement,  "
                + COMPANY_NAME + " text not null);";


    public static final String WALLET_CREATE = "create table "
            + TABLE_WALLET + "("
                + WALLET_COMPANY_ID + " integer primary key autoincrement,  "
                + WALLET_AMOUNT + " integer not null, "
                +"FOREIGN KEY(" + WALLET_COMPANY_ID + ") REFERENCES " +  TABLE_COMPANY + "("+COMPANY_ID+")"
            +");";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(COMPANY_CREATE);
        db.execSQL(WALLET_CREATE);

        db.execSQL(" INSERT INTO  company(_id,name) VALUES (1,'Vertice')");
        db.execSQL(" INSERT INTO  company(_id,name)  VALUES (2,'Hangar')");
        db.execSQL(" INSERT INTO  company(_id,name)  VALUES (3,'Gang')");
        db.execSQL(" INSERT INTO  company(_id,name)  VALUES (4,'Jump')");
        db.execSQL(" INSERT INTO  company(_id,name)  VALUES (5,'Lebes')");
        db.execSQL(" INSERT INTO  company(_id,name)  VALUES (6, 'Boticário')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPANY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALLET);
        onCreate(db);
    }
}