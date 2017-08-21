package com.example.caioalvesdasilva.inventoryapp.data;

/**
 * Created by caio.alves.da.silva on 21/08/2017.
 */

import android.provider.BaseColumns;

/**
 * API Contract for the Pets app.
 */
public final class CarContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private CarContract() {}

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class CarEntry implements BaseColumns {

        /** Name of database table for pets */
        public final static String TABLE_NAME = "cars";

        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Breed of the pet.
         *
         * Type: TEXT
         */
        public final static String COLUMN_CAR_BRAND = "brand";


        /**
         * Name of the pet.
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
         * Gender of the pet.
         *
         * The only possible values are {@link #FUEL_ALCOHOL}, {@link #FUEL_GASOLINE},
         * or {@link #FUEL_FLEX}.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_CAR_FUEL = "fuel";

        /**
         * Weight of the pet.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_CAR_MILEAGE = "mileage";

        /**
         * Possible values for the fuel of the car.
         */
        public static final int FUEL_GASOLINE = 0;
        public static final int FUEL_ALCOHOL = 1;
        public static final int FUEL_FLEX = 2;
    }

}
