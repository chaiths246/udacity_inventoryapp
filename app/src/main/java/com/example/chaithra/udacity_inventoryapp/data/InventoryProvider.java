package com.example.chaithra.udacity_inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by chaithra on 1/31/18.
 */

public class InventoryProvider extends ContentProvider {
    private InventoryDbHelper mDbHelper;
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    private static final int ITEMS = 100;
    private static final int ITEM_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(Inventory_Contract.CONTENT_AUTHORITY, Inventory_Contract.PATH_ITEMS, ITEMS);
        sUriMatcher.addURI(Inventory_Contract.CONTENT_AUTHORITY, Inventory_Contract.PATH_ITEMS + "/#", ITEM_ID);
    }
    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                cursor = database.query(Inventory_Contract.ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEM_ID:
                selection = Inventory_Contract.ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(Inventory_Contract.ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("cannot querry unknown URI " + uri);


        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
         switch (match) {
            case ITEMS:
                return insertInventory(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
         }
    }

    private Uri insertInventory(Uri uri, ContentValues values) {
        String image = values.getAsString(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_IMAGE);
        if (image == null) {
            throw new IllegalArgumentException("Item requires a image");

        }
        String name = values.getAsString(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Item requires a name");

        }
        Integer price = values.getAsInteger(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Item requires valid Price");
        }

        Integer number_of_items = values.getAsInteger(Inventory_Contract.ItemEntry.COLUMN_NUMBER_OF_PRODUCTS);
        if (number_of_items != null && number_of_items < 0) {
            throw new IllegalArgumentException("Item requires valid Numbers");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(Inventory_Contract.ItemEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case ITEMS:
                rowsDeleted = database.delete(Inventory_Contract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                selection = Inventory_Contract.ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(Inventory_Contract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return updateItem(uri, values, selection, selectionArgs);
            case ITEM_ID:
                // For the Inventory_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = Inventory_Contract.ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_IMAGE)) {
            String image = values.getAsString(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_IMAGE);
            if (image == null) {
                throw new IllegalArgumentException("Item requires a image");
            }
        }
        if (values.containsKey(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }

        if (values.containsKey(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Item requires valid price");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        if (values.containsKey(Inventory_Contract.ItemEntry.COLUMN_NUMBER_OF_PRODUCTS)) {
            Integer number_of_items = values.getAsInteger(Inventory_Contract.ItemEntry.COLUMN_NUMBER_OF_PRODUCTS);
            if (number_of_items != null && number_of_items < 0) {
                throw new IllegalArgumentException("Item requires valid number");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(Inventory_Contract.ItemEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return Inventory_Contract.ItemEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return Inventory_Contract.ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
