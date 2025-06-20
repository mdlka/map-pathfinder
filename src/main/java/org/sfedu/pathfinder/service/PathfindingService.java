package org.sfedu.pathfinder.service;

import org.sfedu.pathfinder.model.Path;
import org.sfedu.pathfinder.algorithm.PathfindingAlgorithm;
import org.sfedu.pathfinder.io.MapDataReader;
import org.sfedu.pathfinder.model.RoadsType;
import org.sfedu.pathfinder.model.Graph;
import org.sfedu.pathfinder.model.Node;
import org.sfedu.pathfinder.utils.GraphUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class PathfindingService {
    private static final double MAX_NODE_SEARCH_RADIUS_IN_METERS = 50;

    private final PathfindingAlgorithm algorithm;
    private Graph graph;

    public PathfindingService(PathfindingAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void loadGraph(MapDataReader mapDataReader, RoadsType roadsType, InputStream is) throws IOException {
        this.graph = mapDataReader.readGraph(roadsType, is);
        removeExtraComponents();
    }

    public Path findPath(double startLat, double startLon, double targetLat, double targetLon) {
        if (graph == null)
            throw new IllegalStateException("Graph isn't load. Call loadGraph()");

        Node start = GraphUtils.findNearestNode(graph, startLat, startLon, MAX_NODE_SEARCH_RADIUS_IN_METERS);
        Node target = GraphUtils.findNearestNode(graph, targetLat, targetLon, MAX_NODE_SEARCH_RADIUS_IN_METERS);

        if (start == null || target == null)
            return new Path(List.of());

        return algorithm.findShortestPath(graph, start, target);
    }

    public Graph getGraph() {
        return graph;
    }

    private void removeExtraComponents() {
        List<Set<Node>> components = GraphUtils.findComponents(graph);
        int maxIndex = findComponentIndexWithMaxSize(components);

        for (int i = 0; i < components.size(); i++) {
            if (i == maxIndex)
                continue;

            for (Node node : components.get(i))
                graph.removeNodeById(node.id());
        }
    }

    private static int findComponentIndexWithMaxSize(List<Set<Node>> components) {
        int index = -1;
        int maxSize = -1;

        for (int i = 0; i < components.size(); i++) {
            int size = components.get(i).size();

            if (size > maxSize) {
                maxSize = size;
                index = i;
            }
        }
        return index;
    }
}
