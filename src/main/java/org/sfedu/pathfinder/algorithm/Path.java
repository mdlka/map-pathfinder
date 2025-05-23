package org.sfedu.pathfinder.algorithm;

import org.sfedu.pathfinder.model.Node;
import org.sfedu.pathfinder.utils.PMath;

import java.util.List;

public record Path(List<Node> nodes) {

    public boolean isEmpty() {
        return nodes == null || nodes.isEmpty();
    }

    public double getTotalDistance() {
        if (nodes.size() < 2)
            return 0;

        double distance = 0;

        for (int i = 1; i < nodes.size(); i++) {
            Node from = nodes.get(i - 1);
            Node to = nodes.get(i);
            distance += PMath.calculateDistance(from, to);
        }

        return distance;
    }

    @Override
    public String toString() {
        return "Path { nodeCount=%d, totalDistance=%.2f km }"
                .formatted(nodes.size(), getTotalDistance());
    }
}