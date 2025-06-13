package org.sfedu.pathfinder.utils;

import org.sfedu.pathfinder.model.Graph;
import org.sfedu.pathfinder.model.Node;

import java.util.*;

public class GraphUtils {
    public static Node findNearestNode(Graph graph, double lat, double lon, double maxRadiusMeters) {
        Node nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Node node : graph.getAllNodes()) {
            double distance = PMath.calculateDistance(lat, lon, node.latitude(), node.longitude());

            if (distance < minDistance && distance <= maxRadiusMeters) {
                minDistance = distance;
                nearest = node;
            }
        }

        return nearest;
    }

    public static List<Set<Node>> findComponents(Graph graph) {
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
}
