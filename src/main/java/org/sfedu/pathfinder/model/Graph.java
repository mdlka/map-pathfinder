package org.sfedu.pathfinder.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private final Map<String, Node> nodes = new HashMap<>();
    private final Map<Node, List<Edge>> outEdges = new HashMap<>();
    private final Map<Node, List<Edge>> inEdges = new HashMap<>();

    public void addNode(Node node) {
        nodes.put(node.id(), node);
        outEdges.putIfAbsent(node, new ArrayList<>());
        inEdges.putIfAbsent(node, new ArrayList<>());
    }

    public void addEdge(Edge edge) throws IllegalArgumentException {
        outEdges.get(edge.from()).add(edge);
        inEdges.get(edge.to()).add(edge);
    }

    public void removeNodeById(String id) {
        var node = nodes.remove(id);

        if (node == null)
            return;

        for (Edge edge : outEdges.remove(node))
            inEdges.get(edge.to()).remove(edge);

        for (Edge edge : inEdges.remove(node))
            outEdges.get(edge.from()).remove(edge);
    }

    public Node getNodeById(String id) {
        return nodes.get(id);
    }

    public Collection<Node> getAllNodes() {
        return nodes.values();
    }

    public List<Edge> getEdgesFrom(Node node) {
        return outEdges.getOrDefault(node, Collections.emptyList());
    }

    public List<Node> getNeighbors(Node node) {
        var neighbors = new ArrayList<Node>();

        for (Edge edge : getEdgesFrom(node))
            neighbors.add(edge.to());

        return neighbors;
    }

    public double getEdgeWeight(Node from, Node to) {
        for (Edge edge : getEdgesFrom(from))
            if (edge.to().equals(to))
                return edge.weight();

        return Double.POSITIVE_INFINITY;
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public int getEdgeCount() {
        return outEdges.values().stream()
                .mapToInt(List::size)
                .sum();
    }
}