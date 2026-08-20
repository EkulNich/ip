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

        // Fixed-size array is enough per the spec (no more than 100 tasks,
        // no need to persist to disk at this stage).
        Task[] tasks = new Task[100];
        int taskCount = 0;

        // Scanner is enough here since input is just read line-by-line;
        // no need for buffered/streamed reading at this stage.
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                System.out.println(LINE + "     Bye. Hope to see you again soon!\n" + LINE);
                break;
            } else if (input.equals("list")) {
                StringBuilder listing = new StringBuilder("     Here are the tasks in your list:\n");
                for (int i = 0; i < taskCount; i++) {
                    listing.append("     ").append(i + 1).append(".").append(tasks[i]).append("\n");
                }
                System.out.println(LINE + listing + LINE);
            } else if (input.startsWith("mark ")) {
                int index = Integer.parseInt(input.substring("mark ".length()).trim()) - 1;
                tasks[index].markAsDone();
                System.out.println(LINE + "     Nice! I've marked this task as done:\n"
                        + "       " + tasks[index] + "\n" + LINE);
            } else if (input.startsWith("unmark ")) {
                int index = Integer.parseInt(input.substring("unmark ".length()).trim()) - 1;
                tasks[index].markAsUndone();
                System.out.println(LINE + "     OK, I've marked this task as not done yet:\n"
                        + "       " + tasks[index] + "\n" + LINE);
            } else {
                tasks[taskCount] = new Task(input);
                taskCount++;
                System.out.println(LINE + "     added: " + input + "\n" + LINE);
            }
        }
    }
}
