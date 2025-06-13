package org.sfedu.pathfinder.io;

import org.sfedu.pathfinder.model.*;
import org.sfedu.pathfinder.utils.PMath;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class OSMDataReader implements MapDataReader {
    private static final Set<String> VEHICLE_HIGHWAY_TYPES = Set.of(
            "motorway", "trunk", "primary", "secondary", "tertiary", "unclassified", "residential", "service",
            "living_street", "motorway_link", "trunk_link", "primary_link", "secondary_link", "tertiary_link",
            "track", "escape", "raceway", "road"
    );

    private static final Set<String> PEDESTRIAN_HIGHWAY_TYPES = Set.of(
            "footway", "path", "pedestrian", "steps", "bridleway", "corridor", "via_ferrata", "sidewalk",
            "crossing", "traffic_island", "cycleway", "track", "elevator", "escalator", "service", "living_street",
            "residential", "unclassified", "road", "platform", "proposed", "construction", "bus_stop", "rest_area",
            "emergency_bay", "abandoned", "razed", "historic", "disused", "motorway", "trunk", "primary", "secondary",
            "tertiary", "motorway_link", "trunk_link", "primary_link", "secondary_link", "tertiary_link",
            "give_way", "mini_roundabout"
    );

    @Override
    public Graph readGraph(RoadsType roadsType, InputStream inputStream) throws IOException {
        var graph = new Graph();
        var nodes = new HashMap<String, Node>();

        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(inputStream);

            while (reader.hasNext()) {
                int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT && "node".equals(reader.getLocalName())) {
                    String id = reader.getAttributeValue(null, "id");
                    double lat = Double.parseDouble(reader.getAttributeValue(null, "lat"));
                    double lon = Double.parseDouble(reader.getAttributeValue(null, "lon"));

                    Node node = new Node(id, lat, lon);
                    nodes.put(id, node);
                    graph.addNode(node);
                }

                if (event == XMLStreamConstants.START_ELEMENT && "way".equals(reader.getLocalName()))
                    processWay(roadsType, reader, nodes, graph);
            }

            reader.close();
        } catch (Exception e) {
            throw new IOException("Error reading OSM file", e);
        }

        return graph;
    }

    private void processWay(RoadsType roadsType, XMLStreamReader reader, Map<String, Node> nodes, Graph graph) throws Exception {
        WayParseResult wayParseResult = parseWay(reader);
        String highwayType = wayParseResult.tags().get("highway");

        if (highwayType == null)
            return;

        if (roadsType.equals(RoadsType.Vehicle) && !VEHICLE_HIGHWAY_TYPES.contains(highwayType))
            return;

        if (roadsType.equals(RoadsType.Pedestrian) && !PEDESTRIAN_HIGHWAY_TYPES.contains(highwayType))
            return;

        for (int i = 0; i < wayParseResult.nodeRefs().size() - 1; i++) {
            Node fromNode = nodes.get(wayParseResult.nodeRefs().get(i));
            Node toNode = nodes.get(wayParseResult.nodeRefs().get(i + 1));

            if (fromNode == null || toNode == null)
                continue;

            double weight = PMath.calculateDistance(fromNode, toNode);
            Edge edge = new Edge(wayParseResult.wayId() + "-" + i, fromNode, toNode, weight);
            graph.addEdge(edge);

            if ("yes".equals(wayParseResult.tags().get("oneway")))
                continue;

            graph.addEdge(new Edge(edge.id() + "-reverse", edge.to(), edge.from(), edge.weight()));
        }
    }

    private static WayParseResult parseWay(XMLStreamReader reader) throws XMLStreamException {
        String wayId = reader.getAttributeValue(null, "id");
        var nodeRefs = new ArrayList<String>();
        var tags = new HashMap<String, String>();

        while (!(reader.isEndElement() && "way".equals(reader.getLocalName()))) {
            int event = reader.next();

            if (event == XMLStreamConstants.START_ELEMENT) {
                if ("tag".equals(reader.getLocalName())) {
                    String key = reader.getAttributeValue(null, "k");
                    String value = reader.getAttributeValue(null, "v");

                    if (key != null && value != null)
                        tags.put(key, value);

                } else if ("nd".equals(reader.getLocalName())) {
                    String ref = reader.getAttributeValue(null, "ref");

                    if (ref != null)
                        nodeRefs.add(ref);
                }
            }
        }

        return new WayParseResult(wayId, nodeRefs, tags);
    }

    private record WayParseResult(String wayId, ArrayList<String> nodeRefs, HashMap<String, String> tags) { }
}