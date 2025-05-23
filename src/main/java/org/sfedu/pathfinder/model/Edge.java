package org.sfedu.pathfinder.model;

public record Edge(String id, Node from, Node to, double weight) {
    @Override
    public String toString() {
        return "Edge { id='%s', from=%s, to=%s, weight=%s }"
                .formatted(id, from.id(), to.id(), weight);
    }
}