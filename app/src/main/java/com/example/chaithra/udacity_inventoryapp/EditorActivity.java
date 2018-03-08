package com.example.chaithra.udacity_inventoryapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v4.widget.CursorAdapter;

import com.example.chaithra.udacity_inventoryapp.data.Inventory_Contract;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by chaithra on 1/31/18.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText name;
    private EditText priceedit;
    Button increment;
    Button decrement;
    EditText qunatity;
    ImageView photo;
    Button uploadphoto;
    private Button btnOrderMore;
    private EditText edtxtSupplierEmail;
    private static final int EXISTING_ITEM_LOADER = 0;
    private Uri currentItemUri;
    final int REQUEST_CODE_GALLERY = 999;

    private boolean mItemHasChanged = false;


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.editor_activity);
        Intent intent = getIntent();
        if (intent != null) {
            currentItemUri = intent.getData();
            if (currentItemUri == null) {
                setTitle("Add Item");
                invalidateOptionsMenu();
            } else {
                setTitle("Edit Item");
                getSupportLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
            }
        }


        name = (EditText) findViewById(R.id.edit_name);
        priceedit = (EditText) findViewById(R.id.edit_price);
        edtxtSupplierEmail = (EditText) findViewById(R.id.edit_order_more);
        btnOrderMore = (Button) findViewById(R.id.btn_order_more);
        increment = (Button) findViewById(R.id.increment);
        decrement = (Button) findViewById(R.id.decrement);
        qunatity = (EditText) findViewById(R.id.quantity);
        photo = (ImageView) findViewById(R.id.image);
        uploadphoto = (Button) findViewById(R.id.upload);
        name.setOnTouchListener(mTouchListener);
        priceedit.setOnTouchListener(mTouchListener);
        qunatity.setOnTouchListener(mTouchListener);
        photo.setOnTouchListener(mTouchListener);
        uploadphoto.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(EditorActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditorActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_GALLERY
                    );
                }else{
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_CODE_GALLERY);
                }

            }
        });
        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sumOneToQuantity();
                mItemHasChanged = true;


            }
        });
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtractOneToQuantity();
                mItemHasChanged = true;
            }
        });

        btnOrderMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("EditorActivity", "Ordering More");
               String supplierMail =  edtxtSupplierEmail.getText().toString().trim();
                String nameString = name.getText().toString();
                String priceString = priceedit.getText().toString().trim();
                String quantityString = qunatity.getText().toString().trim();
                if (isUserinputValidated(nameString,priceString,quantityString)){
                    if (isEmailValidation(supplierMail)) {
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setData(Uri.parse("mailto:"));
                        emailIntent.setType("text/plain");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {supplierMail});
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Request From Inventory app");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Product Name: " + nameString +"\n" +
                                "Product price: " +priceString+ "\n" +
                                "Product quantity: " +quantityString+ "\n" +
                                "Total Amount: " +(Integer.parseInt(quantityString)*Integer.parseInt(priceString))+"\n\n"+
                                "Thank you \n Udacity");

                        try {
                            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                            finish();
                            Log.i("sending email...", "");
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(EditorActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(EditorActivity.this,"Enter valid email",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(EditorActivity.this,R.string.txt_enter_all_fields,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isEmailValidation(String supplierEmail) {
        return supplierEmail.length()>0 && supplierEmail.contains("@");
    }

    private void subtractOneToQuantity() {
        String previousValueString = qunatity.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            return;
        } else if (previousValueString.equals("0")) {
            return;
        } else {
            previousValue = Integer.parseInt(previousValueString);
            qunatity.setText(String.valueOf(previousValue - 1));
        }
    }

    private void sumOneToQuantity() {
        String previousValueString = qunatity.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        qunatity.setText(String.valueOf(previousValue + 1));
    }

    private boolean insertItem() {
        ContentValues values = new ContentValues();

        String nameString = name.getText().toString();
        String priceString = priceedit.getText().toString().trim();
        String quantityString = qunatity.getText().toString().trim();

        values.put(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(Inventory_Contract.ItemEntry.COLUMN_NUMBER_OF_PRODUCTS, quantityString);
        values.put(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_IMAGE, imageViewToByte(photo));

        if(isUserinputValidated(nameString,priceString,quantityString)) {
        if (currentItemUri == null) {

                Uri uri = getContentResolver().insert(Inventory_Contract.ItemEntry.CONTENT_URI, values);
                if (uri == null) {
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
                    return true;
                }

        } else {
            int rowsAffected = getContentResolver().update(currentItemUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "Update Item failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Update item successfull",
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        }else{
            Toast.makeText(this,R.string.txt_enter_all_fields,Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean isUserinputValidated(String nameString,String priceString,String quantityString) {
        return nameString.length() > 0 &&
                quantityString.length() > 0 &&
                priceString.length() > 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                // Exit activity
                if(insertItem()){
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                Inventory_Contract.ItemEntry._ID,
                Inventory_Contract.ItemEntry.COLUMN_PRODUCT_NAME,
                Inventory_Contract.ItemEntry.COLUMN_PRODUCT_PRICE,
                Inventory_Contract.ItemEntry.COLUMN_NUMBER_OF_PRODUCTS,
                Inventory_Contract.ItemEntry.COLUMN_PRODUCT_IMAGE};
        return new CursorLoader(EditorActivity.this, currentItemUri,
                projection,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            int nameColumnIndex = data.getColumnIndex(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = data.getColumnIndex(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_PRICE);
            int quantityindex = data.getColumnIndex(Inventory_Contract.ItemEntry.COLUMN_NUMBER_OF_PRODUCTS);
            int imageIndex = data.getColumnIndex(Inventory_Contract.ItemEntry.COLUMN_PRODUCT_IMAGE);


            // Read the pet attributes from the Cursor for the current pet
            String itemName = data.getString(nameColumnIndex);
            String itemPrice = data.getString(priceColumnIndex);
            String itemQuantity = data.getString(quantityindex);
            byte[] byteArray = data.getBlob(imageIndex);
            ByteArrayInputStream imageStream = new ByteArrayInputStream(byteArray);
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            name.setText(itemName);
            priceedit.setText(itemPrice);
            qunatity.setText(itemQuantity);
            photo.setImageBitmap(theImage);


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        name.setText("");
        priceedit.setText("");
        qunatity.setText("");
        photo.setImageBitmap(null);
    }


    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                photo.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (currentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteItem() {
        if (currentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(currentItemUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_item_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_item_successful), Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

}


