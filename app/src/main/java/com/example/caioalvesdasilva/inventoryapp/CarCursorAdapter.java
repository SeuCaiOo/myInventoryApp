package com.example.caioalvesdasilva.inventoryapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caioalvesdasilva.inventoryapp.data.CarContract;

import static android.R.attr.id;
import static android.content.ContentValues.TAG;


/**
 * Created by caio.alves.da.silva on 23/08/2017.
 */

/**
 * {@link CarCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class CarCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link CarCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public CarCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView brandTextView = (TextView) view.findViewById(R.id.carBrand);
        TextView modelTextView = (TextView) view.findViewById(R.id.carModel);
        TextView quantityView = (TextView) view.findViewById(R.id.carQuantity);
        TextView engineView = (TextView) view.findViewById(R.id.carEngine);
        TextView priceView = (TextView) view.findViewById(R.id.carPrice);
        ImageView sellView = (ImageView) view.findViewById(R.id.carSell);

        // Find the columns of pet attributes that we're interested in
        int brandColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_BRAND);
        int modelColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_MODEL);
        int quantityColumnIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_QUANTITY);
        int engineColumnIndex= cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_ENGINE);
        int priceColumnsIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_PRICE);

        // Read the pet attributes from the Cursor for the current pet
        String carBrand = cursor.getString(brandColumnIndex);
        String carModel = cursor.getString(modelColumnIndex);
        String carQuantity = cursor.getString(quantityColumnIndex);
        String carEngine = cursor.getString (engineColumnIndex);
        String carPrice = cursor.getString(priceColumnsIndex);
        final int quantityCar = cursor.getInt(quantityColumnIndex);

        int carId = cursor.getInt(cursor.getColumnIndex(CarContract.CarEntry._ID));

        final Uri currentCarUri = ContentUris.withAppendedId(CarContract.CarEntry.CONTENT_URI, carId);



        // If the pet breed is empty string or null, then use some default text
        // that says "Unknown brand", so the TextView isn't blank.
        if (TextUtils.isEmpty(carBrand)) {
            carBrand = context.getString(R.string.unknown_brand);
        }

        // If the pet breed is empty string or null, then use some default text
        // that says "Unknown model", so the TextView isn't blank.
        if (TextUtils.isEmpty(carModel)) {
            carModel = context.getString(R.string.unknown_model);
        }


        // If the pet breed is empty string or null, then use some default text
        // that says "Unknown breed", so the TextView isn't blank.
        if (TextUtils.isEmpty(carEngine)) {
            carEngine = context.getString(R.string.unknown_engine);
        }
        // If the pet breed is empty string or null, then use some default text
        // that says "Unknown breed", so the TextView isn't blank.
        if (TextUtils.isEmpty(carPrice)) {
            carPrice = context.getString(R.string.unknown_price);
        }

        // Update the TextViews with the attributes for the current pet
        brandTextView.setText(carBrand);
        modelTextView.setText(carModel);
        quantityView.setText(carQuantity);
        engineView.setText(carEngine);
        priceView.setText(carPrice);



        final int currentCarQuantity = cursor.getInt(quantityColumnIndex);
        final Long id = cursor.getLong(cursor.getColumnIndex(CarContract.CarEntry._ID));



        sellView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver resolver = view.getContext().getContentResolver();
                ContentValues values = new ContentValues();
                if (quantityCar > 0) {
                    int qq = quantityCar;
                    Log.d(TAG, "new quabtity= " + qq);
                    values.put(CarContract.CarEntry.COLUMN_CAR_QUANTITY, --qq);
                    resolver.update(
                            currentCarUri,
                            values,
                            null,
                            null
                    );
                    context.getContentResolver().notifyChange(currentCarUri, null);
                } else {
                    Toast.makeText(context, "Item out of stock", Toast.LENGTH_SHORT).show();
                }
            }


        });

    }
}
