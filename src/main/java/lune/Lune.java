package lune;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import lune.exception.LuneException;
import lune.task.Deadline;
import lune.task.Event;
import lune.task.Task;
import lune.task.TaskList;
import lune.task.Todo;

/**
 * Entry point and console loop for Lune, a CLI task-tracking chatbot.
 * Reads commands from stdin, dispatches them via processCommand(), and
 * persists the task list to disk after every successful change.
 */
public class Lune {
    private static final String LINE =
            "    ____________________________________________________________\n";
    private static final Path SAVE_FILE = Path.of("data", "lune.txt");
    // Accepted alongside plain "yyyy-mm-dd" (tried first, via LocalDate.parse):
    // a date with a time attached, e.g. "2/12/2019 1800" for 6pm on 2 Dec 2019.
    private static final DateTimeFormatter SLASH_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

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
            for (CommandType type : values()) {
                if (type != UNKNOWN && type.word().equals(word)) {
                    return type;
                }
            }
            return UNKNOWN;
        }
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
                processCommand(input, tasks);
                saveTasks(tasks);
            } catch (LuneException e) {
                System.out.println(LINE + "     " + e.getMessage() + "\n" + LINE);
            }
        }
        System.out.println(LINE + "     Bye. Hope to see you again soon!\n" + LINE);
    }

    /**
     * Executes one non-"bye" command. Throws LuneException, with a message
     * meant to be shown to the user as-is, for any command Lune can't
     * carry out.
     */
    private static void processCommand(String input, TaskList tasks) throws LuneException {
        switch (CommandType.fromInput(input)) {
            case LIST:
                StringBuilder listing = new StringBuilder("     Here are the tasks in your list:\n");
                for (int i = 0; i < tasks.size(); i++) {
                    listing.append("     ").append(i + 1).append(".").append(tasks.get(i)).append("\n");
                }
                System.out.println(LINE + listing + LINE);
                break;
            case MARK: {
                int index = parseTaskIndex(input, CommandType.MARK, tasks.size());
                tasks.get(index).markAsDone();
                System.out.println(LINE + "     Nice! I've marked this task as done:\n"
                        + "       " + tasks.get(index) + "\n" + LINE);
                break;
            }
            case UNMARK: {
                int index = parseTaskIndex(input, CommandType.UNMARK, tasks.size());
                tasks.get(index).markAsUndone();
                System.out.println(LINE + "     OK, I've marked this task as not done yet:\n"
                        + "       " + tasks.get(index) + "\n" + LINE);
                break;
            }
            case DELETE: {
                int index = parseTaskIndex(input, CommandType.DELETE, tasks.size());
                Task removed = tasks.remove(index);
                System.out.println(LINE + "     Noted. I've removed this task:\n"
                        + "       " + removed + "\n"
                        + "     Now you have " + tasks.size() + " tasks in the list.\n" + LINE);
                break;
            }
            case TODO: {
                String description = input.startsWith("todo ") ? input.substring("todo ".length()).trim() : "";
                if (description.isEmpty()) {
                    throw new LuneException("Uh-oh, a todo needs a description — try: todo <what to do>");
                }
                tasks.add(new Todo(description));
                printAdded(tasks.get(tasks.size() - 1), tasks.size());
                break;
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
                printAdded(tasks.get(tasks.size() - 1), tasks.size());
                break;
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
                printAdded(tasks.get(tasks.size() - 1), tasks.size());
                break;
            }
            case ON: {
                String text = input.equals("on") ? "" : input.substring("on ".length()).trim();
                if (text.isEmpty()) {
                    throw new LuneException("Uh-oh, tell me which date — try: on <date>");
                }
                LocalDate queryDate = parseDateTime("on", text).toLocalDate();
                StringBuilder onListing = new StringBuilder("     Here are the deadlines/events on "
                        + Task.formatDate(queryDate) + ":\n");
                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).occursOn(queryDate)) {
                        onListing.append("     ").append(i + 1).append(".").append(tasks.get(i)).append("\n");
                    }
                }
                System.out.println(LINE + onListing + LINE);
                break;
            }
            case FIND: {
                String keyword = input.equals("find") ? "" : input.substring("find ".length()).trim();
                if (keyword.isEmpty()) {
                    throw new LuneException("Uh-oh, tell me what to search for — try: find <keyword>");
                }
                StringBuilder findListing = new StringBuilder("     Here are the matching tasks in your list:\n");
                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                        findListing.append("     ").append(i + 1).append(".").append(tasks.get(i)).append("\n");
                    }
                }
                System.out.println(LINE + findListing + LINE);
                break;
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
        return number - 1;
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

    private static void printAdded(Task task, int taskCount) {
        System.out.println(LINE + "     Got it. I've added this task:\n"
                + "       " + task + "\n"
                + "     Now you have " + taskCount + " tasks in the list.\n" + LINE);
    }

    /**
     * Writes every task to SAVE_FILE, one per line, overwriting whatever was
     * there before. Called after every successful command so the file on
     * disk always reflects the current in-memory list.
     */
    private static void saveTasks(TaskList tasks) {
        StringBuilder content = new StringBuilder();
        for (Task task : tasks) {
            content.append(task.toSaveFormat()).append("\n");
        }
        try {
            Files.createDirectories(SAVE_FILE.getParent());
            Files.writeString(SAVE_FILE, content.toString());
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
                if (parts.length != 3) {
                    throw new IllegalArgumentException("a todo (T) line needs exactly 3 fields, found " + parts.length);
                }
                task = new Todo(description);
                break;
            case "D":
                if (parts.length != 4 || parts[3].isBlank()) {
                    throw new IllegalArgumentException(
                            "a deadline (D) line needs exactly 4 fields with a non-empty /by, found " + parts.length);
                }
                task = new Deadline(description, parseSavedDateTime(parts[3]));
                break;
            case "E":
                if (parts.length != 5 || parts[3].isBlank() || parts[4].isBlank()) {
                    throw new IllegalArgumentException(
                            "an event (E) line needs exactly 5 fields with non-empty /from and /to, found "
                                    + parts.length);
                }
                task = new Event(description, parseSavedDateTime(parts[3]), parseSavedDateTime(parts[4]));
                break;
            default:
                throw new IllegalArgumentException("unknown task type \"" + type + "\"");
        }
        if (doneFlag.equals("1")) {
            task.markAsDone();
        }
        return task;
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

