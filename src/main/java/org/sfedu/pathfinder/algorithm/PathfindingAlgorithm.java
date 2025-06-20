package org.sfedu.pathfinder.algorithm;

import org.sfedu.pathfinder.model.Graph;
import org.sfedu.pathfinder.model.Node;
import org.sfedu.pathfinder.model.Path;

public interface PathfindingAlgorithm {
    Path findShortestPath(Graph graph, Node start, Node target);
}
