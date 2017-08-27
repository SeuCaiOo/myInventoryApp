package com.example.caioalvesdasilva.inventoryapp;

/**
 * Created by caio.alves.da.silva on 21/08/2017.
 */

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.caioalvesdasilva.inventoryapp.data.CarContract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Allows user to create a new car or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

   public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    /** Identifier for the car image data loader*/
    public static final int IMAGE_GALLERY_REQUEST = 20;

    /** Identifier for the car image URI loader*/
    private static final String STATE_IMAGE_URI = "STATE_IMAGE_URI";

    /** Identifier for the car data loader*/
    private static final int EXISTING_CAR_LOADER = 0;

    final Context mContext = this;

    /** Content URI for the existing car image(null if it's a new car)*/
    private Uri mImageUri;

    /** Image Path of the car fetched from the Uri*/
    private String imagePath;

    /** Bitmap value of the image fetched from the Uri */
    private Bitmap image;

    /** Content URI for the existing car (null if it's a new pet)*/
    private Uri mCurrentCarUri;

    /** EditText field to enter the car's brand */
    private EditText mBrandEditText;

    /** EditText field to enter the car's model*/
    private EditText mModelEditText;

    /** EditText field to enter the car's year*/
    private EditText mYearEditText;

    /** EditText field to enter the car's engine*/
    private EditText mEngineEditText;

    /** EditText field to enter the car's quantity*/
    private EditText mQuantityEditText;

    /** EditText field to enter the car's price*/
    private EditText mPriceEditText;

    /** ImageView field to enter the car's image*/
    private ImageView mImageView;

    /** EditText field to enter the car's mileage*/
    private EditText mMileageEditText;

    /** Spinner field to enter the car's fuel*/
    private Spinner mFuelSpinner;

    /** Button to add the car's image*/
    private Button mAddImageButton;

    /**Button to order more cars from the supplier*/
    private Button mOrder;

    /** EditText field to enter the Car supplier Name*/
    private EditText mContactNameEditText;

    /** EditText field to enter the Car supplier Email*/
    private EditText mContactEmailEditText;

    /** Fuel of the car. The possible values are:
     * 0 for gasoline fuel, 1 for alcohol, 2 for flex.*/
    private int mFuel = CarContract.CarEntry.FUEL_GASOLINE;

    /** Boolean flag that keeps track of whether the car has been edited (true) or not (false)*/
    private boolean mCarHasChanged = false;

    /** OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mCarHasChanged boolean to true.*/
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mCarHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pet or editing an existing one.
        Intent intent = getIntent();
        mCurrentCarUri = intent.getData();

        // If the intent DOES NOT contain a pet content URI, then we know that we are
        // creating a new car.
        if (mCurrentCarUri == null) {
            // This is a new car, so change the app bar to say "Add a Car"
            setTitle(getString(R.string.editor_activity_title_new_car));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a car that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing car, so change app bar to say "Edit Car"
            setTitle(getString(R.string.editor_activity_title_edit_car));

            // Initialize a loader to read the car data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_CAR_LOADER, null, this);
        }


        // Find all relevant views that we will need to read user input from
        mBrandEditText = (EditText) findViewById(R.id.edit_car_brand);
        mModelEditText = (EditText) findViewById(R.id.edit_car_model);
        mYearEditText = (EditText) findViewById(R.id.edit_car_year);
        mEngineEditText = (EditText) findViewById(R.id.edit_car_engine);
        mQuantityEditText = (EditText) findViewById(R.id.edit_car_quantity);
        mPriceEditText = (EditText) findViewById(R.id.edit_car_price);
        mAddImageButton = (Button) findViewById(R.id.add_image);
        mImageView = (ImageView) findViewById(R.id.image_car);
        mMileageEditText = (EditText) findViewById(R.id.edit_car_mileage);
        mFuelSpinner = (Spinner) findViewById(R.id.spinner_fuel);
        mOrder = (Button) findViewById(R.id.email_button);
        mContactNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mContactEmailEditText = (EditText) findViewById(R.id.edit_supplier_email);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mBrandEditText.setOnTouchListener(mTouchListener);
        mModelEditText.setOnTouchListener(mTouchListener);
        mYearEditText.setOnTouchListener(mTouchListener);
        mEngineEditText.setOnTouchListener(mTouchListener);
        mMileageEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mFuelSpinner.setOnTouchListener(mTouchListener);
        mOrder.setOnTouchListener(mTouchListener);
        mContactNameEditText.setOnTouchListener(mTouchListener);
        mContactEmailEditText.setOnTouchListener(mTouchListener);

        //Open camera when you press on Add image button
        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Invoke an implicit intent to open the photo gallery
                Intent openPhotoGallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                //Where do we find the data?
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);

                //Get a String of the pictureDirectoryPath
                String pictureDirectoryPath = pictureDirectory.getPath();

                //Get the Uri representation
                Uri data = Uri.parse(pictureDirectoryPath);

                //Set the data and type
                openPhotoGallery.setDataAndType(data, "image/*");

                //We will invoke this activity and get something back from it
                startActivityForResult(openPhotoGallery, IMAGE_GALLERY_REQUEST);
            }

        });

        //Open the email app to send a message with pre populated fields
        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Invoke an implicit intent to send an email
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

                String to = mContactEmailEditText.getText().toString();
                String modelName = mModelEditText.getText().toString();
                String brandName = mBrandEditText.getText().toString();
                String yearModel = mYearEditText.getText().toString();
                String subject = "Order: " + modelName + " of the " + brandName;
                String supplier = mContactNameEditText.getText().toString();
                String sep = System.getProperty("line.separator");
                String message = "Dear " + supplier + "," + sep +
                        "I would like to order another 10 cars from the model "
                        + modelName + " of the " + brandName + " - Year: " +
                        yearModel + "." + sep + "Greetings," + sep + "Caio";
                emailIntent.setData(Uri.parse("mailto:" + to));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, message);

                try {
                    startActivity(emailIntent);
                    finish();
                    Log.i(LOG_TAG, "Finished sending email...");
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(EditorActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }

        });
        setupSpinner();
    }

    /** Setup the dropdown spinner that allows the user to select the fuel of the car.*/
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter fuelSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_fuel_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        fuelSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mFuelSpinner.setAdapter(fuelSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mFuelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.fuel_alcohol))) {
                        mFuel = 1; // Alcohol
                    } else if (selection.equals(getString(R.string.fuel_flex))) {
                        mFuel = 2; // Flex
                    } else {
                        mFuel = 0; // Gasoline
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mFuel = 0; // Gasoline
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mImageUri != null)
            outState.putString(STATE_IMAGE_URI, mImageUri.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(STATE_IMAGE_URI) &&
                !savedInstanceState.getString(STATE_IMAGE_URI).equals("")) {
            mImageUri = Uri.parse(savedInstanceState.getString(STATE_IMAGE_URI));

            ViewTreeObserver viewTreeObserver = mImageView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mImageView.setImageBitmap(getBitmapFromUri(mImageUri, mContext, mImageView));
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //if we are here our request was successful
        if (requestCode == IMAGE_GALLERY_REQUEST && (resultCode == RESULT_OK)) {
            try {
                //this is the address of the image on the sd cards
                mImageUri = data.getData();
                int takeFlags = data.getFlags();
                takeFlags &= (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                imagePath = mImageUri.toString();
                //Declare a stream to read the data from the card
                InputStream inputStream;
                //We are getting an input stream based on the Uri of the image
                inputStream = getContentResolver().openInputStream(mImageUri);
                //Get a bitmap from the stream
                image = BitmapFactory.decodeStream(inputStream);
                //Show the image to the user
                mImageView.setImageBitmap(image);
                imagePath = mImageUri.toString();
                try {
                    getContentResolver().takePersistableUriPermission(mImageUri, takeFlags);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                mImageView.setImageBitmap(getBitmapFromUri(mImageUri, mContext, mImageView));

            } catch (Exception e) {
                e.printStackTrace();
                //Show the user a Toast mewssage that the Image is not available
                Toast.makeText(EditorActivity.this, "Unable to open image", Toast.LENGTH_LONG).show();
            }
        }
    }

    /** Method to add clear top flag so it doesn't create new instance of parent
     *
     * @return intent*/
    @Override
    public Intent getSupportParentActivityIntent() {
        Intent intent = super.getSupportParentActivityIntent();
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        return intent;
    }

    public Bitmap getBitmapFromUri(Uri uri, Context context, ImageView imageView) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    /**
     * Get user input from editor and save car into database.
     */
    private void saveCar() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String brandString = mBrandEditText.getText().toString().trim();
        String modelString = mModelEditText.getText().toString().trim();
        String yearString = mYearEditText.getText().toString().trim();
        String engineString = mEngineEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String mileageString = mMileageEditText.getText().toString().trim();
        String supplierNameString = mContactNameEditText.getText().toString().trim();
        String supplierEmailString = mContactEmailEditText.getText().toString().trim();

        if ((!TextUtils.isEmpty(imagePath)) &&
                (!TextUtils.isEmpty(yearString)) &&
                (!TextUtils.isEmpty(mileageString)) &&
                (!TextUtils.isEmpty(supplierNameString)) &&
                (!TextUtils.isEmpty(supplierEmailString))) {
            // Exit activity only when all the fields have been filled
            finish();

        } else {
            // Check if this is supposed to be a new car
            // and check if all the fields in the editor are blank
            if (mCurrentCarUri == null || TextUtils.isEmpty(imagePath) ||TextUtils.isEmpty(yearString) ||
                    TextUtils.isEmpty(mileageString) || TextUtils.isEmpty(supplierNameString) ||
                    TextUtils.isEmpty(supplierEmailString)) {
                // if any of the fields are empty le the user know with a Toast message
                Toast.makeText(getApplicationContext(),"Complete all fields with (*)",
                        Toast.LENGTH_LONG).show();
            }
        }
            //make sure the image uri is not null
            if (mImageUri == null) {
                return;
        }

        // Get the imagePath
        imagePath = mImageUri.toString();
        Log.i(LOG_TAG, "TEST: Album Cover string is: " + imagePath);

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(CarContract.CarEntry.COLUMN_CAR_BRAND, brandString);
        values.put(CarContract.CarEntry.COLUMN_CAR_MODEL, modelString);
        values.put(CarContract.CarEntry.COLUMN_CAR_ENGINE, engineString);
        values.put(CarContract.CarEntry.COLUMN_CAR_PRICE, priceString);
        values.put(CarContract.CarEntry.COLUMN_CAR_FUEL, mFuel);
        values.put(CarContract.CarEntry.COLUMN_CAR_IMAGE, imagePath);
        values.put(CarContract.CarEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(CarContract.CarEntry.COLUMN_SUPPLIER_EMAIL, supplierEmailString );

        // If the quantity is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(CarContract.CarEntry.COLUMN_CAR_QUANTITY, quantity);


        // Determine if this is a new or existing pet by checking if mCurrentCarUri is null or not
        if (mCurrentCarUri == null) {
            // This is a NEW car, so insert a new car into the provider,
            // returning the content URI for the new car.
            Uri newUri = getContentResolver().insert(CarContract.CarEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_car_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_car_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING car, so update the pet with content URI: mCurrentCarUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentCarUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentCarUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_car_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_car_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new car, hide the "Delete" menu item.
        if (mCurrentCarUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save car to database
                saveCar();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mCarHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
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

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mCarHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all car attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                CarContract.CarEntry._ID,
                CarContract.CarEntry.COLUMN_CAR_BRAND,
                CarContract.CarEntry.COLUMN_CAR_MODEL,
                CarContract.CarEntry.COLUMN_CAR_YEAR,
                CarContract.CarEntry.COLUMN_CAR_ENGINE,
                CarContract.CarEntry.COLUMN_CAR_FUEL,
                CarContract.CarEntry.COLUMN_CAR_QUANTITY,
                CarContract.CarEntry.COLUMN_CAR_PRICE,
                CarContract.CarEntry.COLUMN_CAR_IMAGE,
                CarContract.CarEntry.COLUMN_SUPPLIER_NAME,
                CarContract.CarEntry.COLUMN_SUPPLIER_EMAIL,
                CarContract.CarEntry.COLUMN_CAR_MILEAGE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentCarUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        ViewTreeObserver viewTreeObserver = mImageView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mImageView.setImageBitmap(getBitmapFromUri(mImageUri, mContext, mImageView));
            }
        });

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of car attributes that we're interested in
            int idColumnIndex = cursor.getColumnIndex(CarContract.CarEntry._ID);
            int brandColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_BRAND);
            int modelColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_MODEL);
            int yearColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_YEAR);
            int engineColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_ENGINE);
            int fuelColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_FUEL);
            int quantityColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_IMAGE);
            int supplierNameColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_SUPPLIER_NAME);
            int supplierEmailColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_SUPPLIER_EMAIL);
            int mileageColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_MILEAGE);

            // Extract out the value from the Cursor for the given column index
            String brand = cursor.getString(brandColumnIndex);
            String model = cursor.getString(modelColumnIndex);
            int year = cursor.getInt(yearColumnIndex);
            String  engine = cursor.getString(engineColumnIndex);
            int fuel = cursor.getInt(fuelColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            final String image = cursor.getString(imageColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierEmail = cursor.getString(supplierEmailColumnIndex);
            int mileage = cursor.getInt(mileageColumnIndex);

            // Update the views on the screen with the values from the database
            mBrandEditText.setText(brand);
            mModelEditText.setText(model);
            mYearEditText.setText(Integer.toString(year));
            mEngineEditText.setText(engine);
            mQuantityEditText.setText(Integer.toString(quantity));
            mPriceEditText.setText(price);
            mImageView.setImageBitmap(getBitmapFromUri(Uri.parse(image), mContext, mImageView));
            mImageUri = Uri.parse(image);
            mContactNameEditText.setText(supplierName);
            mContactEmailEditText.setText(supplierEmail);
            mMileageEditText.setText(Integer.toString(mileage));

            // Fuel is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Gasoline, 1 is Alcohol, 2 is Flex).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (fuel) {
                case CarContract.CarEntry.FUEL_ALCOHOL:
                    mFuelSpinner.setSelection(1);
                    break;
                case CarContract.CarEntry.FUEL_FLEX:
                    mFuelSpinner.setSelection(2);
                    break;
                default:
                    mFuelSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mBrandEditText.setText("");
        mModelEditText.setText("");
        mYearEditText.setText("");
        mEngineEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mMileageEditText.setText("");
        mContactNameEditText.setText("");
        mContactEmailEditText.setText("");
        mFuelSpinner.setSelection(0); // Select "gasoline" fuel
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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

    /**
     * Prompt the user to confirm that they want to delete this car.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the car.
                deleteCar();
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

    /**
     * Perform the deletion of the car in the database.
     */
    private void deleteCar() {
        // Only perform the delete if this is an existing car.
        if (mCurrentCarUri != null) {
            // Call the ContentResolver to delete the car at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentCarUri
            // content URI already identifies the car that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentCarUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_car_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_car_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}