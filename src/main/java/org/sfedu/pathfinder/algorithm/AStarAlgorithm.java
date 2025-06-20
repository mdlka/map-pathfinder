package org.sfedu.pathfinder.algorithm;

import org.sfedu.pathfinder.model.Graph;
import org.sfedu.pathfinder.model.Node;
import org.sfedu.pathfinder.model.Path;
import org.sfedu.pathfinder.utils.PMath;

import java.util.*;

public class AStarAlgorithm implements PathfindingAlgorithm {
    @Override
    public Path findShortestPath(Graph graph, Node start, Node target) {
        PriorityQueue<Node> frontier = new PriorityQueue<>();
        Map<Node, Node> cameFrom = new HashMap<>();
        Map<Node, Double> costSoFar = new HashMap<>();

        frontier.add(start, 0);
        costSoFar.put(start, 0.0);
        cameFrom.put(start, null);

        while (!frontier.isEmpty()) {
            Node current = frontier.poll();

            if (current.equals(target))
                break;

            for (Node next : graph.getNeighbors(current)) {
                double newCost = costSoFar.get(current) + graph.getEdgeWeight(current, next);

                if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                    costSoFar.put(next, newCost);
                    double priority = newCost + heuristic(next, target);
                    frontier.add(next, priority);
                    cameFrom.put(next, current);
                }
            }
        }

        if (!cameFrom.containsKey(target))
            return new Path(Collections.emptyList());

        List<Node> path = new ArrayList<>();
        Node current = target;

        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }

        Collections.reverse(path);
        return new Path(path);
    }

    private double heuristic(Node a, Node b) {
        return PMath.calculateDistance(a, b);
    }
}