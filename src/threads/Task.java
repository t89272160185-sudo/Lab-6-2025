package threads;

import functions.Function;

/**
 * Shared holder for integration parameters between generator and integrator threads.
 */
public class Task {
    private volatile Function function;
    private volatile double leftBorder;
    private volatile double rightBorder;
    private volatile double step;
    private int tasksCount;
    private volatile long version;
    private boolean ready;

    public Task(int tasksCount) {
        setTasksCount(tasksCount);
    }

    public synchronized void update(Function function, double leftBorder, double rightBorder, double step) {
        updateRaw(function, leftBorder, rightBorder, step);
    }

    public synchronized void produce(Function function, double leftBorder, double rightBorder, double step) throws InterruptedException {
        while (ready) {
            wait();
        }
        updateRaw(function, leftBorder, rightBorder, step);
        ready = true;
        notifyAll();
    }

    public synchronized TaskData consume() throws InterruptedException {
        while (!ready) {
            wait();
        }
        TaskData data = new TaskData(function, leftBorder, rightBorder, step, version);
        ready = false;
        notifyAll();
        return data;
    }

    private void updateRaw(Function function, double leftBorder, double rightBorder, double step) {
        this.function = function;
        this.leftBorder = leftBorder;
        this.rightBorder = rightBorder;
        this.step = step;
        version++;
    }

    public synchronized TaskData snapshot() {
        return new TaskData(function, leftBorder, rightBorder, step, version);
    }

    public Function getFunction() {
        return function;
    }

    public double getLeftBorder() {
        return leftBorder;
    }

    public double getRightBorder() {
        return rightBorder;
    }

    public double getStep() {
        return step;
    }

    public int getTasksCount() {
        return tasksCount;
    }

    public void setTasksCount(int tasksCount) {
        if (tasksCount < 1) {
            throw new IllegalArgumentException("tasksCount must be positive");
        }
        this.tasksCount = tasksCount;
    }

    public long getVersion() {
        return version;
    }

    public record TaskData(Function function, double leftBorder, double rightBorder, double step, long version) {
    }
}
