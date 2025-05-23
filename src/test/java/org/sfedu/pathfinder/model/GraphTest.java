package org.sfedu.pathfinder.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {
    @Test
    void testAddNodeAndGetNodeById() {
        Graph graph = new Graph();
        Node node = new Node("1", 0, 0);
        graph.addNode(node);

        assertEquals(node, graph.getNodeById("1"));
    }

    @Test
    void testAddEdgeAndGetNeighbors() {
        Graph graph = new Graph();

        Node node1 = new Node("1", 0, 0);
        Node node2 = new Node("2", 1, 1);

        graph.addNode(node1);
        graph.addNode(node2);
        graph.addEdge(new Edge("e1", node1, node2, 5));

        List<Node> neighbors = graph.getNeighbors(node1);
        assertEquals(1, neighbors.size());
        assertEquals(node2, neighbors.get(0));
    }

    @Test
    void testGetEdgeWeight() {
        Graph graph = new Graph();

        Node node1 = new Node("1", 0, 0);
        Node node2 = new Node("2", 1, 1);

        graph.addNode(node1);
        graph.addNode(node2);
        graph.addEdge(new Edge("e1", node1, node2, 7.5));

        assertEquals(7.5, graph.getEdgeWeight(node1, node2));
        assertEquals(Double.POSITIVE_INFINITY, graph.getEdgeWeight(node2, node1)); // Направленный граф
    }

    @Test
    void testNodeCount() {
        Graph graph = new Graph();
        graph.addNode(new Node("1", 0, 0));
        graph.addNode(new Node("2", 1, 1));

        assertEquals(2, graph.getNodeCount());
    }

    @Test
    void testEdgeCount() {
        Graph graph = new Graph();
        Node n1 = new Node("1", 0, 0);
        Node n2 = new Node("2", 1, 1);
        graph.addNode(n1);
        graph.addNode(n2);
        graph.addEdge(new Edge("e1", n1, n2, 5));

        assertEquals(1, graph.getEdgeCount());
    }
}