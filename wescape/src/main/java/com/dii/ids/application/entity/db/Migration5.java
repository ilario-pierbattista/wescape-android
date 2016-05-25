package com.dii.ids.application.entity.db;

import com.dii.ids.application.entity.Node;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

// @TODO Delete
@Migration(version = 5, database = WescapeDatabase.class)
public class Migration5 extends AlterTableMigration<Node> {
    public Migration5(Class<Node> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        addColumn(SQLiteType.TEXT, "type");
    }
}
