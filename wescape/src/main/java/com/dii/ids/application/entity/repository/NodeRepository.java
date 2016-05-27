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

    public static void deleteAll() {
        SQLite.delete()
                .from(Node.class)
                .execute();
    }

    /**
     * Find nodes in a floor
     *
     * @param floor floor
     * @return List<Node>
     */
    public static List<Node> findByFloor(String floor) {
        return SQLite
                .select()
                .from(Node.class)
                .where(Node_Table.floor.eq(floor))
                .queryList();
    }

    /**
     * Find nodes in a specific floor and region
     *
     * @param floor  floor
     * @param x0     x of the center of the region
     * @param y0     y of the center of the region
     * @param radius radius of the region
     * @return List<Node>
     */
    public static List<Node> findByFloor(String floor, int x0, int y0, int radius) {
        return SQLite
                .select()
                .from(Node.class)
                .where(Node_Table.floor.eq(floor))
                .and(Node_Table.x.greaterThan(x0 - radius))
                .and(Node_Table.x.lessThan(x0 + radius))
                .and(Node_Table.y.lessThan(y0 + radius))
                .and(Node_Table.y.lessThan(y0 + radius))
                .queryList();
    }

    /**
     * Find all exits node
     *
     * @return List of node
     */
    public static List<Node> findAllExits() {
        return SQLite
                .select()
                .from(Node.class)
                .where(Node_Table.type.eq(Node.TYPE_EXIT))
                .or(Node_Table.type.eq(Node.TYPE_EMERGENCY))
                .queryList();
    }
}
