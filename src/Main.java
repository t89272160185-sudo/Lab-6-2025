import functions.Functions;
import functions.basic.Exp;
import functions.basic.Log;
import threads.Generator;
import threads.Integrator;
import threads.OnePlaceSemaphore;
import threads.SimpleGenerator;
import threads.SimpleIntegrator;
import threads.Task;

import java.util.Random;

public class Main {
    private static final int DEFAULT_TASKS_COUNT = 120;

    public static void main(String[] args) {
        System.out.println("=== Task 1: numerical integration check ===");
        integrationCheck();

        System.out.println("\n=== Task 2: sequential workflow (nonThread) ===");
        nonThread();

        System.out.println("\n=== Task 3: simple threads with synchronization ===");
        simpleThreads();

        System.out.println("\n=== Task 4: semaphore-based threads with interruption ===");
        complicatedThreads();
    }

    private static void integrationCheck() {
        Exp exp = new Exp();
        double approx = Functions.integrate(exp, 0.0, 1.0, 0.01);
        double theoretical = Math.E - 1.0;
        System.out.printf("exp(x) integral on [0;1] with step 0.01: %.8f (theoretical %.8f)%n", approx, theoretical);

        double step = findStepForAccuracy(exp, 0.0, 1.0, theoretical, 1e-7);
        double precise = Functions.integrate(exp, 0.0, 1.0, step);
        System.out.printf("Step to reach 1e-7 precision: %.8g, result %.8f%n", step, precise);
    }

    private static double findStepForAccuracy(Exp function, double left, double right, double target, double tolerance) {
        double step = 0.5;
        for (int i = 0; i < 30; i++) {
            double value = Functions.integrate(function, left, right, step);
            if (Math.abs(value - target) < tolerance) {
                return step;
            }
            step *= 0.5;
        }
        return step;
    }

    public static void nonThread() {
        Task task = new Task(DEFAULT_TASKS_COUNT);
        Random random = new Random();
        for (int i = 0; i < task.getTasksCount(); i++) {
            double base = 1.0 + random.nextDouble() * 9.0;
            Log log = new Log(base);
            double left = Math.max(1e-3, random.nextDouble() * 100.0); // clamp to stay inside log domain
            double right = 100.0 + random.nextDouble() * 100.0;
            double step = Math.max(1e-3, random.nextDouble()); // avoid zero step

            task.update(log, left, right, step);
            System.out.printf("Source %.4f %.4f %.6f%n", left, right, step);
            double result = Functions.integrate(task.getFunction(), task.getLeftBorder(), task.getRightBorder(), task.getStep());
            System.out.printf("Result %.4f %.4f %.6f %.6f%n", left, right, step, result);
        }
    }

    public static void simpleThreads() {
        Task task = new Task(DEFAULT_TASKS_COUNT);
        Thread generator = new Thread(new SimpleGenerator(task), "SimpleGenerator");
        Thread integrator = new Thread(new SimpleIntegrator(task), "SimpleIntegrator");
        generator.setPriority(Thread.NORM_PRIORITY + 1);
        integrator.setPriority(Thread.NORM_PRIORITY);
        generator.start();
        integrator.start();
        try {
            generator.join();
            integrator.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void complicatedThreads() {
        Task task = new Task(DEFAULT_TASKS_COUNT);
        OnePlaceSemaphore semaphore = new OnePlaceSemaphore();
        Generator generator = new Generator(task, semaphore);
        Integrator integrator = new Integrator(task, semaphore);
        generator.start();
        integrator.start();

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        generator.interrupt();
        integrator.interrupt();

        try {
            generator.join();
            integrator.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
