package com.dii.ids.application.entity.repository;

import com.dii.ids.application.entity.Node;
import com.dii.ids.application.entity.Node_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class NodeRepository {

    public static Node find(int id) {

        return SQLite
                .select()
                .from(Node.class)
                .where(Node_Table.id.eq(id))
                .querySingle();
    }

    public static List<Node> findAll() {
        return SQLite
                .select()
                .from(Node.class)
                .queryList();
    }

    public static List<Node> findByFloor(String floor) {
        return SQLite
                .select()
                .from(Node.class)
                .where(Node_Table.floor.eq(floor))
                .queryList();
    }
}
