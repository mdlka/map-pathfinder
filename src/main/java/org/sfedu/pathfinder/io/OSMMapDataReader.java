package org.sfedu.pathfinder.io;

import org.sfedu.pathfinder.model.*;
import org.sfedu.pathfinder.utils.PMath;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.*;

public class OSMMapDataReader implements MapDataReader {
    private static final Set<String> ALLOWED_HIGHWAY_TYPES = Set.of(
            // Автомобильные дороги
            "motorway",       // Магистральные дороги
            "trunk",          // Основные дороги
            "primary",        // Главные дороги
            "secondary",      // Второстепенные дороги
            "tertiary",       // Третичные дороги
            "unclassified",   // Неклассифицированные дороги
            "residential",    // Улицы жилых районов
            "service",        // Сервисные дороги
            "living_street",  // Жилая улица (ограничение скорости)

            // Пешие/велодорожки
            "footway",        // Пешеходные дорожки
            "path",           // Тропинки
            "pedestrian",     // Пешеходные зоны
            "steps"           // Лестницы
    );

    @Override
    public Graph readGraph(InputStream inputStream) {
        Graph graph = new Graph();
        Map<String, Node> nodes = new HashMap<>();

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

                if (event == XMLStreamConstants.START_ELEMENT && "way".equals(reader.getLocalName())) {
                    processWay(reader, nodes, graph);
                }
            }

            reader.close();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при чтении OSM файла", e);
        }

        return graph;
    }

    private void processWay(XMLStreamReader reader, Map<String, Node> nodes, Graph graph) throws Exception {
        String wayId = reader.getAttributeValue(null, "id");
        List<String> nodeRefs = new ArrayList<>();
        Map<String, String> tags = new HashMap<>();

        // Считываем все теги <tag k="..." v="..."/> и ссылки на узлы <nd ref="..."/>
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

        String highwayType = tags.get("highway");

        if (highwayType == null || !ALLOWED_HIGHWAY_TYPES.contains(highwayType))
            return;

        for (int i = 0; i < nodeRefs.size() - 1; i++) {
            Node fromNode = nodes.get(nodeRefs.get(i));
            Node toNode = nodes.get(nodeRefs.get(i + 1));

            if (fromNode != null && toNode != null) {
                double weight = PMath.calculateDistance(fromNode, toNode);
                Edge edge = new Edge(wayId + "-" + i, fromNode, toNode, weight);
                graph.addEdge(edge);

                if (!"yes".equals(tags.get("oneway")))
                    graph.addEdge(new Edge(edge.id() + "-reverse", edge.to(), edge.from(), edge.weight()));
            }
        }
    }
}