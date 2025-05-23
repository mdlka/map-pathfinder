package org.sfedu.pathfinder.io;

import org.sfedu.pathfinder.model.Graph;

import java.io.InputStream;

public interface MapDataReader {
    Graph readGraph(InputStream inputStream);
}
