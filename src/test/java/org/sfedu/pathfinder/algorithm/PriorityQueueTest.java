package org.sfedu.pathfinder.algorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class PriorityQueueTest {
    private PriorityQueue<String> queue;

    @BeforeEach
    void setUp() {
        queue = new PriorityQueue<>();
    }

    @Test
    void testIsEmpty_WhenCreated_ReturnsTrue() {
        assertTrue(queue.isEmpty());
    }

    @Test
    void testAddAndPoll_SingleElement_ReturnsSameElement() {
        queue.add("A", 10);

        assertEquals("A", queue.poll());
        assertTrue(queue.isEmpty());
    }

    @Test
    void testPoll_EmptyQueue_ThrowsException() {
        assertThrows(NoSuchElementException.class, () -> queue.poll());
    }

    @Test
    void testAddMultipleElements_PollReturnsMinInOrder() {
        queue.add("C", 3);
        queue.add("A", 1);
        queue.add("B", 2);

        assertEquals("A", queue.poll());
        assertEquals("B", queue.poll());
        assertEquals("C", queue.poll());
        assertTrue(queue.isEmpty());
    }

    @Test
    void testContains_ReturnsTrueIfAdded() {
        queue.add("X", 5);
        assertTrue(queue.contains("X"));
    }

    @Test
    void testContains_ReturnsFalseIfNotAdded() {
        assertFalse(queue.contains("Y"));
    }

    @Test
    void testGetPriority_ReturnsCorrectValue() {
        queue.add("A", 7.5);
        assertEquals(7.5, queue.getPriority("A"), 0.001);
    }

    @Test
    void testGetPriority_UnknownItem_ReturnsInfinity() {
        assertEquals(Double.POSITIVE_INFINITY, queue.getPriority("Unknown"));
    }

    @Test
    void testDecreaseKey_UpdatesPriorityAndOrder() {
        queue.add("Low", 10);
        queue.add("Medium", 5);
        queue.add("High", 1);

        queue.decreaseKey("Low", 0.5);

        assertEquals("Low", queue.poll());
        assertEquals("High", queue.poll());
        assertEquals("Medium", queue.poll());
    }

    @Test
    void testDecreaseKey_ThrowsIfNewPriorityIsHigher() {
        queue.add("Item", 1);
        assertThrows(IllegalArgumentException.class, () -> queue.decreaseKey("Item", 2));
    }

    @Test
    void testAddExistingItem_UpdatesPriority() {
        queue.add("X", 10);
        queue.add("X", 5);

        assertEquals("X", queue.poll());
        assertTrue(queue.isEmpty());
    }

    @Test
    void testPoll_MaintainsHeapProperty() {
        queue.add("A", 4);
        queue.add("B", 3);
        queue.add("C", 2);
        queue.add("D", 1);

        assertEquals("D", queue.poll());
        assertEquals("C", queue.poll());
        assertEquals("B", queue.poll());
        assertEquals("A", queue.poll());
    }
}