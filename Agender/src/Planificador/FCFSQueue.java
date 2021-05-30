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
    private final Semaphore mutex = new Semaphore(1);

    public void push(T obj) throws InterruptedException {
        mutex.acquire();
        queue.add(obj);
        mutex.release();
    }

    public T pop() throws InterruptedException {
        mutex.acquire();
        T obj = queue.poll();
        mutex.release();
        return obj;
    }

    public boolean isEmpty() throws InterruptedException {
        mutex.acquire();
        boolean isEmpty = queue.isEmpty();
        mutex.release();
        return isEmpty;
    }
}
