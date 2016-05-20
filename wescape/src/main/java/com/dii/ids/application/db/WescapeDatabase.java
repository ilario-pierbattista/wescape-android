package com.dii.ids.application.db;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = WescapeDatabase.NAME, version = WescapeDatabase.VERSION)
public class WescapeDatabase {
    public static final String NAME = "WescapeDatabase"; // we will add the .db extension

    public static final int VERSION = 2;
}
