package org.sfedu.pathfinder.utils;

import org.sfedu.pathfinder.model.Node;

public class PMath {
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dx = lon2 - lon1;
        double dy = lat2 - lat1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double calculateDistance(Node a, Node b) {
        return calculateDistance(a.latitude(), a.longitude(), b.latitude(), b.longitude());
    }
}
