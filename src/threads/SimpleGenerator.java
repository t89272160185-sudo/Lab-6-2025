package threads;

import functions.Function;
import functions.basic.Log;

import java.util.Random;

public class SimpleGenerator implements Runnable {
    private final Task task;
    private final Random random = new Random();

    public SimpleGenerator(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        for (int i = 0; i < task.getTasksCount(); i++) {
            double base = 1.0 + random.nextDouble() * 9.0;
            Function function = new Log(base);
            double left = Math.max(1e-3, random.nextDouble() * 100.0); // clamp to stay inside log domain
            double right = 100.0 + random.nextDouble() * 100.0;
            double step = Math.max(1e-3, random.nextDouble()); // avoid zero step

            // Single synchronized block keeps parameters consistent.
            try {
                task.produce(function, left, right, step);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            System.out.printf("Source %.4f %.4f %.6f%n", left, right, step);
        }
    }
}
