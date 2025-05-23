package org.sfedu.pathfinder.model;

import java.util.*;

public class Graph {
    private final Map<String, Node> nodes = new HashMap<>();
    private final List<Edge> edges = new ArrayList<>();
    private final Map<Node, List<Edge>> adjacencyMap = new HashMap<>();

    public void addNode(Node node) {
        nodes.put(node.id(), node);
        adjacencyMap.putIfAbsent(node, new ArrayList<>());
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
        adjacencyMap.get(edge.from()).add(edge);
    }

    public Node getNodeById(String id) {
        return nodes.get(id);
    }

    public Collection<Node> getAllNodes() {
        return nodes.values();
    }

    public List<Edge> getEdgesFrom(Node node) {
        return adjacencyMap.getOrDefault(node, Collections.emptyList());
    }

    public List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();

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
        return edges.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Graph:\n");

        for (Node node : nodes.values()) {
            sb.append("  ").append(node).append(" -> ");
            sb.append(getNeighbors(node)).append("\n");
        }

        return sb.toString();
    }
}