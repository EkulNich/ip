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

        // Scanner is enough here since input is just read line-by-line;
        // no need for buffered/streamed reading at this stage.
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                System.out.println(LINE + "     Bye. Hope to see you again soon!\n" + LINE);
                break;
            }
            System.out.println(LINE + "     " + input + "\n" + LINE);
        }
    }
}
