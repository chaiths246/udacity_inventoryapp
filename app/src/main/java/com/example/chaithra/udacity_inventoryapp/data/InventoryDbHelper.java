package com.example.chaithra.udacity_inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by chaithra on 1/31/18.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "Itemlist.db";
    private static final int DATABASE_VERSION = 1;
    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
                String SQL_CREATE_ITEM_TABLE = "CREATE TABLE " + Inventory_Contract.ItemEntry.TABLE_NAME + " ("
                + Inventory_Contract.ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Inventory_Contract.ItemEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + Inventory_Contract.ItemEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + Inventory_Contract.ItemEntry.COLUMN_PRODUCT_IMAGE + " BLOB, "
                + Inventory_Contract.ItemEntry.COLUMN_NUMBER_OF_PRODUCTS + " INTEGER NOT NULL DEFAULT 0);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_ITEM_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
