package threads;

import functions.Functions;

public class Integrator extends Thread {
    private final Task task;
    private final OnePlaceSemaphore semaphore;

    public Integrator(Task task, OnePlaceSemaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        for (int i = 0; i < task.getTasksCount(); i++) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            try {
                semaphore.beginRead();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            Task.TaskData data = task.snapshot();
            semaphore.endRead();
            if (data.function() == null) {
                // In theory should not happen, but guard from accidental misuse.
                i--;
                continue;
            }
            double result = Functions.integrate(
                    data.function(),
                    data.leftBorder(),
                    data.rightBorder(),
                    data.step()
            );
            System.out.printf(
                    "Result %.4f %.4f %.6f %.6f%n",
                    data.leftBorder(),
                    data.rightBorder(),
                    data.step(),
                    result
            );

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
