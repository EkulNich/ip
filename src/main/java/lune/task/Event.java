package lune.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A task that starts at a specific date/time and ends at a specific date/time.
 */
public class Event extends Task {

    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Creates a new, not-done event spanning from/to (inclusive).
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Renders this event for display, e.g.
     * "[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)".
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + formatDateTime(from)
                + " to: " + formatDateTime(to) + ")";
    }

    /**
     * Renders this event as one line of the on-disk save format, e.g.
     * "E | 0 | project meeting | 2019-10-15T00:00 | 2019-10-16T00:00".
     */
    @Override
    public String toSaveFormat() {
        return "E | " + super.toSaveFormat() + " | " + from + " | " + to;
    }

    /**
     * Whether the given date falls within this event's from/to range,
     * inclusive on both ends (comparing dates only, ignoring time-of-day).
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from.toLocalDate()) && !date.isAfter(to.toLocalDate());
    }
}
