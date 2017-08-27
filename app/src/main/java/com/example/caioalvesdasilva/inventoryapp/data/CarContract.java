package com.example.caioalvesdasilva.inventoryapp.data;

/**
 * Created by caio.alves.da.silva on 21/08/2017.
 */

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Pets app.
 */
public final class CarContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private CarContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.caioalvesdasilva.inventoryapp";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.caioalvesdasilva.inventoryapp/cars/ is a valid path for
     * looking at car data. content://com.example.caioalvesdasilva.inventoryapp/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_CARS = "cars";


    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single car.
     */
    public static final class CarEntry implements BaseColumns {

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CARS);

        /**
         * The MIME type of the {@link #BASE_CONTENT_URI} for a list of cars.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CARS;

        /**
         * The MIME type of the {@link #BASE_CONTENT_URI} for a single car.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CARS;


        /** Name of database table for cars */
        public final static String TABLE_NAME = "cars";


        /**
         * Unique ID number for the car (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;


        /**
         * Brand of the car.
         *
         * Type: TEXT
         */
        public final static String COLUMN_CAR_BRAND = "brand";


        /**
         * Model of the car.
         *
         * Type: TEXT
         */
        public final static String COLUMN_CAR_MODEL = "model";


        /**
         * Year of the car.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_CAR_YEAR = "year";


        /**
         * Engine of the car.
         *
         * Type: REAL
         */
        public final static String COLUMN_CAR_ENGINE = "engine";


        /**
         * Fuel of the car.
         *
         * The only possible values are {@link #FUEL_ALCOHOL}, {@link #FUEL_GASOLINE},
         * or {@link #FUEL_FLEX}.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_CAR_FUEL = "fuel";


        /**
         * Quantity of the car.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_CAR_QUANTITY = "quantity";


        /**
         * Price of the car.
         *
         * Type: REAL
         */
        public final static String COLUMN_CAR_PRICE = "price";


        /**
         * Image of the car.
         *
         * Type: Text
         */
        public final static String COLUMN_CAR_IMAGE = "image";


        /**
         * Mileage of the car.
         *
         * Type: Text
         */
        public final static String COLUMN_CAR_MILEAGE = "mileage";


        /**
         * Possible values for the fuel of the car.
         */
        public static final int FUEL_GASOLINE = 0;
        public static final int FUEL_ALCOHOL = 1;
        public static final int FUEL_FLEX = 2;

        /**
         * Returns whether or not the given fuel is {@link #FUEL_ALCOHOL}, {@link #FUEL_GASOLINE},
         * or {@link #FUEL_FLEX}.
         */
        public static boolean isValidFuel(int gender) {
            if (gender == FUEL_ALCOHOL || gender == FUEL_GASOLINE || gender == FUEL_FLEX) {
                return true;
            }
            return false;
        }
    }

}
