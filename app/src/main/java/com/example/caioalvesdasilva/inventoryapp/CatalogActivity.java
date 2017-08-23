package com.example.caioalvesdasilva.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.caioalvesdasilva.inventoryapp.data.CarContract;

public class CatalogActivity extends AppCompatActivity {

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

        // Perform a query on the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to access the pet data.
        Cursor cursor = getContentResolver().query(
                CarContract.CarEntry.CONTENT_URI,   // The content URI of the words table
                projection,             // The columns to return for each row
                null,                   // Selection criteria
                null,                   // Selection criteria
                null);                  // The sort order for the returned rows

        // Find the ListView which will be populated with the pet data
        ListView petListView = (ListView) findViewById(R.id.list);

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        CarCursorAdapter adapter = new CarCursorAdapter(this, cursor);

        // Attach the adapter to the ListView.
        petListView.setAdapter(adapter);
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertPet() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(CarContract.CarEntry.COLUMN_CAR_BRAND, "Volkswagen");
        values.put(CarContract.CarEntry.COLUMN_CAR_MODEL, "Golf");
        values.put(CarContract.CarEntry.COLUMN_CAR_YEAR, "2002");
        values.put(CarContract.CarEntry.COLUMN_CAR_ENGINE, 2.0);
        values.put(CarContract.CarEntry.COLUMN_CAR_FUEL, CarContract.CarEntry.FUEL_GASOLINE);
        values.put(CarContract.CarEntry.COLUMN_CAR_MILEAGE, 100000);

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(CarContract.CarEntry.CONTENT_URI, values);
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
