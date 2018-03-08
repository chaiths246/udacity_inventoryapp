package com.example.chaithra.udacity_inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.chaithra.udacity_inventoryapp.data.InventoryDbHelper;
import com.example.chaithra.udacity_inventoryapp.data.Inventory_Contract;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private InventoryDbHelper mDbHelper;
    private static final int ITEM_LOADER = 0;
    ItemCursorAdapter mcursoradapter;
    private ImageView imageView;
    private ListView itemListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        itemListView = (ListView) findViewById(R.id.list_inventary);
        View emptyView = findViewById(R.id.empty_view);
        imageView = findViewById(R.id.empty_shelter_image);
        itemListView.setEmptyView(emptyView);
        mcursoradapter = new ItemCursorAdapter(this, null);
        itemListView.setAdapter(mcursoradapter);
        getLoaderManager().initLoader(ITEM_LOADER,null,this);
        mDbHelper = new InventoryDbHelper(this);
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("Udacity","onItemClick "+id);
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentitemUri = ContentUris.withAppendedId(Inventory_Contract.ItemEntry.CONTENT_URI, id);
                intent.setData(currentitemUri);
                startActivity(intent);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
    }
    public void clickOnSale(long id, int quantity) {

            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            int newQuantity = 0;
            if (quantity > 0) {
                newQuantity = quantity -1;
            }
            ContentValues values = new ContentValues();
            values.put(Inventory_Contract.ItemEntry.COLUMN_NUMBER_OF_PRODUCTS, newQuantity);
            String selection = Inventory_Contract.ItemEntry._ID + "=?";
            String[] selectionArgs = new String[] { String.valueOf(id) };
        getContentResolver().update(Inventory_Contract.ItemEntry.CONTENT_URI,
                    values, selection, selectionArgs);

        }


    private void insertItem() {
        ContentValues values = new ContentValues();
        values.put(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_NAME, "Toto");
        values.put(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_IMAGE, imageViewToByte(imageView));
        values.put(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_PRICE, 9);
        values.put(Inventory_Contract.ItemEntry.COLUMN_NUMBER_OF_PRODUCTS, 7);
        Uri newUri = getContentResolver().insert(Inventory_Contract.ItemEntry.CONTENT_URI, values);
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mainactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertItem();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllInventory();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String[] projection = {
                Inventory_Contract.ItemEntry._ID,
                Inventory_Contract.ItemEntry.COLUMN_PRODUCT_NAME,
                Inventory_Contract.ItemEntry.COLUMN_PRODUCT_PRICE,
                Inventory_Contract.ItemEntry.COLUMN_NUMBER_OF_PRODUCTS,
                Inventory_Contract.ItemEntry.COLUMN_PRODUCT_IMAGE};
        return new CursorLoader(this, Inventory_Contract.ItemEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mcursoradapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mcursoradapter.swapCursor(null);

    }
    private void deleteAllInventory() {
                int rowsDeleted = getContentResolver().delete(Inventory_Contract.ItemEntry.CONTENT_URI, null, null);
                Log.v("CatalogActivity", rowsDeleted + " rows deleted from Inventory database");
            }

}

