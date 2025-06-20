package org.sfedu.pathfinder.io;

import org.sfedu.pathfinder.model.Graph;
import org.sfedu.pathfinder.model.Node;
import org.sfedu.pathfinder.model.Path;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GeoJsonExporter {

    public static void exportGraphToGeoJson(Graph graph, String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

        writer.write("{\n");
        writer.write("  \"type\": \"FeatureCollection\",\n");
        writer.write("  \"features\": [\n");

        boolean first = true;

        for (Node from : graph.getAllNodes()) {
            for (Node to : graph.getNeighbors(from)) {
                if (!first)
                    writer.write(",\n");

                writeLineString(writer, from, to);
                first = false;
            }
        }

        writer.write("\n  ]\n");
        writer.write("}\n");
        writer.close();
    }

    public static void exportPathToGeoJson(Path path, String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

        writer.write("{\n");
        writer.write("  \"type\": \"FeatureCollection\",\n");
        writer.write("  \"features\": [\n");

        for (int i = 0; i < path.size() - 1; i++) {
            if (i > 0)
                writer.write(",\n");

            writeLineString(writer, path.getNode(i), path.getNode(i + 1));
        }

        if (!path.isEmpty()) {
            writer.write(",\n");
            writePoint(writer, path.getNode(0), "Start", "#3b7a1d"); // зеленый
            writer.write(",\n");
            writePoint(writer, path.getNode(path.size() - 1), "End", "#e6194b"); // красный
        }

        writer.write("\n  ]\n");
        writer.write("}\n");
        writer.close();
    }

    private static void writeLineString(BufferedWriter writer, Node from, Node to) throws IOException {
        writer.write("    {\n");
        writer.write("      \"type\": \"Feature\",\n");
        writer.write("      \"properties\": {},\n");
        writer.write("      \"geometry\": {\n");
        writer.write("        \"type\": \"LineString\",\n");
        writer.write("        \"coordinates\": [\n");
        writer.write("          [" + from.longitude() + ", " + from.latitude() + "],\n");
        writer.write("          [" + to.longitude() + ", " + to.latitude() + "]\n");
        writer.write("        ]\n");
        writer.write("      }\n");
        writer.write("    }");
    }

    private static void writePoint(BufferedWriter writer, Node node, String name, String color) throws IOException {
        writer.write("    {\n");
        writer.write("      \"type\": \"Feature\",\n");
        writer.write("      \"properties\": {\n");
        writer.write("        \"name\": \"" + name + "\"");

        if (color != null && !color.isEmpty())
            writer.write(",\n        \"marker-color\": \"" + color + "\"");

        writer.write("\n      },\n");
        writer.write("      \"geometry\": {\n");
        writer.write("        \"type\": \"Point\",\n");
        writer.write("        \"coordinates\": [" + node.longitude() + ", " + node.latitude() + "]\n");
        writer.write("      }\n");
        writer.write("    }");
    }
}