package org.sfedu.pathfinder.algorithm;

import org.junit.jupiter.api.Test;
import org.sfedu.pathfinder.model.Graph;
import org.sfedu.pathfinder.model.Node;
import org.sfedu.pathfinder.model.Edge;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AStarAlgorithmTest {

    private final AStarAlgorithm aStar = new AStarAlgorithm();

    @Test
    void testSimplePath() {
        Graph graph = new Graph();

        Node a = new Node("A", 45.0, 45.0);
        Node B = new Node("B", 45.1, 45.1);
        Node C = new Node("C", 45.2, 45.2);

        graph.addNode(a);
        graph.addNode(B);
        graph.addNode(C);

        graph.addEdge(new Edge("AB", a, B, 10));
        graph.addEdge(new Edge("BC", B, C, 10));
        graph.addEdge(new Edge("AC", a, C, 30)); // Длиннее, чем A -> B -> C

        Path path = aStar.findShortestPath(graph, a, C);

        assertNotNull(path);
        assertFalse(path.isEmpty());
        List<Node> nodes = path.nodes();
        assertEquals(3, nodes.size());
        assertEquals(a, nodes.get(0));
        assertEquals(B, nodes.get(1));
        assertEquals(C, nodes.get(2));
    }

    @Test
    void testNoPath() {
        Graph graph = new Graph();

        Node A = new Node("A", 45.0, 45.0);
        Node B = new Node("B", 45.1, 45.1);

        graph.addNode(A);
        graph.addNode(B);

        Path path = aStar.findShortestPath(graph, A, B);

        assertNotNull(path);
        assertTrue(path.isEmpty());
    }

    @Test
    void testSameStartEnd() {
        Graph graph = new Graph();

        Node A = new Node("A", 45.0, 45.0);

        graph.addNode(A);

        Path path = aStar.findShortestPath(graph, A, A);

        assertNotNull(path);
        assertFalse(path.isEmpty());
        assertEquals(1, path.nodes().size());
        assertEquals(A, path.nodes().get(0));
    }

    @Test
    void testPathWithMultipleOptions() {
        Graph graph = new Graph();

        Node A = new Node("A", 0, 0);
        Node B = new Node("B", 0, 1);
        Node C = new Node("C", 0, 2);
        Node D = new Node("D", 0, 3);

        graph.addNode(A);
        graph.addNode(B);
        graph.addNode(C);
        graph.addNode(D);

        graph.addEdge(new Edge("AB", A, B, 1));
        graph.addEdge(new Edge("BC", B, C, 1));
        graph.addEdge(new Edge("CD", C, D, 1));
        graph.addEdge(new Edge("AD", A, D, 4));

        Path path = aStar.findShortestPath(graph, A, D);

        assertNotNull(path);
        assertFalse(path.isEmpty());

        List<Node> nodes = path.nodes();
        assertEquals(4, nodes.size(), "Ожидался путь из 4 узлов: A → B → C → D");

        assertEquals(A, nodes.get(0));
        assertEquals(B, nodes.get(1));
        assertEquals(C, nodes.get(2));
        assertEquals(D, nodes.get(3));
    }
}