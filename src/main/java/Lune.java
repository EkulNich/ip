import java.util.ArrayList;
import java.util.Scanner;

public class Lune {
    private static final String LINE =
            "    ____________________________________________________________\n";

    public static void main(String[] args) {
        String banner = " _                     \n"
                + "| |   _   _ _ __   ___ \n"
                + "| |  | | | | '_ \\ / _ \\\n"
                + "| |__| |_| | | | |  __/\n"
                + "|_____\\__,_|_| |_|\\___|\n";
        System.out.println(banner);

        System.out.println(LINE + "     Hello! I'm Lune\n     What can I do for you?\n" + LINE);

        // ArrayList grows as needed, so there's no fixed task limit to enforce.
        ArrayList<Task> tasks = new ArrayList<>();

        // Scanner is enough here since input is just read line-by-line;
        // no need for buffered/streamed reading at this stage.
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                System.out.println(LINE + "     Bye. Hope to see you again soon!\n" + LINE);
                break;
            }
            try {
                processCommand(input, tasks);
            } catch (LuneException e) {
                System.out.println(LINE + "     " + e.getMessage() + "\n" + LINE);
            }
        }
    }

    /**
     * Executes one non-"bye" command. Throws LuneException, with a message
     * meant to be shown to the user as-is, for any command Lune can't
     * carry out.
     */
    private static void processCommand(String input, ArrayList<Task> tasks) throws LuneException {
        if (input.equals("list")) {
            StringBuilder listing = new StringBuilder("     Here are the tasks in your list:\n");
            for (int i = 0; i < tasks.size(); i++) {
                listing.append("     ").append(i + 1).append(".").append(tasks.get(i)).append("\n");
            }
            System.out.println(LINE + listing + LINE);
        } else if (input.equals("mark") || input.startsWith("mark ")) {
            int index = parseTaskIndex(input, "mark", tasks.size());
            tasks.get(index).markAsDone();
            System.out.println(LINE + "     Nice! I've marked this task as done:\n"
                    + "       " + tasks.get(index) + "\n" + LINE);
        } else if (input.equals("unmark") || input.startsWith("unmark ")) {
            int index = parseTaskIndex(input, "unmark", tasks.size());
            tasks.get(index).markAsUndone();
            System.out.println(LINE + "     OK, I've marked this task as not done yet:\n"
                    + "       " + tasks.get(index) + "\n" + LINE);
        } else if (input.equals("delete") || input.startsWith("delete ")) {
            int index = parseTaskIndex(input, "delete", tasks.size());
            Task removed = tasks.remove(index);
            System.out.println(LINE + "     Noted. I've removed this task:\n"
                    + "       " + removed + "\n"
                    + "     Now you have " + tasks.size() + " tasks in the list.\n" + LINE);
        } else if (input.equals("todo") || input.startsWith("todo ")) {
            String description = input.startsWith("todo ") ? input.substring("todo ".length()).trim() : "";
            if (description.isEmpty()) {
                throw new LuneException("Uh-oh, a todo needs a description — try: todo <what to do>");
            }
            tasks.add(new Todo(description));
            printAdded(tasks.get(tasks.size() - 1), tasks.size());
        } else if (input.equals("deadline") || input.startsWith("deadline ")) {
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
        } else if (input.equals("event") || input.startsWith("event ")) {
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
        } else {
            throw new LuneException("Uh-oh, I don't recognize that command — "
                    + "try todo, deadline, event, list, mark, unmark, delete, or bye.");
        }
    }

    private static int parseTaskIndex(String input, String commandWord, int taskCount) throws LuneException {
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
}
