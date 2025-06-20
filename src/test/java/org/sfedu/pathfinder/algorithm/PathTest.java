package org.sfedu.pathfinder.algorithm;

import org.junit.jupiter.api.Test;
import org.sfedu.pathfinder.model.Node;
import org.sfedu.pathfinder.model.Path;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PathTest {

    @Test
    void testEmptyPath() {
        Path path = new Path(List.of());
        assertTrue(path.isEmpty());
        assertEquals(0, path.getTotalDistance());
    }

    @Test
    void testSingleNodePath() {
        Node node = new Node("1", 45.0, 45.0);
        Path path = new Path(List.of(node));
        assertFalse(path.isEmpty());
        assertEquals(0, path.getTotalDistance());
    }

    @Test
    void testTwoNodePath() {
        Node node1 = new Node("1", 45.0, 45.0);
        Node node2 = new Node("2", 45.1, 45.1);

        Path path = new Path(List.of(node1, node2));

        assertFalse(path.isEmpty());
        assertEquals(2, path.size());
        double distance = path.getTotalDistance();
        assertTrue(distance > 0 && distance < 20); // ~12-15 км между точками
    }
}