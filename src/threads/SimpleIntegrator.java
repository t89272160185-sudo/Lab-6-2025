package threads;

import functions.Functions;

public class SimpleIntegrator implements Runnable {
    private final Task task;

    public SimpleIntegrator(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        long lastVersion = -1;
        for (int i = 0; i < task.getTasksCount(); i++) {
            Task.TaskData data = awaitNextData(lastVersion);
            if (data == null) {
                return;
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
            lastVersion = data.version();
        }
    }

    private Task.TaskData awaitNextData(long lastVersion) {
        while (true) {
            Task.TaskData data = task.snapshot();
            if (data.function() != null && data.version() != lastVersion) {
                return data;
            }
            if (Thread.currentThread().isInterrupted()) {
                return null;
            }
            Thread.yield();
        }
    }
}
