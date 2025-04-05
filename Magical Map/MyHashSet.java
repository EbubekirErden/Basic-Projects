import java.util.ArrayList;
import java.util.Iterator;

public class MyHashSet<T> implements Cloneable, Iterable<T> { // Generic HashSet Class
    private static class SetEntry<T> { // Private set entry class
        T key;
        SetEntry<T> next;

        public SetEntry(T key) {
            this.key = key;
        }
    }

    private static final int DEFAULT_CAPACITY = 1499;
    private static final float LOAD_FACTOR = 0.85f;
    private int size;
    private SetEntry<T>[] hashTable;

    public MyHashSet() {
        hashTable = new SetEntry[DEFAULT_CAPACITY];
        size = 0;
    }

    /**
     * @param key key to be indexed
     * @return index of key according to its hash code
     */
    private int getIndex(T key) {
        return (key.hashCode() & 0x7fffffff) % hashTable.length;
    }

    /**
     * Adds key to the hash table
     * @param key key to be added
     * @return true if correctly added, false otherwise
     */
    public boolean add(T key) {
        if ((double) size / hashTable.length > LOAD_FACTOR) resize();

        int index = getIndex(key);
        SetEntry<T> entry = hashTable[index];

        while (entry != null) {
            if (entry.key.equals(key)) return false;
            entry = entry.next;
        }

        SetEntry<T> newEntry = new SetEntry(key);
        newEntry.next = hashTable[index];
        hashTable[index] = newEntry;
        size++;
        return true;
    }

    /**
     * Removes a key from the table
     * @param key key to be removed
     * @return true if correctly removed, false otherwise
     */
    public boolean remove(T key) {
        int index = getIndex(key);
        SetEntry<T> entry = hashTable[index];
        SetEntry<T> prev = null;

        while (entry != null) {
            if (entry.key.equals(key)) {
                if (prev == null) hashTable[index] = entry.next;
                else prev.next = entry.next;
                size--;
                return true;
            }
            prev = entry;
            entry = entry.next;
        }
        return false;
    }

    /**
     * Checks if key exist in table or not
     * @param key key to be checked
     * @return true if table contains key, false if not
     */
    public boolean contains(T key) {
        int index = getIndex(key);
        SetEntry<T> entry = hashTable[index];

        while (entry != null) {
            if (entry.key.equals(key)) return true;
            entry = entry.next;
        }
        return false;
    }

    /**
     * @return size of entries
     */
    public int size() {
        return size;
    }

    /**
     * Resizes hash table
     */
    private void resize() {
        SetEntry<T>[] oldHashTable = hashTable;
        hashTable = new SetEntry[oldHashTable.length * 2];
        size = 0;
        for (SetEntry<T> entry : oldHashTable) {
            while (entry != null) {
                add(entry.key);
                entry = entry.next;
            }
        }
    }

    /**
     * Clears hash table to reuse
     */
    public void clear() {
        hashTable = new SetEntry[DEFAULT_CAPACITY];
        size = 0;
    }

    /**
     * Clone method
     * @return cloned set
     */
    @Override
    public MyHashSet<T> clone() {
        MyHashSet<T> clone = new MyHashSet<>();
        for (T key : this) {
            clone.add(key);
        }
        return clone;
    }

    /**
     * Iterator method
     * @return an iterable
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int bucketIndex = 0;
            private SetEntry<T> current = null;

            private void advanceToNext() { // finds next element
                while (current == null && bucketIndex < hashTable.length) {
                    current = hashTable[bucketIndex++];
                }
            }

            @Override
            public boolean hasNext() { // checks if next element exist
                advanceToNext();
                return current != null;
            }

            @Override
            public T next() { // moves to next element
                advanceToNext();
                T key = current.key;
                current = current.next;
                return key;
            }
        };
    }
}
