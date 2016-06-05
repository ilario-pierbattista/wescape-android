package com.dii.ids.application.entity.repository;

import com.dii.ids.application.entity.Edge;
import com.dii.ids.application.entity.Edge_Table;
import com.dii.ids.application.entity.Node;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class EdgeRepository {

    public static Edge find(int id) {

        return SQLite
                .select()
                .from(Edge.class)
                .where(Edge_Table.id.eq(id))
                .querySingle();
    }

    public static List<Edge> findAll() {
        return SQLite
                .select()
                .from(Edge.class)
                .queryList();
    }

    public static List<Edge> findAllButOne(Edge edge) {
        if (edge == null) {
            return findAll();
        } else {
            return SQLite
                    .select()
                    .from(Edge.class)
                    .where(Edge_Table.id.notEq(edge.getId()))
                    .queryList();
        }
    }

    public static List<Edge> findStairs() {
        return SQLite
                .select()
                .from(Edge.class)
                .where(Edge_Table.stairs.eq(true))
                .queryList();
    }

    public static List<Edge> findByBeginNode(int id) {
        return SQLite
                .select()
                .from(Edge.class)
                .where(Edge_Table.begin_id.eq(id))
                .queryList();
    }

    public static List<Edge> findByEndNode(int id) {
        return SQLite
                .select()
                .from(Edge.class)
                .where(Edge_Table.end_id.eq(id))
                .queryList();
    }

    public static Edge findMaxLengthEdge() {
        List<Edge> edges = SQLite.select()
                .from(Edge.class)
                .orderBy(Edge_Table.length, false)
                .limit(1)
                .queryList();
        return edges.get(0);
    }

    public static double getMaxLength() {
        final Edge maxLengthEdge = findMaxLengthEdge();
        return maxLengthEdge.getLength();
    }
}
