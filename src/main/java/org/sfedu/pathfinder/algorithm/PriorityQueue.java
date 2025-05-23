package org.sfedu.pathfinder.algorithm;

import java.util.*;

public class PriorityQueue<T> {
    private final List<Entry<T>> heap = new ArrayList<>();
    private final Map<T, Double> valueMap = new HashMap<>();

    public static class Entry<T> implements Comparable<Entry<T>> {
        T item;
        double priority;

        public Entry(T item, double priority) {
            this.item = item;
            this.priority = priority;
        }

        @Override
        public int compareTo(Entry<T> other) {
            return Double.compare(this.priority, other.priority);
        }
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public void add(T item, double priority) {
        if (valueMap.containsKey(item)) {
            decreaseKey(item, priority);
            return;
        }

        Entry<T> entry = new Entry<>(item, priority);
        heap.add(entry);
        valueMap.put(item, priority);
        siftUp(size() - 1);
    }

    public T poll() {
        if (isEmpty())
            throw new NoSuchElementException();

        Entry<T> root = heap.get(0);
        valueMap.remove(root.item);

        swap(0, size() - 1);
        heap.remove(size() - 1);

        if (!isEmpty())
            siftDown(0);

        return root.item;
    }

    public boolean contains(T item) {
        return valueMap.containsKey(item);
    }

    public double getPriority(T item) {
        return valueMap.getOrDefault(item, Double.POSITIVE_INFINITY);
    }

    public void decreaseKey(T item, double newPriority) {
        for (int i = 0; i < heap.size(); i++) {
            Entry<T> entry = heap.get(i);

            if (entry.item.equals(item)) {
                if (newPriority > entry.priority)
                    throw new IllegalArgumentException("New priority is greater than current");

                entry.priority = newPriority;
                valueMap.put(item, newPriority);
                siftUp(i);
                break;
            }
        }
    }

    private int size() {
        return heap.size();
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;

            if (heap.get(index).compareTo(heap.get(parent)) >= 0)
                break;

            swap(index, parent);
            index = parent;
        }
    }

    private void siftDown(int index) {
        int minIndex = index;
        int left = 2 * index + 1;
        int right = 2 * index + 2;

        if (left < size() && heap.get(left).compareTo(heap.get(minIndex)) < 0)
            minIndex = left;

        if (right < size() && heap.get(right).compareTo(heap.get(minIndex)) < 0)
            minIndex = right;

        if (minIndex != index) {
            swap(index, minIndex);
            siftDown(minIndex);
        }
    }

    private void swap(int i, int j) {
        Collections.swap(heap, i, j);
    }
}