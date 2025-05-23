package org.sfedu.pathfinder.utils;

import org.sfedu.pathfinder.model.Graph;
import org.sfedu.pathfinder.model.Node;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import static org.sfedu.pathfinder.utils.PMath.calculateDistance;

public class GraphUtils {
    public static boolean isReachable(Graph graph, Node start, Node target) {
        Set<Node> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.equals(target))
                return true;

            for (Node neighbor : graph.getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return false;
    }

    public static Node findNearestNode(Graph graph, double lat, double lon, double maxRadiusMeters) {
        Node nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Node node : graph.getAllNodes()) {
            double distance = calculateDistance(lat, lon, node.latitude(), node.longitude());

            if (distance < minDistance && distance <= maxRadiusMeters) {
                minDistance = distance;
                nearest = node;
            }
        }

        return nearest;
    }
}
