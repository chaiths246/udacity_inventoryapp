package com.example.chaithra.udacity_inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chaithra.udacity_inventoryapp.data.Inventory_Contract;

import java.io.ByteArrayInputStream;

/**
 * Created by chaithra on 1/31/18.
 */

public class ItemCursorAdapter extends CursorAdapter {

    private final MainActivity activity;
    public ItemCursorAdapter(MainActivity context, Cursor c) {
        super(context, c, 0 /* flags */);
        this.activity = context;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.listitem, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.list_name);
        TextView priceTextView = (TextView) view.findViewById(R.id.list_price);
        TextView numberIndex = (TextView) view.findViewById(R.id.list_number);
        ImageView imageView = (ImageView) view.findViewById(R.id.list_image);
        // Find the columns of Inventory attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_PRICE);
        int quantityindex = cursor.getColumnIndex(Inventory_Contract.ItemEntry.COLUMN_NUMBER_OF_PRODUCTS);
        int imageIndex = cursor.getColumnIndex(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_IMAGE);
        Button sale = (Button) view.findViewById(R.id.sale);
        // Read the Inventory attributes from the Cursor for the current Inventory
        String itemName = cursor.getString(nameColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);
        final String itemQuantity = cursor.getString(quantityindex);
        byte[] byteArray = cursor.getBlob(imageIndex);
        ByteArrayInputStream imageStream = new ByteArrayInputStream(byteArray);
        Bitmap theImage = BitmapFactory.decodeStream(imageStream);


        //  Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);

        // Update the TextViews with the attributes for the current Inventory
        nameTextView.setText(itemName);
        priceTextView.setText(itemPrice + " $");
        numberIndex.setText(itemQuantity);
        imageView.setImageBitmap(theImage);

        final long id = cursor.getLong(cursor.getColumnIndex(Inventory_Contract.ItemEntry._ID));
        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity=Integer.parseInt(itemQuantity);
                activity.clickOnSale(id,
                        quantity);
            }
        });

    }
    }
