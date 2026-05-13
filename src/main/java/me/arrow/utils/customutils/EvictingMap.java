package me.arrow.utils.customutils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@RequiredArgsConstructor
public class EvictingMap<K, V> extends HashMap<K, V> {

    @Getter
    private final int size;
    private final Deque<K> storedKeys = new LinkedList<>();

    @Override
    public boolean remove(Object key, Object value) {
        storedKeys.removeFirstOccurrence(key); // Safely remove first occurrence
        return super.remove(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (!containsKey(key)) {
            checkAndRemove();
            storedKeys.addLast(key);
        }
        return super.putIfAbsent(key, value);
    }

    @Override
    public V put(K key, V value) {
        checkAndRemove();
        storedKeys.addLast(key);
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        storedKeys.clear();
        super.clear();
    }

    @Override
    public V remove(Object key) {
        storedKeys.removeFirstOccurrence(key); // Safely remove first occurrence
        return super.remove(key);
    }

    private void checkAndRemove() {
        // Use pollFirst() to safely remove and return null if empty
        if (storedKeys.size() >= size) {
            K firstKey = storedKeys.pollFirst();
            if (firstKey != null) {
                super.remove(firstKey);
            }
        }
    }
}
