package org.sfedu.pathfinder.io;

import org.sfedu.pathfinder.model.Graph;
import org.sfedu.pathfinder.model.RoadsType;

import java.io.IOException;
import java.io.InputStream;

public interface MapDataReader {
    Graph readGraph(RoadsType roadsType, InputStream inputStream) throws IOException;
}

