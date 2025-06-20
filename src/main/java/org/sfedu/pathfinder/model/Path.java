package org.sfedu.pathfinder.model;

import org.sfedu.pathfinder.utils.PMath;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Path {
    private final List<Node> nodes;
    private double cachedTotalDistance = -1;

    public Path(List<Node> nodes) {
        this.nodes = new ArrayList<>(Objects.requireNonNull(nodes));
    }

    public int size() {
        return nodes.size();
    }

    public Node getNode(int nodeIndex) {
        return nodes.get(nodeIndex);
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public double getTotalDistance() {
        if (cachedTotalDistance >= 0)
            return cachedTotalDistance;

        if (nodes.size() < 2)
            return 0;

        double distance = 0;

        for (int i = 1; i < nodes.size(); i++) {
            Node from = nodes.get(i - 1);
            Node to = nodes.get(i);
            distance += PMath.calculateDistance(from, to);
        }

        return cachedTotalDistance = distance;
    }

    @Override
    public String toString() {
        return "Path { nodeCount=%d, totalDistance=%.2f km }"
                .formatted(nodes.size(), getTotalDistance());
    }
}