package com.example.chaithra.udacity_inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by chaithra on 1/31/18.
 */

public class Inventory_Contract {
    private Inventory_Contract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.chaithra.udacity_inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "Items";

    public static final class ItemEntry implements BaseColumns {
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY +
                "/" + PATH_ITEMS;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);
        public final static String TABLE_NAME = "Products";
        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_PRODUCT_IMAGE = "image";
        public final static String COLUMN_PRODUCT_NAME = "name";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_NUMBER_OF_PRODUCTS = "number";


    }
}
