package provapdm.jeansarlon.fidelize;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CompanyDAO {
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] allCollumns ={DBHelper.COMPANY_ID, DBHelper.COMPANY_NAME};

    public CompanyDAO(Context context){
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public Company createCompany(String nome) {

        ContentValues values = new ContentValues();
        values.put(DBHelper.COMPANY_NAME, nome);

        long insertId = database.insert(DBHelper.TABLE_COMPANY, null, values);

        Cursor cursor = database.query(DBHelper.TABLE_COMPANY, allCollumns, DBHelper.COMPANY_ID + " = " + insertId, null, null, null, null);

        cursor.moveToFirst();
        Company newCompany = cursor2Company(cursor);
        cursor.close();
        return newCompany;
    }

    public List<Company> getAllCompanies(){
        List<Company> companies = new ArrayList<Company>();
        Cursor cursor = database.query(DBHelper.TABLE_COMPANY, allCollumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Company company = cursor2Company(cursor);
            companies.add(company);
            cursor.moveToNext();
        }

        cursor.close();
        return companies;
    }

    public void destroyCompany(Company company){
        long id = company.id;
        database.delete(DBHelper.TABLE_COMPANY, DBHelper.COMPANY_ID + " = " + id, null);
    }


    private Company cursor2Company(Cursor cursor) {
        Company company = new Company(cursor.getLong(0), cursor.getString(1));
        return company;
    }
}
