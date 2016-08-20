package com.example.carlrygart.schoolify;

import android.provider.BaseColumns;

public final class SchoolifyContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public SchoolifyContract() {}

    /* Inner class that defines the table contents */
    public static abstract class SchoolEntry implements BaseColumns {
        public static final String TABLE_NAME = "school";
        public static final String COLUMN_NAME_SCHOOL_ID = "schoolid";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_POSTAL_CODE = "postalCode";
        public static final String COLUMN_NAME_WEB_SITE = "webSite";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_EMAIL = "email";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SchoolEntry.TABLE_NAME + " (" +
                    SchoolEntry._ID + " INTEGER PRIMARY KEY," +
                    SchoolEntry.COLUMN_NAME_SCHOOL_ID + TEXT_TYPE + COMMA_SEP +
                    SchoolEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    SchoolEntry.COLUMN_NAME_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    SchoolEntry.COLUMN_NAME_POSTAL_CODE + TEXT_TYPE + COMMA_SEP +
                    SchoolEntry.COLUMN_NAME_WEB_SITE + TEXT_TYPE + COMMA_SEP +
                    SchoolEntry.COLUMN_NAME_PHONE + TEXT_TYPE + COMMA_SEP +
                    SchoolEntry.COLUMN_NAME_EMAIL + TEXT_TYPE + COMMA_SEP +
            " )";
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SchoolEntry.TABLE_NAME;


}
