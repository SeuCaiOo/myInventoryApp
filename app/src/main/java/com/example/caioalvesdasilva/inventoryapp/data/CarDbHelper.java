package com.example.caioalvesdasilva.inventoryapp.data;

/**
 * Created by caio.alves.da.silva on 21/08/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper for Pets app. Manages database creation and version management.
 */
public class CarDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = CarDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "dealership.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link CarDbHelper}.
     *
     * @param context of the app
     */
    public CarDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + CarContract.CarEntry.TABLE_NAME + " ("
                + CarContract.CarEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CarContract.CarEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "
                + CarContract.CarEntry.COLUMN_PET_BREED + " TEXT, "
                + CarContract.CarEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "
                + CarContract.CarEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}