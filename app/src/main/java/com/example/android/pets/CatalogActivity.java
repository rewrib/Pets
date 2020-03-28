/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import com.example.android.pets.data.PetsContract;
import com.example.android.pets.data.PetsDbHelper;
import com.example.android.pets.data.PetsProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import static com.example.android.pets.data.PetsContract.CONTENT_URI;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    private PetsDbHelper mDbHelper;


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
        mDbHelper = new PetsDbHelper(this);

        displayDatabaseInfo();

        // Find the ListView which will be populated with the pet data
        ListView petListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);
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
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        // REFACTOR FROM: Cursor cursor = db.rawQuery("SELECT * FROM " + PetsContract.PetsEntry.TABLE_NAME, null);
        String[] projection = {
                PetsContract.PetsEntry.COLUMN_ID,
                PetsContract.PetsEntry.COLUMN_NAME,
                PetsContract.PetsEntry.COLUMN_BREED,
                PetsContract.PetsEntry.COLUMN_GENDER,
                PetsContract.PetsEntry.COLUMN_WEIGHT
        };

        Cursor cursor = getContentResolver().query(CONTENT_URI, projection, null, null, null);




/*      (BAD_PRACTISE)
        Cursor cursor = db.query(
                PetsContract.PetsEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
*/

        ListView petListView = (ListView) findViewById(R.id.list);

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        PetCursorAdapter adapter = new PetCursorAdapter(this, cursor);
        // Attach the adapter to the ListView.
        petListView.setAdapter(adapter);
        cursor.close();


    }


    private void insertPet() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues dummyValues = new ContentValues();
        dummyValues.put(PetsContract.PetsEntry.COLUMN_NAME, "Toto");
        dummyValues.put(PetsContract.PetsEntry.COLUMN_BREED, "Terrier");
        dummyValues.put(PetsContract.PetsEntry.COLUMN_GENDER, PetsContract.PetsEntry.GENDER_MALE);
        dummyValues.put(PetsContract.PetsEntry.COLUMN_WEIGHT, 7);
        Uri newUri = getContentResolver().insert(PetsContract.CONTENT_URI, dummyValues);


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
