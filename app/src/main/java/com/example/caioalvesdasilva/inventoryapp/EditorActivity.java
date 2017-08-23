package com.example.caioalvesdasilva.inventoryapp;

/**
 * Created by caio.alves.da.silva on 21/08/2017.
 */

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.caioalvesdasilva.inventoryapp.data.CarContract;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

    /** EditText field to enter the pet's name */
    private EditText mBrandEditText;

    /** EditText field to enter the pet's breed */
    private EditText mModelEditText;

    private EditText mYearEditText;

    private EditText mEngineEditTex;

    /** EditText field to enter the pet's weight */
    private EditText mMileageEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mFuelSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mFuel = CarContract.CarEntry.FUEL_GASOLINE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pet or editing an existing one.
        Intent intent = getIntent();
        Uri currentPetUri = intent.getData();

        // If the intent DOES NOT contain a pet content URI, then we know that we are
        // creating a new pet.
        if (currentPetUri == null) {
            // This is a new pet, so change the app bar to say "Add a Pet"
            setTitle(getString(R.string.editor_activity_title_new_car));
        } else {
            // Otherwise this is an existing pet, so change app bar to say "Edit Pet"
            setTitle(getString(R.string.editor_activity_title_edit_car));
        }


        // Find all relevant views that we will need to read user input from
        mBrandEditText = (EditText) findViewById(R.id.edit_car_brand);
        mModelEditText = (EditText) findViewById(R.id.edit_car_model);
        mYearEditText = (EditText) findViewById(R.id.edit_car_year);
        mEngineEditTex = (EditText) findViewById(R.id.edit_car_engine);

        mMileageEditText = (EditText) findViewById(R.id.edit_car_mileage);
        mFuelSpinner = (Spinner) findViewById(R.id.spinner_fuel);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mFuelSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mFuelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mFuel = 1; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mFuel = 2; // Female
                    } else {
                        mFuel = 0; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mFuel = 0; // Unknown
            }
        });
    }

    /**
     * Get user input from editor and save new pet into database.
     */
    private void insertPet() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String brandString = mBrandEditText.getText().toString().trim();
        String modelString = mModelEditText.getText().toString().trim();
        String yearString = mYearEditText.getText().toString().trim();
        String engineString = mEngineEditTex.getText().toString().trim();

        String mileageString = mMileageEditText.getText().toString().trim();
        int mileage = Integer.parseInt(mileageString);
        int year = Integer.parseInt(yearString);
        int engine = (int) Float.parseFloat(engineString);

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(CarContract.CarEntry.COLUMN_CAR_BRAND, brandString);
        values.put(CarContract.CarEntry.COLUMN_CAR_MODEL, modelString);
        values.put(CarContract.CarEntry.COLUMN_CAR_YEAR, year);
        values.put(CarContract.CarEntry.COLUMN_CAR_ENGINE, engine);
        values.put(CarContract.CarEntry.COLUMN_CAR_FUEL, mFuel);
        values.put(CarContract.CarEntry.COLUMN_CAR_MILEAGE, mileage);


        // Insert a new pet into the provider, returning the content URI for the new pet.
        Uri newUri = getContentResolver().insert(CarContract.CarEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_pet_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Do nothing for now
                // Save pet to database
                insertPet();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}