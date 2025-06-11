package org.sfedu.pathfinder.io;

import org.sfedu.pathfinder.model.Graph;

import java.io.IOException;
import java.io.InputStream;

public interface MapDataReader {
    enum RoadsType {
        Vehicle,
        Pedestrian
    }
    Graph readGraph(RoadsType roadsType, InputStream inputStream) throws IOException;
}
