package Planificador;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * Cola FCFS Generica sincronizada
 * @author NicoPuig
 * @param <T>
 */
public class FCFSQueue<T> {

    private final Queue<T> queue = new LinkedList<>();
    private final Semaphore mutex = new Semaphore(1, true);

    public void push(T obj) {
        try {
            mutex.acquire();
            queue.add(obj);
            mutex.release();
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
    }

    public T pop() {
        try {
            mutex.acquire();
            T object = queue.poll();
            mutex.release();
            return object;
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
        return null;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
