package ru.avelier.pwcats.db;

import android.provider.BaseColumns;

/**
 * Created by Adelier on 28.06.2014.
 */
public class DbItemsContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DbItemsContract() {}

    /* Inner class that defines the table contents */
    public static abstract class ItemsEntry implements BaseColumns {
        public static final String TABLE_NAME = "items";
        public static final String COL_NAME = "name";
    }
}
