package org.sfedu.pathfinder.service;

import org.sfedu.pathfinder.algorithm.Path;
import org.sfedu.pathfinder.algorithm.PathfindingAlgorithm;
import org.sfedu.pathfinder.io.MapDataReader;
import org.sfedu.pathfinder.model.Edge;
import org.sfedu.pathfinder.model.Graph;
import org.sfedu.pathfinder.model.Node;
import org.sfedu.pathfinder.utils.GraphUtils;
import org.sfedu.pathfinder.utils.PMath;

import java.io.InputStream;
import java.util.*;

public class PathfindingService {
    private static final double MAX_NODE_SEARCH_RADIUS_IN_METERS = 50;

    private final PathfindingAlgorithm algorithm;
    private Graph graph;

    public PathfindingService(PathfindingAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void loadGraph(MapDataReader mapDataReader, InputStream is) {
        this.graph = mapDataReader.readGraph(is);
        System.out.println("Graph loaded. Nodes: " + graph.getNodeCount() + ", Edges: " + graph.getEdgeCount());
    }

    public Path findPath(double startLat, double startLon, double targetLat, double targetLon) {
        if (graph == null)
            throw new IllegalStateException("Graph isn't load. Call loadGraph()");

        Node start = GraphUtils.findNearestNode(graph, startLat, startLon, MAX_NODE_SEARCH_RADIUS_IN_METERS);
        Node target = GraphUtils.findNearestNode(graph, targetLat, targetLon, MAX_NODE_SEARCH_RADIUS_IN_METERS);

        if (start == null || target == null)
            return new Path(List.of());

        System.out.println("Is reachable - " + GraphUtils.isReachable(graph, start, target));

        if (!GraphUtils.isReachable(graph, start, target))
            connectComponents(start, target);

        System.out.println("Finding path from " + start + " to " + target);

        return algorithm.findShortestPath(graph, start, target);
    }

    public Graph getGraph() {
        return graph;
    }

    private void connectComponents(Node start, Node target) {
        List<Set<Node>> components = findConnectedComponents(graph);
        Integer startComponentIndex = getComponentIndex(components, start);
        Integer endComponentIndex = getComponentIndex(components, target);

        System.out.println("Components before connection: " + components.size());

        if (startComponentIndex == null || endComponentIndex == null) {
            System.out.println("Одна из точек вне графа.");
            return;
        }

        if (startComponentIndex.equals(endComponentIndex)) {
            System.out.println("Точки находятся в одной компоненте связности.");
        } else {
            System.out.println("Точки находятся в разных компонентах связности.");
            System.out.printf("Старт в компоненте #%d, финиш в компоненте #%d%n",
                    startComponentIndex, endComponentIndex);

            connectComponents(graph, components, 0.0001); // 10 метров. слишком много не надо, а то фигня получается

            components = findConnectedComponents(graph);
            System.out.println("Components after connection: " + components.size());
            System.out.println("Узлов: " + graph.getNodeCount() + ", рёбер: " + graph.getEdgeCount());
        }
    }

    private static List<Set<Node>> findConnectedComponents(Graph graph) {
        Set<Node> visited = new HashSet<>();
        List<Set<Node>> components = new ArrayList<>();

        for (Node node : graph.getAllNodes()) {
            if (!visited.contains(node)) {
                Set<Node> component = new LinkedHashSet<>();
                Deque<Node> stack = new ArrayDeque<>();

                stack.push(node);

                while (!stack.isEmpty()) {
                    Node current = stack.pop();

                    if (visited.contains(current))
                        continue;

                    visited.add(current);
                    component.add(current);

                    for (Node neighbor : graph.getNeighbors(current))
                        if (!visited.contains(neighbor))
                            stack.push(neighbor);
                }

                components.add(component);
            }
        }

        return components;
    }

    private static Integer getComponentIndex(List<Set<Node>> components, Node node) {
        for (int i = 0; i < components.size(); i++)
            if (components.get(i).contains(node))
                return i;

        return null;
    }

    private static Node[] findClosestPair(Set<Node> comp1, Set<Node> comp2) {
        double minDistance = Double.MAX_VALUE;
        Node[] result = new Node[2];

        for (Node n1 : comp1) {
            for (Node n2 : comp2) {
                double distance = PMath.calculateDistance(n1.latitude(), n1.longitude(), n2.latitude(), n2.longitude());
                if (distance < minDistance) {
                    minDistance = distance;
                    result[0] = n1;
                    result[1] = n2;
                }
            }
        }

        return result;
    }

    private static void connectComponents(Graph graph, List<Set<Node>> components, double maxAllowedDistance) {
        int count = 0;

        for (int i = 0; i < components.size(); i++) {
            for (int j = i + 1; j < components.size(); j++) {
                Set<Node> compA = components.get(i);
                Set<Node> compB = components.get(j);

                Node[] closestPair = findClosestPair(compA, compB);
                Node from = closestPair[0];
                Node to = closestPair[1];
                double distance = PMath.calculateDistance(from.latitude(), from.longitude(), to.latitude(), to.longitude());

                if (distance <= maxAllowedDistance) {
                    String edgeId = "artificial-" + i + "-" + j;
                    graph.addEdge(new Edge(edgeId, from, to, distance));
                    graph.addEdge(new Edge("reverse-" + edgeId, to, from, distance));
                    count++;
                    // System.out.printf("Добавлено искусственное ребро между компонентами %d и %d (расстояние: %.5f)%n", i, j, distance);
                } else {
                    // System.out.printf("Компоненты %d и %d слишком далеки (%.5f), ребро не добавлено.%n", i, j, distance);
                }
            }
        }

        System.out.println("Добавлено искусственных рёбер: " + count);
    }
}
