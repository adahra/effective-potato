package com.test.wafuco.utility;

import java.util.HashMap;
import java.util.Map;

public class HeapSet<T extends Comparable<T>> {
    private final Map<T, Integer> map;
    private final IDPool idPool;
    private int[] indices;
    private T[] nodes;
    private int count;

    @SuppressWarnings("unchecked")
    public HeapSet(int capacity) {
        capacity = nextPowerOfTwo(capacity);
        indices = new int[capacity];
        idPool = new IDPool();
        nodes = (T[]) new Comparable[capacity];
        map = new HashMap<>(capacity * 2);
    }

    /**
     * Adds a new item "a" to the heap or updates existing "b".
     * If an item "b" already exists, where a.equals(b):
     * if "a" != "b", "b" is replaced with "a". Regardless
     * we re-compare the item with parent and child nodes,
     * and update its placement in the heap if required.
     * otherwise, if no item exists where a.equals(b) then "a" is added.
     * 
     * @param item item to add
     */
    public void set(T item) {
        Integer node_id = map.get(item);
        if (node_id == null) {
            if (count == nodes.length)
                grow();
            int new_id = idPool.obtainID();
            map.put(item, new_id);
            indices[new_id] = count;
            nodes[count] = item;
            up(count++, new_id);
        } else {
            int current_index = indices[node_id];
            nodes[current_index] = item;
            T parent = nodes[parent(current_index)];
            if (item.compareTo(parent) > 0) {
                up(current_index, node_id);
            } else
                down(current_index, node_id);
        }
    }

    /**
     * Get a item "a" where "a.equals(item)" or null.
     * Even if the nodes are equal, their contents might not
     * be the same.
     * 
     * @param item The item to "equal-compare" with.
     * @return an item "a" where "a.equals(item)" or null
     */
    public T get(T item) {
        Integer i = map.get(item);
        return i == null ? null : nodes[indices[i]];
    }

    public T pop() {
        T removed = nodes[0];
        idPool.returnID(map.remove(removed));
        nodes[0] = nodes[--count];
        nodes[count] = null;
        if (count > 0) {
            T moved = nodes[0];
            int moved_id = map.get(moved);
            indices[moved_id] = 0;
            if (count > 1)
                down(0, moved_id);
        }
        return removed;
    }

    public boolean contains(T item) {
        return map.containsKey(item);
    }

    public T peak() {
        return nodes[0];
    }

    public int size() {
        return count;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public boolean notEmpty() {
        return count > 0;
    }

    private void up(int index, int id) {
        T node = nodes[index];
        while (index > 0) {
            int parent_index = parent(index);
            T parent = nodes[parent_index];
            if (node.compareTo(parent) > 0) {
                nodes[index] = parent;
                indices[map.get(parent)] = index;
                index = parent_index;
            } else
                break;
        }
        nodes[index] = node;
        indices[id] = index;
    }

    private void down(int index, int id) {
        final T node = nodes[index];
        while (true) {
            int left_index = 1 + (index * 2);
            int right_index = left_index + 1;
            if (left_index >= count)
                break;
            if (right_index < count) {
                T right_node = nodes[right_index];
                T left_node = nodes[left_index];
                if (right_node.compareTo(left_node) > 0) {
                    if (node.compareTo(right_node) >= 0)
                        break;
                    nodes[index] = right_node;
                    indices[map.get(right_node)] = index;
                    index = right_index;
                } else {
                    if (node.compareTo(left_node) >= 0)
                        break;
                    nodes[index] = left_node;
                    indices[map.get(left_node)] = index;
                    index = left_index;
                }
            } else {
                T left_node = nodes[left_index];
                if (node.compareTo(left_node) >= 0)
                    break;
                nodes[index] = left_node;
                indices[map.get(left_node)] = index;
                index = left_index;
            }
        }
        nodes[index] = node;
        indices[id] = index;
    }

    @SuppressWarnings("unchecked")
    private void grow() {
        T[] old_nodes = nodes;
        nodes = (T[]) new Comparable[count << 1];
        System.arraycopy(old_nodes, 0, nodes, 0, count);
        int[] old_indices = indices;
        indices = new int[nodes.length];
        System.arraycopy(old_indices, 0, indices, 0, count);
    }

    private int nextPowerOfTwo(int value) {
        if (value-- == 0)
            return 1;
        value |= value >>> 1;
        value |= value >>> 2;
        value |= value >>> 4;
        value |= value >>> 8;
        value |= value >>> 16;
        return value + 1;
    }

    private int parent(int index) {
        return (index - 1) / 2;
    }

    private int right(int index) {
        return 2 + (index * 2);
    }

    private int left(int index) {
        return 1 + (index * 2);
    }

    private int indexOf(T node) {
        return indices[map.get(node)];
    }

    private final static class IDPool {
        private final IntQueue ids = new IntQueue(16);
        private int new_id = 0;

        public int obtainID() {
            if (ids.isEmpty()) {
                return new_id++;
            } else
                return ids.dequeue();
        }

        public void returnID(int id) {
            ids.enqueue(id);
        }
    }
}
