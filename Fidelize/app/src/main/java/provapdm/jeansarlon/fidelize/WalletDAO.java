package provapdm.jeansarlon.fidelize;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class WalletDAO {
    private SQLiteDatabase database;
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private String[] allCollumns = {DBHelper.WALLET_COMPANY_ID, DBHelper.WALLET_AMOUNT};

    public WalletDAO(Context context){
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }


    public Wallet getItemWallet(long id){
        Wallet iTemWallet;

        try {

            List<Wallet> wallet = new ArrayList<Wallet>();
            String query = "SELECT "
                    + DBHelper.WALLET_COMPANY_ID
                    + ", "
                    + DBHelper.COMPANY_NAME+ ", "
                    + DBHelper.WALLET_AMOUNT
                    + " FROM "
                    + DBHelper.TABLE_COMPANY
                    + " INNER JOIN "
                    + DBHelper.TABLE_WALLET
                    + " ON "+ DBHelper.WALLET_COMPANY_ID
                    + " = " + DBHelper.COMPANY_ID+
                    " WHERE "+ DBHelper.COMPANY_ID + " = " + id;

            Cursor cursor = database.rawQuery(query, null);
            if(cursor.getCount() != 0) {
                cursor.moveToFirst();
                iTemWallet = cursor2Wallet(cursor);
                cursor.close();
                return iTemWallet;
            }
        } catch (Exception e) {
            System.out.println("ERROR " + e.toString());
        }
        return null;
    }


    public Boolean store(Wallet object) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.WALLET_COMPANY_ID, object.company_id);
        values.put(DBHelper.WALLET_AMOUNT, object.amount);
        try {
            long insertId = database.insert(DBHelper.TABLE_WALLET, null, values);
            return true;
        } catch (Exception e) {
            System.out.println("ERROR " + e.toString());
        }
        return false;
    }

    public Boolean update(Wallet object) {

        ContentValues values = new ContentValues();
        values.put(DBHelper.WALLET_AMOUNT, object.amount);
        try {
            database.update(DBHelper.TABLE_WALLET, values,DBHelper.WALLET_COMPANY_ID + "="+ object.company_id, null);
            return true;
        } catch (Exception e) {
            System.out.println("ERROR " + e.toString());
        }
        return false;
    }

    public List<Wallet> getAll() {
        List<Wallet> wallet = new ArrayList<Wallet>();
        String query = "SELECT " + DBHelper.WALLET_COMPANY_ID + ", " + DBHelper.COMPANY_NAME+ ", " + DBHelper.WALLET_AMOUNT + " FROM " + DBHelper.TABLE_COMPANY + " INNER JOIN " + DBHelper.TABLE_WALLET + " ON "+ DBHelper.WALLET_COMPANY_ID + " = " + DBHelper.COMPANY_ID  + " ORDER BY " + DBHelper.WALLET_AMOUNT + " DESC " + ";";
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Wallet item_wallet = cursor2Wallet(cursor);
            wallet.add(item_wallet);
            cursor.moveToNext();
        }

        cursor.close();
        return wallet;
    }


    private Wallet cursor2Wallet(Cursor cursor) {
        Wallet newwallet = new Wallet(cursor.getLong(0), cursor.getInt(2), cursor.getString(1));
        return newwallet;
    }
}
