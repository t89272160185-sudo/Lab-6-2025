package threads;

import functions.Functions;

public class SimpleIntegrator implements Runnable {
    private final Task task;

    public SimpleIntegrator(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        for (int i = 0; i < task.getTasksCount(); i++) {
            Task.TaskData data = awaitNextData();
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
        }
    }

    private Task.TaskData awaitNextData() {
        try {
            return task.consume();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
