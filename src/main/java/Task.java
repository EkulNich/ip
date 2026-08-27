/**
 * Represents a task with a description and a done/not-done status.
 * Subclasses ({@link Todo}, {@link Deadline}, {@link Event}) add the
 * type-specific date/time information and prepend their type tag.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
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
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Renders this task as one line of the on-disk save format, e.g.
     * "1 | read book". Subclasses prepend their type letter and append
     * their own fields.
     */
    public String toSaveFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }
}
