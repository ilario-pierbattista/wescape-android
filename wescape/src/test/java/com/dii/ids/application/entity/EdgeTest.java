package com.dii.ids.application.entity;

import org.junit.Test;

import static org.junit.Assert.*;


public class EdgeTest {
    @Test
    public void testIsConnectedTo() throws Exception {
        Node node1 = new Node(),
                node2 = new Node(),
                node3 = new Node(),
                node4 = new Node();

        node1.setId(1);
        node2.setId(2);
        node3.setId(3);
        node4.setId(4);

        Edge edge1 = new Edge(),
                edge2 = new Edge(),
                edge3 = new Edge();
        edge1.setBegin(node1);
        edge1.setEnd(node2);
        edge2.setBegin(node2);
        edge2.setEnd(node3);
        edge3.setBegin(node3);
        edge3.setEnd(node4);

        assertTrue(edge1.isConnectedTo(edge2));
        assertFalse(edge1.isConnectedTo(edge3));
    }
}