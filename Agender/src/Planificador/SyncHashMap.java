package Planificador;

import java.util.HashMap;
import java.util.concurrent.Semaphore;

/**
 *
 * @author PaoloMazza, SebaMazzey, NicoPuig
 * @param <K>
 * @param <V>
 */
public class SyncHashMap<K extends Comparable, V> {

    private final Semaphore mutex = new Semaphore(1, true);
    private final HashMap<K, V> map;

    public SyncHashMap() {
        this.map = new HashMap<>();
    }

    public SyncHashMap(int initialCapacity) {
        this.map = new HashMap<>(initialCapacity);
    }

    public void put(K key, V value) {
        try {
            mutex.acquire();
            map.put(key, value);
            mutex.release();
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
    }

    public V get(K key) {
        try {
            mutex.acquire();
            V value = map.get(key);
            mutex.release();
            return value;
        } catch (InterruptedException ex) {
            System.out.println(ex);
            return null;
        }
    }

    public V remove(K key) {
        try {
            mutex.acquire();
            V value = map.remove(key);
            mutex.release();
            return value;
        } catch (InterruptedException ex) {
            System.out.println(ex);
            return null;
        }
    }
}
