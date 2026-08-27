import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task with a description and a done/not-done status.
 * Subclasses ({@link Todo}, {@link Deadline}, {@link Event}) add the
 * type-specific date/time information and prepend their type tag.
 */
public class Task {
    /** Display format for a bare date, e.g. "Oct 15 2019". */
    protected static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    /** Display format for a date with a time attached, e.g. "Oct 15 2019, 6:00 PM". */
    protected static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

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

    /**
     * Whether this task is associated with the given date. Todos never
     * are; Deadline/Event override this with their own date(s). Used by
     * the "on <date>" command.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Formats a date/time for display: just the date (e.g. "Oct 15 2019")
     * if no time-of-day was given (midnight), or date and time together
     * (e.g. "Oct 15 2019, 6:00 PM") otherwise. A task created from a plain
     * "yyyy-mm-dd" input has no way to distinguish "no time given" from
     * "genuinely means midnight" — this treats midnight as "no time given",
     * which matches every realistic use of this chatbot.
     */
    protected static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(DISPLAY_DATE_FORMAT);
        }
        return dateTime.format(DISPLAY_DATE_TIME_FORMAT);
    }
}
