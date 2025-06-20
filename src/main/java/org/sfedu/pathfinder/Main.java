package org.sfedu.pathfinder;

import org.sfedu.pathfinder.algorithm.AStarAlgorithm;
import org.sfedu.pathfinder.io.OSMDataReader;
import org.sfedu.pathfinder.service.PathfindingService;
import org.sfedu.pathfinder.ui.ConsoleApp;

public class Main {
    public static void main(String[] args) {
        var service = new PathfindingService(new OSMDataReader(), new AStarAlgorithm());
        var app = new ConsoleApp(service);

        app.start();
    }
}