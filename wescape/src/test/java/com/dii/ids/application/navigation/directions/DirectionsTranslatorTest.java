package com.dii.ids.application.navigation.directions;

import com.dii.ids.application.entity.Node;
import com.dii.ids.application.navigation.Path;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;


public class DirectionsTranslatorTest {

    final String FLOOR_1 = "145";
    final String FLOOR_2 = "150";
    final String FLOOR_3 = "155";

    @Test
    public void testPlaneDirections() throws Exception {

        Node node1 = new Node(),
                node2 = new Node(),
                node3 = new Node(),
                node4 = new Node(),
                node5 = new Node(),
                node6 = new Node(),
                node7 = new Node();
        node1.setX(10).setY(1).setFloor(FLOOR_1).setType(Node.TYPE_GENERAL);
        node2.setX(10).setY(10).setFloor(FLOOR_1).setType(Node.TYPE_GENERAL);
        node3.setX(8).setY(1).setFloor(FLOOR_1).setType(Node.TYPE_GENERAL);
        node4.setX(1).setY(10).setFloor(FLOOR_1).setType(Node.TYPE_GENERAL);
        node5.setX(5).setY(15).setFloor(FLOOR_1).setType(Node.TYPE_GENERAL);
        node6.setX(3).setY(10).setFloor(FLOOR_1).setType(Node.TYPE_GENERAL);
        node7.setX(8).setY(8).setFloor(FLOOR_1).setType(Node.TYPE_GENERAL);

        Path path = new Path();
        path.add(node1);
        path.add(node2);
        path.add(node3);
        path.add(node4);
        path.add(node5);
        path.add(node6);
        path.add(node7);

        DirectionsTranslator translator = new DirectionsTranslator(path);
        Directions directions = translator.calculateDirections().getDirections();

        assertEquals(directions.get(0), Actions.GO_AHEAD);
        assertEquals(directions.get(1), Actions.TURN_BACK_LEFT);
        assertEquals(directions.get(2), Actions.TURN_RIGHT);
        assertEquals(directions.get(3), Actions.TURN_RIGHT);
        assertEquals(directions.get(4), Actions.TURN_BACK_RIGHT);
        assertEquals(directions.get(5), Actions.TURN_LEFT);
    }

    @Test
    public void testMultiFloorDirections() throws Exception{
        Node node1 = new Node(),
                node2 = new Node(),
                node3 = new Node(),
                node4 = new Node(),
                node5 = new Node(),
                node6 = new Node(),
                node7 = new Node(),
                node8 = new Node(),
                node9 = new Node();

        node1.setX(10).setY(1).setFloor(FLOOR_1).setType(Node.TYPE_GENERAL);
        node2.setX(10).setY(10).setFloor(FLOOR_1).setType(Node.TYPE_GENERAL);
        node3.setX(8).setY(1).setFloor(FLOOR_2).setType(Node.TYPE_GENERAL);
        node4.setX(1).setY(10).setFloor(FLOOR_2).setType(Node.TYPE_GENERAL);
        node5.setX(5).setY(15).setFloor(FLOOR_2).setType(Node.TYPE_GENERAL);
        node6.setX(3).setY(10).setFloor(FLOOR_3).setType(Node.TYPE_GENERAL);
        node7.setX(8).setY(8).setFloor(FLOOR_3).setType(Node.TYPE_GENERAL);
        node8.setX(10).setY(1).setFloor(FLOOR_2).setType(Node.TYPE_GENERAL);
        node9.setX(10).setY(10).setFloor(FLOOR_2).setType(Node.TYPE_GENERAL);

        Path path = new Path();
        path.add(node1);
        path.add(node2);
        path.add(node3);
        path.add(node4);
        path.add(node5);
        path.add(node6);
        path.add(node7);
        path.add(node8);
        path.add(node9);

        DirectionsTranslator translator = new DirectionsTranslator(path);
        Directions directions = translator.calculateDirections().getDirections();

        assertEquals(directions.get(0), Actions.GO_AHEAD);
        assertEquals(directions.get(1), Actions.GO_UPSTAIRS);
        assertEquals(directions.get(2), Actions.GO_AHEAD);
        assertEquals(directions.get(3), Actions.TURN_LEFT);
        assertEquals(directions.get(4), Actions.GO_UPSTAIRS);
        assertEquals(directions.get(5), Actions.TURN_RIGHT);
        assertEquals(directions.get(6), Actions.GO_DOWNSTAIRS);
        assertEquals(directions.get(7), Actions.TURN_BACK_LEFT);

    }

}
