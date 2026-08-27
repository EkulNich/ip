import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Lune {
    private static final String LINE =
            "    ____________________________________________________________\n";
    private static final Path SAVE_FILE = Path.of("data", "lune.txt");

    /**
     * The commands processCommand() can dispatch on. Enum constant names
     * double as the literal command word (lowercased via word()), so adding
     * a command word to check for is a one-line change instead of a new
     * string literal scattered across an if-else chain.
     */
    private enum CommandType {
        LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, UNKNOWN;

        String word() {
            return name().toLowerCase();
        }

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

    public static void main(String[] args) {
        String banner = " _                     \n"
                + "| |   _   _ _ __   ___ \n"
                + "| |  | | | | '_ \\ / _ \\\n"
                + "| |__| |_| | | | |  __/\n"
                + "|_____\\__,_|_| |_|\\___|\n";
        System.out.println(banner);

        System.out.println(LINE + "     Hello! I'm Lune\n     What can I do for you?\n" + LINE);

        // ArrayList grows as needed, so there's no fixed task limit to enforce.
        ArrayList<Task> tasks = loadTasks();

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
    private static void processCommand(String input, ArrayList<Task> tasks) throws LuneException {
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
                        + "try: deadline <what to do> /by <when>");
            }
            if (byIndex == -1) {
                throw new LuneException("Uh-oh, a deadline needs a /by date or time — "
                        + "try: deadline " + description + " /by <when>");
            }
            String by = rest.substring(byIndex + " /by ".length()).trim();
            if (by.isEmpty()) {
                throw new LuneException("Uh-oh, a deadline's /by date or time can't be empty.");
            }
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
                        + "try: event <what to do> /from <start> /to <end>");
            }
            if (fromIndex == -1) {
                throw new LuneException("Uh-oh, an event needs a /from date or time — "
                        + "try: event " + description + " /from <start> /to <end>");
            }
            if (toIndex == -1 || toIndex < fromIndex) {
                throw new LuneException("Uh-oh, an event needs a /to date or time after /from — "
                        + "try: event " + description + " /from <start> /to <end>");
            }
            String from = rest.substring(fromIndex + " /from ".length(), toIndex).trim();
            String to = rest.substring(toIndex + " /to ".length()).trim();
            if (from.isEmpty() || to.isEmpty()) {
                throw new LuneException("Uh-oh, an event's /from and /to date or time can't be empty.");
            }
            tasks.add(new Event(description, from, to));
            printAdded(tasks.get(tasks.size() - 1), tasks.size());
            break;
        }
        case UNKNOWN:
        default:
            throw new LuneException("Uh-oh, I don't recognize that command — "
                    + "try todo, deadline, event, list, mark, unmark, delete, or bye.");
        }
    }

    private static int parseTaskIndex(String input, CommandType command, int taskCount) throws LuneException {
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
    private static void saveTasks(ArrayList<Task> tasks) {
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

    private static Task parseSavedTask(String line) {
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
            task = new Deadline(description, parts[3]);
            break;
        case "E":
            if (parts.length != 5 || parts[3].isBlank() || parts[4].isBlank()) {
                throw new IllegalArgumentException(
                        "an event (E) line needs exactly 5 fields with non-empty /from and /to, found "
                                + parts.length);
            }
            task = new Event(description, parts[3], parts[4]);
            break;
        default:
            throw new IllegalArgumentException("unknown task type \"" + type + "\"");
        }
        if (doneFlag.equals("1")) {
            task.markAsDone();
        }
        return task;
    }
}
