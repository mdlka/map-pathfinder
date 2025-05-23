package org.sfedu.pathfinder.model;

public record Node(String id, double latitude, double longitude) {
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Node node = (Node) o;
        return id.equals(node.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Node { id='%s', lat=%s, lon=%s }"
                .formatted(id, latitude, longitude);
    }
}