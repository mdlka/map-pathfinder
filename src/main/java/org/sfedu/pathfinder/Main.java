package org.sfedu.pathfinder;

import org.sfedu.pathfinder.algorithm.AStarAlgorithm;
import org.sfedu.pathfinder.io.GeoJsonExporter;
import org.sfedu.pathfinder.io.OSMMapDataReader;
import org.sfedu.pathfinder.service.PathfindingService;

public class Main {
    public static void main(String[] args) {
        try {
            var service = new PathfindingService(new AStarAlgorithm());
            service.loadGraph(new OSMMapDataReader(), Main.class.getResourceAsStream("/map1.xml"));

            var path = service.findPath(
                    47.2148, 39.6632,
                    47.2148, 39.6635
            );

            GeoJsonExporter.exportGraphToGeoJson(service.getGraph(), "graph.geojson");

            if (path.isEmpty()) {
                System.out.println("Path is empty.");
            } else {
                System.out.printf("Path found. Total distance: %f%n", path.getTotalDistance());
                GeoJsonExporter.exportPathToGeoJson(path, "path.geojson");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}