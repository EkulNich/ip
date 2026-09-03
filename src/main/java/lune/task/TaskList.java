package lune.task;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Holds the current list of tasks and the operations to add, remove, and
 * access them. Wraps an ArrayList<Task> rather than exposing it directly,
 * so callers go through this class's operations instead of manipulating
 * the underlying list themselves.
 */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks;

    /**
     * Creates a new, empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list wrapping the given tasks (e.g. ones just loaded
     * from disk).
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * <p>Kept as a single-argument method rather than a varargs
     * {@code add(Task... tasks)}: every call site adds exactly one task at
     * a time, so varargs would add an array allocation per call with no
     * caller ever benefiting from passing more than one task at once.</p>
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given (0-based) index.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given (0-based) index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns how many tasks are in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an iterator over the tasks, in list order.
     */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
