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

    public Task(int tasksCount) {
        setTasksCount(tasksCount);
    }

    public synchronized void update(Function function, double leftBorder, double rightBorder, double step) {
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
