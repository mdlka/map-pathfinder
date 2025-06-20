package org.sfedu.pathfinder.io;

import org.sfedu.pathfinder.model.Graph;
import org.sfedu.pathfinder.model.RoadsType;

import java.io.IOException;

public interface MapDataReader {
    Graph readGraph(String filePath, RoadsType roadsType) throws IOException;
}

