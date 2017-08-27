package com.example.caioalvesdasilva.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.caioalvesdasilva.inventoryapp.data.CarContract;

import static com.example.caioalvesdasilva.inventoryapp.data.CarContract.CarEntry.COLUMN_CAR_QUANTITY;
import static com.example.caioalvesdasilva.inventoryapp.data.CarContract.CarEntry.CONTENT_URI;

public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the car data loader */
    private static final int CAR_LOADER = 0;

    /** Adapter for the ListView */
    CarCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the car data
        ListView petListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of car data in the Cursor.
        // There is no car data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new CarCursorAdapter(this, null);
        petListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link CarEntry#CONTENT_URI}.
                // For example, the URI would be
                // "content://com.example.caioalvesdasilva.inventoryapp/cars/2"
                // if the car with ID 2 was clicked on.
                Uri currentPetUri = ContentUris.withAppendedId(CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentPetUri);

                // Launch the {@link EditorActivity} to display the data for the current car.
                startActivity(intent);
            }
        });
        // Kick off the loader
        getLoaderManager().initLoader(CAR_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded car data into the database. For debugging purposes only.
     */
    private void insertCar() {

        // Create a ContentValues object where column model are the keys,
        // and Golf's csr attributes are the values.
        Uri path = Uri.parse("android.resource://com.example.caioalvesdasilva.inventoryapp/"
                + R.drawable.img_golf_mk7);
        String imgPath = path.toString();

        ContentValues values = new ContentValues();
        values.put(CarContract.CarEntry.COLUMN_CAR_BRAND, "Volkswagen");
        values.put(CarContract.CarEntry.COLUMN_CAR_MODEL, "Golf Mk7");
        values.put(CarContract.CarEntry.COLUMN_CAR_YEAR, "2015");
        values.put(CarContract.CarEntry.COLUMN_CAR_ENGINE, "1.8 TSI");
        values.put(CarContract.CarEntry.COLUMN_CAR_FUEL, CarContract.CarEntry.FUEL_GASOLINE);
        values.put(CarContract.CarEntry.COLUMN_CAR_QUANTITY, 1);
        values.put(CarContract.CarEntry.COLUMN_CAR_PRICE, 5000.00);
        values.put(CarContract.CarEntry.COLUMN_CAR_IMAGE, imgPath);
        values.put(CarContract.CarEntry.COLUMN_SUPPLIER_NAME, "Caio");
        values.put(CarContract.CarEntry.COLUMN_SUPPLIER_EMAIL, "caiopimentel8@gmail.com");
        values.put(CarContract.CarEntry.COLUMN_CAR_MILEAGE, 1000);

        // Insert a new row for Golf into the provider using the ContentResolver.
        // Use the {@link CarEntry#CONTENT_URI} to indicate that we want to insert
        // into the Cars database table.
        // Receive the new content URI that will allow us to access Golf's data in the future.
        getContentResolver().insert(CONTENT_URI, values);
    }

    /**
     * Helper method to delete all car in the database.
     */
    private void deleteAllCars() {
        int rowsDeleted = getContentResolver().delete(CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from car database");
    }


    /**
     * Prompt the user to confirm that they want to delete this car.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the car.
                deleteAllCars();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the car.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                // Do nothing for now
                insertCar();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                CarContract.CarEntry._ID,
                CarContract.CarEntry.COLUMN_CAR_BRAND,
                CarContract.CarEntry.COLUMN_CAR_MODEL,
                CarContract.CarEntry.COLUMN_CAR_ENGINE,
                CarContract.CarEntry.COLUMN_CAR_QUANTITY,
                CarContract.CarEntry.COLUMN_CAR_PRICE
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link CarCursorAdapter} with this new cursor containing updated car data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
