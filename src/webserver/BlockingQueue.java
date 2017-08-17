package webserver;

import java.util.LinkedList;

/**
 *
 * @author Wilmer
 */
public class BlockingQueue {

    private final LinkedList list = new LinkedList();
    private boolean isClosed = false;

    synchronized public void enqueue(Object obj) {
        if (isClosed) {
            throw new ClosedException();
        }
        list.add(obj);
        notify();
    }

    synchronized public Object dequeue() {
        while (!isClosed && list.isEmpty()) {
            try {
                // Esperar hasta que haya un nuevo response
                wait();
            } catch (InterruptedException e) {
                // Ignorar
            }
        }
        if (list.isEmpty()) {
            return null;
        }
        return list.removeFirst();
    }

    synchronized public int size() {
        return list.size();
    }

    synchronized public void close() {
        isClosed = true;
        notifyAll();
    }

    synchronized public void open() {
        isClosed = false;
    }

    public static class ClosedException extends RuntimeException {

        ClosedException() {
            super("Queue closed.");
        }
    }
}
