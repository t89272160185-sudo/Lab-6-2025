package threads;

import functions.Function;
import functions.basic.Log;

import java.util.Random;

public class Generator extends Thread {
    private final Task task;
    private final OnePlaceSemaphore semaphore;
    private final Random random = new Random();

    public Generator(Task task, OnePlaceSemaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        for (int i = 0; i < task.getTasksCount(); i++) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            double base = 1.0 + random.nextDouble() * 9.0;
            Function function = new Log(base);
            double left = Math.max(1e-3, random.nextDouble() * 100.0); // clamp to stay inside log domain
            double right = 100.0 + random.nextDouble() * 100.0;
            double step = Math.max(1e-3, random.nextDouble()); // avoid zero step

            try {
                semaphore.beginWrite();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            task.update(function, left, right, step);
            semaphore.endWrite();
            System.out.printf("Source %.4f %.4f %.6f%n", left, right, step);

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
