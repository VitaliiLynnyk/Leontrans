package leontrans.leontranstm.basepart.favouritespart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_Version = 1;
    public static final String DATABASE_NAME = "LEONTRANS.db";
    public static final String MY_TABLE_TO_DO_LIST = "selected_list_item";
    public static final String TO_DO_LIST_COLUMN_ID = "id";
    public static final String TO_DO_LIST_COLUMN_NAME = "id_selected_item";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("LOG", "--- onCreate database ---");
        db.execSQL(
                "create table "+MY_TABLE_TO_DO_LIST + "(id integer  primary key, id_selected_item integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+MY_TABLE_TO_DO_LIST);
        onCreate(db);
    }

    public boolean insertContact (Integer id_selected_item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_selected_item", id_selected_item);
        db.insert(MY_TABLE_TO_DO_LIST, null, contentValues);
        return true;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, MY_TABLE_TO_DO_LIST);
        return numRows;
    }

    public Integer deleteContact (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(MY_TABLE_TO_DO_LIST,
                "id_selected_item=? ",
                new String[] { Integer.toString(id) });
    }
    public boolean checkIfExist(String fieldValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + MY_TABLE_TO_DO_LIST + " where " + TO_DO_LIST_COLUMN_NAME + " = " + fieldValue;

        Cursor res  = db.rawQuery(Query, null);

        if(res.getCount() <= 0){
            res.close();
            return false;
        }
        res.close();
        return true;
    }

    public ArrayList<DBinformation> getAllTODOLIST() {
        ArrayList<DBinformation> array_list = new ArrayList<DBinformation>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + MY_TABLE_TO_DO_LIST, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(new DBinformation(Integer.parseInt(res.getString(res.getColumnIndex(TO_DO_LIST_COLUMN_ID))),Integer.parseInt(res.getString(res.getColumnIndex(TO_DO_LIST_COLUMN_NAME)))));
            res.moveToNext();
        }
        return array_list;
    }
}
