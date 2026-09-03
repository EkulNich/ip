package lune;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import lune.Lune.CommandType;
import lune.exception.LuneException;
import lune.task.Deadline;
import lune.task.Event;
import lune.task.Task;
import lune.task.Todo;

/**
 * Tests the highest-value logic in Lune: command dispatch (CommandType),
 * task-index validation, date/time parsing, and save-file line parsing.
 * These carry almost all the branching/edge-case handling in the program,
 * so they're prioritized over TaskList's one-line delegates or Todo's
 * two-line overrides. main()/processCommand() are intentionally left to
 * the console-level test-ui suite — they're mostly println-driven
 * orchestration rather than logic with a return value to assert on.
 */
public class LuneTest {

    // --- CommandType.fromInput ---

    @Test
    public void fromInput_bareCommandWord_matchingTypeReturned() {
        assertEquals(CommandType.LIST, CommandType.fromInput("list"));
        assertEquals(CommandType.TODO, CommandType.fromInput("todo"));
    }

    @Test
    public void fromInput_commandWordWithArguments_matchingTypeReturned() {
        assertEquals(CommandType.MARK, CommandType.fromInput("mark 2"));
        assertEquals(CommandType.DEADLINE, CommandType.fromInput("deadline return book /by 2019-10-15"));
    }

    @Test
    public void fromInput_unrecognizedWord_unknownReturned() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("blah"));
    }

    @Test
    public void fromInput_emptyInput_unknownReturned() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput(""));
    }

    @Test
    public void fromInput_wordThatIsCommandWordPlusExtraLetters_unknownReturned() {
        // "todolist" must not accidentally match TODO just because it starts
        // with "todo" — fromInput compares the whole leading word, not a prefix.
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("todolist"));
    }

    @Test
    public void fromInput_bye_unknownReturned() {
        // "bye" is deliberately not a CommandType constant - it's handled
        // separately in main()'s loop before processCommand() is ever called.
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("bye"));
    }

    // --- parseTaskIndex ---

    @Test
    public void parseTaskIndex_validNumberWithinRange_zeroBasedIndexReturned() throws LuneException {
        assertEquals(1, Lune.parseTaskIndex("mark 2", CommandType.MARK, 3));
    }

    @Test
    public void parseTaskIndex_lowerBoundaryOne_zeroReturned() throws LuneException {
        assertEquals(0, Lune.parseTaskIndex("mark 1", CommandType.MARK, 1));
    }

    @Test
    public void parseTaskIndex_upperBoundaryEqualsTaskCount_lastIndexReturned() throws LuneException {
        assertEquals(2, Lune.parseTaskIndex("mark 3", CommandType.MARK, 3));
    }

    @Test
    public void parseTaskIndex_noArgumentGiven_exceptionThrown() {
        assertThrows(LuneException.class, () -> Lune.parseTaskIndex("mark", CommandType.MARK, 3));
    }

    @Test
    public void parseTaskIndex_onlyWhitespaceArgument_exceptionThrown() {
        assertThrows(LuneException.class, () -> Lune.parseTaskIndex("mark   ", CommandType.MARK, 3));
    }

    @Test
    public void parseTaskIndex_nonNumericArgument_exceptionThrown() {
        assertThrows(LuneException.class, () -> Lune.parseTaskIndex("mark abc", CommandType.MARK, 3));
    }

    @Test
    public void parseTaskIndex_zero_exceptionThrown() {
        assertThrows(LuneException.class, () -> Lune.parseTaskIndex("mark 0", CommandType.MARK, 3));
    }

    @Test
    public void parseTaskIndex_pastTaskCount_exceptionThrown() {
        assertThrows(LuneException.class, () -> Lune.parseTaskIndex("mark 4", CommandType.MARK, 3));
    }

    @Test
    public void parseTaskIndex_emptyTaskList_exceptionThrown() {
        assertThrows(LuneException.class, () -> Lune.parseTaskIndex("mark 1", CommandType.MARK, 0));
    }

    // --- parseDateTime ---

    @Test
    public void parseDateTime_isoDate_parsedAsStartOfDay() throws LuneException {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), Lune.parseDateTime("/by", "2019-10-15"));
    }

    @Test
    public void parseDateTime_slashDateWithTime_parsedWithTime() throws LuneException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), Lune.parseDateTime("/by", "2/12/2019 1800"));
    }

    @Test
    public void parseDateTime_garbageText_exceptionThrown() {
        assertThrows(LuneException.class, () -> Lune.parseDateTime("/by", "not-a-date"));
    }

    @Test
    public void parseDateTime_invalidMonthInIsoFormat_exceptionThrown() {
        assertThrows(LuneException.class, () -> Lune.parseDateTime("/by", "2019-13-45"));
    }

    @Test
    public void parseDateTime_emptyText_exceptionThrown() {
        assertThrows(LuneException.class, () -> Lune.parseDateTime("/by", ""));
    }

    // --- parseSavedTask ---

    @Test
    public void parseSavedTask_validDoneTodo_todoReturnedWithDoneStatus() {
        Task task = Lune.parseSavedTask("T | 1 | read book");
        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getDescription());
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void parseSavedTask_validNotDoneTodo_todoReturnedWithNotDoneStatus() {
        Task task = Lune.parseSavedTask("T | 0 | read book");
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void parseSavedTask_validDeadline_deadlineReturnedWithCorrectDate() {
        Task task = Lune.parseSavedTask("D | 0 | return book | 2019-06-06T00:00");
        assertInstanceOf(Deadline.class, task);
        assertTrue(task.occursOn(LocalDate.of(2019, 6, 6)));
        assertFalse(task.occursOn(LocalDate.of(2019, 6, 7)));
    }

    @Test
    public void parseSavedTask_validEvent_eventReturnedWithCorrectRange() {
        Task task = Lune.parseSavedTask("E | 0 | project meeting | 2019-08-06T00:00 | 2019-08-08T00:00");
        assertInstanceOf(Event.class, task);
        assertTrue(task.occursOn(LocalDate.of(2019, 8, 7)));
        assertFalse(task.occursOn(LocalDate.of(2019, 8, 9)));
    }

    @Test
    public void parseSavedTask_tooFewFields_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedTask("this is garbage"));
    }

    @Test
    public void parseSavedTask_invalidDoneFlag_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedTask("T | 2 | read book"));
    }

    @Test
    public void parseSavedTask_emptyDescription_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedTask("T | 1 |  "));
    }

    @Test
    public void parseSavedTask_unknownTypeLetter_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedTask("X | 0 | read book"));
    }

    @Test
    public void parseSavedTask_todoWithExtraFields_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedTask("T | 0 | read book | extra"));
    }

    @Test
    public void parseSavedTask_deadlineMissingByField_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedTask("D | 0 | return book"));
    }

    @Test
    public void parseSavedTask_deadlineWithBlankBy_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedTask("D | 0 | return book | "));
    }

    @Test
    public void parseSavedTask_deadlineWithInvalidDate_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedTask("D | 0 | return book | not-a-date"));
    }

    @Test
    public void parseSavedTask_eventMissingToField_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () ->
                Lune.parseSavedTask("E | 0 | project meeting | 2019-08-06T00:00"));
    }

    @Test
    public void parseSavedTask_eventWithBlankTo_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () ->
                Lune.parseSavedTask("E | 0 | project meeting | 2019-08-06T00:00 | "));
    }

    // --- parseSavedDateTime ---

    @Test
    public void parseSavedDateTime_validIsoDateTime_parsedCorrectly() {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), Lune.parseSavedDateTime("2019-12-02T18:00"));
    }

    @Test
    public void parseSavedDateTime_garbageText_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedDateTime("not-a-date"));
    }

    @Test
    public void parseSavedDateTime_dateOnlyMissingTimePart_exceptionThrown() {
        // LocalDateTime.parse requires the "T<time>" part; a bare date isn't
        // enough, unlike parseDateTime()'s more lenient user-input parsing.
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedDateTime("2019-12-02"));
    }
}
