package ru.avelier.pwcats.db;

import android.provider.BaseColumns;

/**
 * Created by Adelier on 28.06.2014.
 */
public class DbRecentItemsContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DbRecentItemsContract() {}

    /* Inner class that defines the table contents */
    public static abstract class RecentItemsEntry implements BaseColumns {
        public static final String TABLE_NAME = "recent_items";
        public static final String COL_RECENT_ID = "recent_id";
        public static final String COL_RECENT_DATE = "recent_date";
    }
}
