package com.example.brenodm.clientvideointerativo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brenodm on 05/06/17.
 */

class DB_Helper extends SQLiteOpenHelper {

    private static final String db_name = "tags_times";
    private static final int db_version = 12;

    public DB_Helper(Context context) {
        super(context, db_name, null, db_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String sqlCreateTableTraining = "CREATE TABLE tags("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "tag TEXT,"
                    + "time INTEGER"
                    + ")";

            db.execSQL(sqlCreateTableTraining);
        } catch (Exception e) {
            Log.e("MySQL Create Error", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlDropTableTraining = "DROP TABLE tags";
        db.execSQL(sqlDropTableTraining);
        onCreate(db);
    }

    public void insertTags(TagsList_GetterSetter training) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put("tag", training.getTag_item());
            cv.put("time", training.getTime_item());


            db.insert("tags", null, cv);
            db.close();
        } catch (Exception e) {
            Log.e("MySQL Insert Error", e.getMessage());
        }
    }

    /*public boolean deleteTraining(String name) {

        boolean result = false;

        String query = "Select * FROM training WHERE " + COLUMN_PRODUCTNAME + " =  \"" + productname + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Product product = new Product();

        if (cursor.moveToFirst()) {
            product.setID(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_PRODUCTS, COLUMN_ID + " = ?",
                    new String[] { String.valueOf(product.getID()) });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }*/

    public List<TagsList_GetterSetter> selectTags() {
        List<TagsList_GetterSetter> listTags = new ArrayList<TagsList_GetterSetter>();

        SQLiteDatabase db = getReadableDatabase();
        String sqlSelectTrainings = "SELECT * FROM tags";
        Cursor c = db.rawQuery(sqlSelectTrainings, null);

        if (c.moveToFirst()) {
            do {
                TagsList_GetterSetter tags = new TagsList_GetterSetter();

                tags.setId(c.getInt(0));
                tags.setTag_item(c.getString(1));
                tags.setTime_item(c.getString(2));


                listTags.add(tags);
            } while (c.moveToNext());
        }
        db.close();
        return listTags;
    }
}
