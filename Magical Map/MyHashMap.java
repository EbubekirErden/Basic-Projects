public class MyHashMap<K, V> implements Cloneable { // Generic HashMap Class

    private static class MapEntry<K, V> { // Private Entry class
        K key;
        V value;
        MapEntry<K, V> next;

        public MapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private static final int DEFAULT_CAPACITY = 547;
    private static final float LOAD_FACTOR = 0.85f;
    private int size;
    private MapEntry<K, V>[] hashTable;
    private MyHashSet<K> keys;

    public MyHashMap() {
        hashTable = new MapEntry[DEFAULT_CAPACITY];
        keys = new MyHashSet<>();
        size = 0;
    }

    /**
     * Clones hash map
     * @return cloned map
     */
    @Override
    public MyHashMap<K, V> clone() {
        MyHashMap<K, V> mhm = new MyHashMap<>();
        for (MapEntry<K, V> entry : hashTable) {
            if (entry != null) mhm.put(entry.key, entry.value);
        }
        mhm.keys = this.keys.clone();
        mhm.size = this.size;
        return mhm;
    }

    /**
     * Gets table index
     * @param key variable to be hashed
     * @return index of key
     */
    private int getIndex(K key) {
        return (key.hashCode() & 0x7FFFFFFF) % hashTable.length;
    }

    /**
     * Puts key value pair to hash table
     * @param key key
     * @param value value
     */
    public void put(K key, V value) {
        if ((double) size / hashTable.length > LOAD_FACTOR) resize();

        int index = getIndex(key);
        MapEntry<K,V> entry = hashTable[index];

        while (entry != null) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
            entry = entry.next;
        }

        MapEntry<K,V> newEntry = new MapEntry<>(key, value);
        newEntry.next = hashTable[index];
        hashTable[index] = newEntry;
        keys.add(key);
        size++;
    }

    /**
     * If value of key is absent it puts default value given
     * @param key key
     * @param value default value
     */
    public void putIfAbsent(K key, V value) {
        if (get(key) != null) return;
        put(key, value);
    }

    /**
     * Gets value of a key
     * @param key key
     * @return value of key
     */
    public V get(K key) {
        int index = getIndex(key);
        MapEntry<K,V> entry = hashTable[index];

        while (entry != null) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
            entry = entry.next;
        }
        return null;
    }

    /**
     * If value does not exist it return default value
     * @param key key
     * @param defaultValue default value
     * @return default value or real value
     */
    public V getOrDefault(K key, V defaultValue) {
        V value = get(key);

        if (value != null) return value;
        else {
            put(key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Checks if key in hash table
     * @param key key
     * @return True if contains, false if not
     */
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Remove a key from table
     * @param key key to be removed
     */
    public void remove(K key) {
        int index = getIndex(key);
        MapEntry<K,V> entry = hashTable[index];
        MapEntry<K,V> prev = entry;

        while (entry != null) {
            if (entry.key.equals(key)) {
                if (prev == null) hashTable[index] = entry.next;
                else prev.next = entry.next;
                size--;
                return;
            }
            prev = entry;
            entry = entry.next;
        }
    }

    /**
     * Resize method for if the hash table becomes overloaded
     */
    private void resize() {
        MapEntry<K,V>[] oldHashTable = hashTable;
        hashTable = new MapEntry[oldHashTable.length * 2];
        size = 0;

        for (MapEntry<K,V> entry : oldHashTable) {
            while (entry != null) {
                put(entry.key, entry.value);
                entry = entry.next;
            }
        }
    }

    /**
     * @return size of entries
     */
    public int size() {
        return size;
    }

    /**
     * @return set of keys
     */
    public MyHashSet<K> keySet() {
        return keys;
    }

    /**
     * Clears hash table to reuse
     */
    public void clear() {
        hashTable = new MapEntry[DEFAULT_CAPACITY];
        size = 0;
        keys.clear();
    }
}
