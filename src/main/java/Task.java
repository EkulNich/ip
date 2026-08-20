/**
 * Represents a task with a description and a done/not-done status.
 * A task can optionally carry a deadline ("by") or an event window
 * ("from"/"to"); which fields apply is determined by which constructor
 * was used to create the task.
 */
public class Task {
    protected String description;
    protected boolean isDone;
    protected String type;
    protected String by;
    protected String from;
    protected String to;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
        this.type = "T";
    }

    public Task(String description, String by) {
        this(description);
        this.type = "D";
        this.by = by;
    }

    public Task(String description, String from, String to) {
        this(description);
        this.type = "E";
        this.from = from;
        this.to = to;
    }

    public String getDescription() {
        return description;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    public void markAsDone() {
        isDone = true;
    }

    public void markAsUndone() {
        isDone = false;
    }

    @Override
    public String toString() {
        String base = "[" + type + "][" + getStatusIcon() + "] " + description;
        if (type.equals("D")) {
            return base + " (by: " + by + ")";
        } else if (type.equals("E")) {
            return base + " (from: " + from + " to: " + to + ")";
        }
        return base;
    }
}
