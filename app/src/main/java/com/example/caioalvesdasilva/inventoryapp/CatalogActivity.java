package com.example.caioalvesdasilva.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.caioalvesdasilva.inventoryapp.data.CarContract;
import com.example.caioalvesdasilva.inventoryapp.data.CarDbHelper;

public class CatalogActivity extends AppCompatActivity {

    /** Database helper that will provide us access to the database */
    private CarDbHelper mDbHelper;


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

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new CarDbHelper(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        CarDbHelper mDbHelper = new CarDbHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                CarContract.CarEntry._ID,
                CarContract.CarEntry.COLUMN_CAR_BRAND,
                CarContract.CarEntry.COLUMN_CAR_MODEL,
                CarContract.CarEntry.COLUMN_CAR_YEAR,
                CarContract.CarEntry.COLUMN_CAR_ENGINE,
                CarContract.CarEntry.COLUMN_CAR_FUEL,
                CarContract.CarEntry.COLUMN_CAR_MILEAGE };

        // Perform a query on the pets table
        Cursor cursor = db.query(
                CarContract.CarEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        TextView displayView = (TextView) findViewById(R.id.text_view_pet);

        try {
            // Create a header in the Text View that looks like this:
            //
            // The pets table contains <number of rows in Cursor> pets.
            // _id - name - breed - gender - weight
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("The pets table contains " + cursor.getCount() + " pets.\n\n");
            displayView.append(CarContract.CarEntry._ID + " - " +
                    CarContract.CarEntry.COLUMN_CAR_BRAND + " - " +
                    CarContract.CarEntry.COLUMN_CAR_MODEL + " - " +
                    CarContract.CarEntry.COLUMN_CAR_YEAR + " - " +
                    CarContract.CarEntry.COLUMN_CAR_ENGINE + " - " +
                    CarContract.CarEntry.COLUMN_CAR_FUEL + " - " +
                    CarContract.CarEntry.COLUMN_CAR_MILEAGE + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(CarContract.CarEntry._ID);
            int brandColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_BRAND);
            int modelColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_MODEL);
            int yearColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_YEAR);
            int engineColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_ENGINE);
            int fuelColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_FUEL);
            int mileageColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_MILEAGE);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentBrand = cursor.getString(brandColumnIndex);
                String currentModel = cursor.getString(modelColumnIndex);
                int currentYear = cursor.getInt(yearColumnIndex);
                float currentEngine = cursor.getFloat(engineColumnIndex);
                int currentFuel = cursor.getInt(fuelColumnIndex);
                int currentMileage = cursor.getInt(mileageColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentBrand + " - " +
                        currentModel + " - " +
                        currentYear + " - " +
                        currentEngine + " - " +
                        currentFuel + " - " +
                        currentMileage));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertPet() {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(CarContract.CarEntry.COLUMN_CAR_BRAND, "Volkswagen");
        values.put(CarContract.CarEntry.COLUMN_CAR_MODEL, "Golf");
        values.put(CarContract.CarEntry.COLUMN_CAR_YEAR, "2002");
        values.put(CarContract.CarEntry.COLUMN_CAR_ENGINE, 2.0);
        values.put(CarContract.CarEntry.COLUMN_CAR_FUEL, CarContract.CarEntry.FUEL_GASOLINE);
        values.put(CarContract.CarEntry.COLUMN_CAR_MILEAGE, 100000);

        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.
        long newRowId = db.insert(CarContract.CarEntry.TABLE_NAME, null, values);
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
                insertPet();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
