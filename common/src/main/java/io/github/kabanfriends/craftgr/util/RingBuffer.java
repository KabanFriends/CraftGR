package io.github.kabanfriends.craftgr.util;

public class RingBuffer<T> {

    private final T[] buffer;
    private final int capacity;

    private int head;
    private int tail;
    private int size;

    @SuppressWarnings("unchecked")
    public RingBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = (T[]) new Object[capacity];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    public void add(T element) {
        buffer[tail] = element;
        tail = (tail + 1) % capacity;
        if (size < capacity) {
            size++;
        } else {
            head = (head + 1) % capacity;
        }
    }

    public void removeHeadN(int n) {
        if (n <= 0) {
            return;
        }
        if (n > size) {
            throw new IllegalArgumentException("Count out of bounds: " + n);
        }
        for (int i = 0; i < n; i++) {
            buffer[head] = null;
            head = (head + 1) % capacity;
        }
        size -= n;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        int actualIndex = (head + index) % capacity;
        return (T) buffer[actualIndex];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }
}
