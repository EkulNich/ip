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

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task remove(int index) {
        return tasks.remove(index);
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
