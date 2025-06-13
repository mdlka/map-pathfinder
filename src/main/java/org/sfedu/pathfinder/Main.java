package org.sfedu.pathfinder;

import org.sfedu.pathfinder.algorithm.AStarAlgorithm;
import org.sfedu.pathfinder.io.GeoJsonExporter;
import org.sfedu.pathfinder.io.OSMDataReader;
import org.sfedu.pathfinder.io.RoadsType;
import org.sfedu.pathfinder.service.PathfindingService;

public class Main {
    public static void main(String[] args) {
        try {
            var service = new PathfindingService(new AStarAlgorithm());
            service.loadGraph(new OSMDataReader(), RoadsType.Pedestrian, Main.class.getResourceAsStream("/map.xml"));

            var path = service.findPath(
                    47.20871377101845, 39.62857697262525,
                    47.265546620621464, 39.87561805519811
            );

            GeoJsonExporter.exportGraphToGeoJson(service.getGraph(), "graph.geojson");

            if (path.isEmpty()) {
                System.out.println("Path is empty.");
            } else {
                System.out.printf("Path found. Total distance: %f km%n", path.getTotalDistance() * 100);
                GeoJsonExporter.exportPathToGeoJson(path, "path.geojson");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}