package lune;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lune.exception.LuneException;
import lune.task.Deadline;
import lune.task.Event;
import lune.task.Task;
import lune.task.TaskList;
import lune.task.Todo;

/**
 * Entry point and console loop for Lune, a CLI task-tracking chatbot.
 * Reads commands from stdin, dispatches them via processCommand(), and
 * persists the task list to disk after every successful change. Also
 * exposes getResponse(), an instance method the JavaFX GUI (lune.gui) uses
 * to reuse this exact same command logic outside the console loop.
 */
public class Lune {
    private static final String LINE =
            "    ____________________________________________________________\n";
    // Every line of a successful command's message (built in the handleX
    // methods below) is hand-indented by this many spaces, to line up under
    // LINE in the console; dedent() strips exactly this much back off.
    private static final int CONSOLE_INDENT_WIDTH = 5;
    private static final Path SAVE_FILE = Path.of("data", "lune.txt");
    // Accepted alongside plain "yyyy-mm-dd" (tried first, via LocalDate.parse):
    // a date with a time attached, e.g. "2/12/2019 1800" for 6pm on 2 Dec 2019.
    private static final DateTimeFormatter SLASH_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    private final TaskList tasks;
    private CommandType lastCommandType = CommandType.UNKNOWN;

    /**
     * The commands processCommand() can dispatch on. Enum constant names
     * double as the literal command word (lowercased via word()), so adding
     * a command word to check for is a one-line change instead of a new
     * string literal scattered across an if-else chain.
     *
     * Package-private (not private) so LuneTest can construct commands and
     * exercise fromInput() directly.
     */
    enum CommandType {
        LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, ON, FIND, UNKNOWN;

        /**
         * Returns this command's literal word as typed by the user, e.g.
         * "todo" for TODO.
         */
        String word() {
            return name().toLowerCase();
        }

        /**
         * Maps an input line's leading word to the matching CommandType,
         * or UNKNOWN if it doesn't match any recognized command (including
         * "bye", which is handled separately in main() rather than here).
         */
        static CommandType fromInput(String input) {
            String word = input.contains(" ") ? input.substring(0, input.indexOf(' ')) : input;
            return Arrays.stream(values())
                    .filter(type -> type != UNKNOWN && type.word().equals(word))
                    .findFirst()
                    .orElse(UNKNOWN);
        }
    }

    /**
     * Creates a new Lune, loading any previously-saved tasks from disk.
     */
    public Lune() {
        this.tasks = new TaskList(loadTasks());
    }

    /**
     * Prints the banner/greeting, loads any saved tasks, then reads and
     * executes commands from stdin until "bye" or input runs out.
     */
    public static void main(String[] args) {
        String banner = " _                     \n"
                + "| |   _   _ _ __   ___ \n"
                + "| |  | | | | '_ \\ / _ \\\n"
                + "| |__| |_| | | | |  __/\n"
                + "|_____\\__,_|_| |_|\\___|\n";
        System.out.println(banner);

        System.out.println(LINE + "     Hello! I'm Lune\n     What can I do for you?\n" + LINE);

        // TaskList grows as needed, so there's no fixed task limit to enforce.
        TaskList tasks = new TaskList(loadTasks());

        // Scanner is enough here since input is just read line-by-line;
        // no need for buffered/streamed reading at this stage.
        // hasNextLine() guards against input ending without "bye" (e.g. a
        // piped file, or Ctrl+D) — without it, nextLine() throws
        // NoSuchElementException once stdin is exhausted.
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                break;
            }
            try {
                String message = processCommand(input, tasks);
                System.out.println(LINE + message + LINE);
                saveTasks(tasks);
            } catch (LuneException e) {
                System.out.println(LINE + "     " + e.getMessage() + "\n" + LINE);
            }
        }
        System.out.println(LINE + "     Bye. Hope to see you again soon!\n" + LINE);
    }

    /**
     * Generates Lune's response to one line of user input, for the GUI to
     * display in a chat bubble. Reuses the exact same command logic as the
     * console loop in main() (processCommand()), but returns plain text
     * instead of the console's line-separator/indent-prefixed formatting.
     */
    public String getResponse(String input) {
        if (input.equals("bye")) {
            lastCommandType = CommandType.UNKNOWN;
            return "Bye. Hope to see you again soon!";
        }
        lastCommandType = CommandType.fromInput(input);
        try {
            String message = processCommand(input, tasks);
            saveTasks(tasks);
            return dedent(message);
        } catch (LuneException e) {
            return e.getMessage();
        }
    }

    /**
     * Returns the CommandType of the most recent command handled by
     * getResponse(), as its lowercase word (e.g. "todo", "mark"). Lets the
     * GUI style each reply bubble according to what kind of command
     * produced it.
     */
    public String getCommandType() {
        return lastCommandType.word();
    }

    /**
     * Executes one non-"bye" command and returns the resulting message
     * (formatted for the console: each line prefixed with indentation,
     * the way main() prints it). Throws LuneException, with a message
     * meant to be shown to the user as-is, for any command Lune can't
     * carry out.
     */
    private static String processCommand(String input, TaskList tasks) throws LuneException {
        switch (CommandType.fromInput(input)) {
            case LIST:
                return "     Here are the tasks in your list:\n" + formatNumbered(tasks, i -> true);
            case MARK: {
                int index = parseTaskIndex(input, CommandType.MARK, tasks.size());
                tasks.get(index).markAsDone();
                return "     Nice! I've marked this task as done:\n"
                        + "       " + tasks.get(index) + "\n";
            }
            case UNMARK: {
                int index = parseTaskIndex(input, CommandType.UNMARK, tasks.size());
                tasks.get(index).markAsUndone();
                return "     OK, I've marked this task as not done yet:\n"
                        + "       " + tasks.get(index) + "\n";
            }
            case DELETE: {
                int index = parseTaskIndex(input, CommandType.DELETE, tasks.size());
                Task removed = tasks.remove(index);
                return "     Noted. I've removed this task:\n"
                        + "       " + removed + "\n"
                        + "     Now you have " + tasks.size() + " tasks in the list.\n";
            }
            case TODO: {
                String description = input.startsWith("todo ") ? input.substring("todo ".length()).trim() : "";
                if (description.isEmpty()) {
                    throw new LuneException("Uh-oh, a todo needs a description — try: todo <what to do>");
                }
                tasks.add(new Todo(description));
                return formatAdded(tasks.get(tasks.size() - 1), tasks.size());
            }
            case DEADLINE: {
                String rest = input.startsWith("deadline ") ? input.substring("deadline ".length()) : "";
                int byIndex = rest.indexOf(" /by ");
                String description = (byIndex == -1 ? rest : rest.substring(0, byIndex)).trim();
                if (description.isEmpty()) {
                    throw new LuneException("Uh-oh, a deadline needs a description — "
                            + "try: deadline <what to do> /by <date>");
                }
                if (byIndex == -1) {
                    throw new LuneException("Uh-oh, a deadline needs a /by date — "
                            + "try: deadline " + description + " /by <date>");
                }
                String byText = rest.substring(byIndex + " /by ".length()).trim();
                if (byText.isEmpty()) {
                    throw new LuneException("Uh-oh, a deadline's /by date can't be empty.");
                }
                LocalDateTime by = parseDateTime("/by", byText);
                tasks.add(new Deadline(description, by));
                return formatAdded(tasks.get(tasks.size() - 1), tasks.size());
            }
            case EVENT: {
                String rest = input.startsWith("event ") ? input.substring("event ".length()) : "";
                int fromIndex = rest.indexOf(" /from ");
                int toIndex = rest.indexOf(" /to ");
                String description = (fromIndex == -1 ? rest : rest.substring(0, fromIndex)).trim();
                if (description.isEmpty()) {
                    throw new LuneException("Uh-oh, an event needs a description — "
                            + "try: event <what to do> /from <date> /to <date>");
                }
                if (fromIndex == -1) {
                    throw new LuneException("Uh-oh, an event needs a /from date — "
                            + "try: event " + description + " /from <date> /to <date>");
                }
                if (toIndex == -1 || toIndex < fromIndex) {
                    throw new LuneException("Uh-oh, an event needs a /to date after /from — "
                            + "try: event " + description + " /from <date> /to <date>");
                }
                String fromText = rest.substring(fromIndex + " /from ".length(), toIndex).trim();
                String toText = rest.substring(toIndex + " /to ".length()).trim();
                if (fromText.isEmpty() || toText.isEmpty()) {
                    throw new LuneException("Uh-oh, an event's /from and /to dates can't be empty.");
                }
                LocalDateTime from = parseDateTime("/from", fromText);
                LocalDateTime to = parseDateTime("/to", toText);
                tasks.add(new Event(description, from, to));
                return formatAdded(tasks.get(tasks.size() - 1), tasks.size());
            }
            case ON: {
                String text = input.equals("on") ? "" : input.substring("on ".length()).trim();
                if (text.isEmpty()) {
                    throw new LuneException("Uh-oh, tell me which date — try: on <date>");
                }
                LocalDate queryDate = parseDateTime("on", text).toLocalDate();
                return "     Here are the deadlines/events on " + Task.formatDate(queryDate) + ":\n"
                        + formatNumbered(tasks, i -> tasks.get(i).occursOn(queryDate));
            }
            case FIND: {
                String keyword = input.equals("find") ? "" : input.substring("find ".length()).trim();
                if (keyword.isEmpty()) {
                    throw new LuneException("Uh-oh, tell me what to search for — try: find <keyword>");
                }
                String lowerKeyword = keyword.toLowerCase();
                IntPredicate matchesKeyword = i -> tasks.get(i).getDescription().toLowerCase()
                        .contains(lowerKeyword);
                return "     Here are the matching tasks in your list:\n" + formatNumbered(tasks, matchesKeyword);
            }
            case UNKNOWN:
                // Fallthrough
            default:
                throw new LuneException("Uh-oh, I don't recognize that command — "
                        + "try todo, deadline, event, list, mark, unmark, delete, on, find, or bye.");
        }
    }

    /**
     * Parses and validates the task number argument of a mark/unmark/delete
     * command (e.g. "mark 2"), returning it as a 0-based index. Throws if
     * the argument is missing, isn't a number, or is out of range for the
     * current task count.
     */
    static int parseTaskIndex(String input, CommandType command, int taskCount) throws LuneException {
        String commandWord = command.word();
        String arg = input.equals(commandWord) ? "" : input.substring(commandWord.length() + 1).trim();
        if (arg.isEmpty()) {
            throw new LuneException("Uh-oh, which task number should I " + commandWord
                    + "? Try: " + commandWord + " 2");
        }
        int number;
        try {
            number = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new LuneException("Uh-oh, \"" + arg + "\" doesn't look like a task number.");
        }
        if (number < 1 || number > taskCount) {
            throw new LuneException("Uh-oh, task " + number + " doesn't exist — "
                    + "you currently have " + taskCount + " task(s).");
        }
        int index = number - 1;
        // Bad user input is already rejected above via LuneException; this
        // documents that the checks above are themselves correct, i.e. any
        // number that reaches here truly does convert to a valid 0-based
        // index — a bug here would be in this method's own logic, not the
        // user's command.
        assert index >= 0 && index < taskCount : "validated index must be within bounds";
        return index;
    }

    /**
     * Parses a user-supplied date/time, trying "yyyy-mm-dd" (as a start of
     * day) first, then "d/m/yyyy HHmm". label identifies which field this
     * is (e.g. "/by") for the error message if neither format matches.
     */
    static LocalDateTime parseDateTime(String label, String text) throws LuneException {
        try {
            return LocalDate.parse(text).atStartOfDay();
        } catch (DateTimeParseException isoFailure) {
            try {
                return LocalDateTime.parse(text, SLASH_DATE_TIME_FORMAT);
            } catch (DateTimeParseException slashFailure) {
                throw new LuneException("Uh-oh, \"" + text + "\" isn't a valid " + label
                        + " date/time — use yyyy-mm-dd (e.g. 2019-10-15) or d/m/yyyy HHmm (e.g. 2/12/2019 1800).");
            }
        }
    }

    /**
     * Renders every task whose (0-based) position in tasks satisfies
     * matches, each as "     &lt;n&gt;.&lt;task&gt;\n" numbered by that
     * original position — not by its position among the matches. Shared by
     * "list" (matches everything), "on" (matches by date), and "find"
     * (matches by keyword).
     */
    private static String formatNumbered(TaskList tasks, IntPredicate matches) {
        return IntStream.range(0, tasks.size())
                .filter(matches)
                .mapToObj(i -> "     " + (i + 1) + "." + tasks.get(i) + "\n")
                .collect(Collectors.joining());
    }

    private static String formatAdded(Task task, int taskCount) {
        return "     Got it. I've added this task:\n"
                + "       " + task + "\n"
                + "     Now you have " + taskCount + " tasks in the list.\n";
    }

    /**
     * Strips processCommand()'s console-only formatting (the fixed 5-space
     * indent every line is prefixed with, for lining up under LINE) so the
     * message reads cleanly as plain text in a GUI chat bubble. A line
     * that had extra indentation beyond the fixed 5 spaces (e.g. a task
     * shown under a confirmation message) keeps that extra indent.
     */
    private static String dedent(String message) {
        StringBuilder result = new StringBuilder();
        for (String line : message.split("\n", -1)) {
            // dedent() is only ever called on processCommand()'s successful
            // return value (getResponse() returns LuneException messages,
            // which have no such indent, unchanged) — every non-empty line
            // of that value is built with the fixed 5-space console indent,
            // so this should always hold. The substring branch below stays
            // as a safety net in case that construction invariant is ever
            // broken by a future edit.
            assert line.isEmpty() || line.length() >= 5 : "processCommand() success messages are always indented";
            result.append(line.length() >= 5 ? line.substring(5) : line).append("\n");
        }
        return result.toString().strip();
    }

    /**
     * Writes every task to SAVE_FILE, one per line, overwriting whatever was
     * there before. Called after every successful command so the file on
     * disk always reflects the current in-memory list.
     */
    private static void saveTasks(TaskList tasks) {
        // Each task contributes its own trailing "\n" (rather than joining
        // with "\n" as a separator) so an empty task list still produces an
        // empty string, matching the original loop's behavior exactly.
        String content = tasks.stream()
                .map(task -> task.toSaveFormat() + "\n")
                .collect(Collectors.joining());
        try {
            Files.createDirectories(SAVE_FILE.getParent());
            Files.writeString(SAVE_FILE, content);
        } catch (IOException e) {
            System.out.println(LINE + "     Uh-oh, I couldn't save your tasks to disk: "
                    + e.getMessage() + "\n" + LINE);
        }
    }

    /**
     * Reads SAVE_FILE (in the format written by saveTasks()) and rebuilds
     * the task list from it. Returns an empty list if the file doesn't
     * exist yet (e.g. first run). A line that can't be parsed (hand-edited
     * or corrupted) is skipped individually, with a warning, rather than
     * discarding every other task in the file.
     *
     * Known limitation: a task whose description/by/from/to itself contains
     * the literal " | " delimiter will not round-trip correctly (it'll be
     * skipped as malformed on the next load), since the save format doesn't
     * escape the delimiter. Not fixed here — full escaping is out of
     * proportion for what this file format needs to do.
     */
    private static ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(SAVE_FILE)) {
            return tasks;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(SAVE_FILE);
        } catch (IOException e) {
            System.out.println(LINE + "     Uh-oh, I couldn't read " + SAVE_FILE + " ("
                    + e.getMessage() + ") — starting with an empty list.\n" + LINE);
            return tasks;
        }
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseSavedTask(line));
            } catch (IllegalArgumentException e) {
                System.out.println(LINE + "     Uh-oh, skipping unreadable line " + (i + 1)
                        + " in " + SAVE_FILE + ": " + e.getMessage() + "\n" + LINE);
            }
        }
        return tasks;
    }

    /**
     * Parses one line of the on-disk save format (as written by
     * saveTasks()) into the matching Task. Throws IllegalArgumentException,
     * describing exactly what's wrong, for any line that doesn't have the
     * right number of fields, an unknown type letter, an invalid done
     * flag, an empty description, or an invalid date/time.
     */
    static Task parseSavedTask(String line) {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3) {
            throw new IllegalArgumentException("expected at least 3 fields (type | done | description), found "
                    + parts.length);
        }
        String type = parts[0];
        String doneFlag = parts[1];
        String description = parts[2];
        if (!doneFlag.equals("0") && !doneFlag.equals("1")) {
            throw new IllegalArgumentException("done flag must be \"0\" or \"1\", found \"" + doneFlag + "\"");
        }
        if (description.isBlank()) {
            throw new IllegalArgumentException("description can't be empty");
        }
        Task task;
        switch (type) {
            case "T":
                task = parseSavedTodo(parts, description);
                break;
            case "D":
                task = parseSavedDeadline(parts, description);
                break;
            case "E":
                task = parseSavedEvent(parts, description);
                break;
            default:
                throw new IllegalArgumentException("unknown task type \"" + type + "\"");
        }
        // Every case above either assigns task or throws, so the compiler
        // already treats task as definitely assigned here; this documents
        // that invariant explicitly, so it fails loudly if a future case is
        // ever added that forgets to do either.
        assert task != null : "every switch case above must assign task or throw";
        if (doneFlag.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Builds a Todo from a save-file line's fields (type "T"): exactly 3
     * fields, with no extra date fields.
     */
    private static Task parseSavedTodo(String[] parts, String description) {
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "a todo (T) line needs exactly 3 fields, found " + parts.length);
        }
        return new Todo(description);
    }

    /**
     * Builds a Deadline from a save-file line's fields (type "D"): exactly
     * 4 fields, with a non-empty /by date/time.
     */
    private static Task parseSavedDeadline(String[] parts, String description) {
        if (parts.length != 4 || parts[3].isBlank()) {
            throw new IllegalArgumentException(
                    "a deadline (D) line needs exactly 4 fields with a non-empty /by, found "
                            + parts.length);
        }
        return new Deadline(description, parseSavedDateTime(parts[3]));
    }

    /**
     * Builds an Event from a save-file line's fields (type "E"): exactly 5
     * fields, with non-empty /from and /to date/times.
     */
    private static Task parseSavedEvent(String[] parts, String description) {
        if (parts.length != 5 || parts[3].isBlank() || parts[4].isBlank()) {
            throw new IllegalArgumentException(
                    "an event (E) line needs exactly 5 fields with non-empty /from and /to, found "
                            + parts.length);
        }
        return new Event(description, parseSavedDateTime(parts[3]), parseSavedDateTime(parts[4]));
    }

    /**
     * Parses a date/time field from the on-disk save format (LocalDateTime's
     * own ISO string form), throwing IllegalArgumentException with a clear
     * message if it isn't valid.
     */
    static LocalDateTime parseSavedDateTime(String text) {
        try {
            return LocalDateTime.parse(text);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("\"" + text + "\" isn't a valid saved date/time");
        }
    }
}
