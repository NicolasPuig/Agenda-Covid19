package Planificador;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * Cola FCFS Generica sincronizada
 *
 * @author NicoPuig
 * @param <T>
 */
public class FCFSQueue<T> {

    private final Queue<T> queue = new LinkedList<>();

    public synchronized void push(T obj) {
        queue.add(obj);
    }

    public synchronized T pop() {
        return queue.poll();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
    
}