import java.io.IOException;

public class Consumer extends Thread {
    private final int id;
    private final ThreadSafeQueue<Processor> queue;

    public Consumer(int id, ThreadSafeQueue<Processor> queue) {
        this.id = id;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Wait for new element.
                Processor elem = queue.pop();

                // Stop consuming if null is received.
                if (elem == null) {
                    return;
                }
                // Process element.
                try {
                    elem.process();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}