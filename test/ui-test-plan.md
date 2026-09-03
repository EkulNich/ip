# UI Test Plan

Test cases for `Lune`. Each test case is run as its own fresh `java Lune`
process; the **Input** lines are piped to stdin one per line, and the
program's full console output (stdout) is compared against **Expected
output** verbatim. Run with the `test-ui` skill.

## Test Case 1: Greet and exit

**Aim:** Verify the startup banner/greeting print, and that `bye` exits with the farewell message.

**Input:**
```input
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 2: Add a todo and list it

**Aim:** Verify `todo` adds a task tagged `[T]` and `list` displays it.

**Input:**
```input
todo read book
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 3: Add a deadline and list it

**Aim:** Verify `deadline ... /by ...` adds a task tagged `[D]`, parsing the `yyyy-mm-dd` date (also accepts `d/m/yyyy HHmm` with a time) and displaying it as `MMM dd yyyy`.

**Input:**
```input
deadline return book /by 2019-10-15
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: Oct 15 2019)
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[D][ ] return book (by: Oct 15 2019)
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 4: Add an event and list it

**Aim:** Verify `event ... /from ... /to ...` adds a task tagged `[E]`, parsing both dates (`yyyy-mm-dd` or `d/m/yyyy HHmm`) and displaying them as `MMM dd yyyy`.

**Input:**
```input
event project meeting /from 2019-10-16 /to 2019-10-18
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Oct 16 2019 to: Oct 18 2019)
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[E][ ] project meeting (from: Oct 16 2019 to: Oct 18 2019)
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 5: Mark and unmark a task

**Aim:** Verify `mark` and `unmark` toggle a task's done status, reflected in both their confirmation message and later `list` output.

**Input:**
```input
todo read book
mark 1
list
unmark 1
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     OK, I've marked this task as not done yet:
       [T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 6: Mixed task types end to end

**Aim:** Verify todos, deadlines, and events can be mixed in one session, marked done, and listed together with correct numbering and running task counts.

**Input:**
```input
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-08
todo join sports club
mark 1
mark 4
todo borrow book
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: Jun 06 2019)
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Aug 06 2019 to: Aug 08 2019)
     Now you have 3 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] join sports club
     Now you have 4 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] join sports club
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] borrow book
     Now you have 5 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][ ] return book (by: Jun 06 2019)
     3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 08 2019)
     4.[T][X] join sports club
     5.[T][ ] borrow book
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 7: Consecutive errors leave the task list untouched

**Aim:** Verify that back-to-back invalid commands (empty todo/deadline/event) are each rejected without adding a phantom task, leaving the list exactly as it was before them.

**Input:**
```input
todo read book
todo
deadline
event
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, a todo needs a description — try: todo <what to do>
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, a deadline needs a description — try: deadline <what to do> /by <date>
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, an event needs a description — try: event <what to do> /from <date> /to <date>
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 8: A malformed deadline does not consume a task slot

**Aim:** Verify a deadline missing '/by' is rejected and does not get numbered as a task, so the next successful add still gets the correct index.

**Input:**
```input
todo read book
deadline return book
deadline return book /by 2019-06-06
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, a deadline needs a /by date — try: deadline return book /by <date>
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: Jun 06 2019)
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Jun 06 2019)
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 9: A malformed event does not consume a task slot

**Aim:** Verify an event missing '/to' is rejected and does not get numbered as a task, so the next successful add still gets task number 1.

**Input:**
```input
event meeting /from 2019-06-06
event meeting /from 2019-06-06 /to 2019-06-08
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, an event needs a /to date after /from — try: event meeting /from <date> /to <date>
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] meeting (from: Jun 06 2019 to: Jun 08 2019)
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[E][ ] meeting (from: Jun 06 2019 to: Jun 08 2019)
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 10: mark/unmark errors never change any task's done status or count

**Aim:** Verify marking on an empty list, task number 0, an out-of-range number, and a non-numeric argument are all rejected without side effects, and a valid mark afterward still works correctly.

**Input:**
```input
mark 1
todo read book
mark 0
mark 5
mark abc
mark 1
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, task 1 doesn't exist — you currently have 0 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, task 0 doesn't exist — you currently have 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, task 5 doesn't exist — you currently have 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, "abc" doesn't look like a task number.
    ____________________________________________________________

    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 11: Unknown commands interleaved with valid ones don't disturb the list

**Aim:** Verify unrecognized commands are rejected without altering existing tasks or task numbering, interleaved between valid adds.

**Input:**
```input
todo read book
blah
list
foobar
todo return book
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, I don't recognize that command — try todo, deadline, event, list, mark, unmark, delete, on, find, or bye.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, I don't recognize that command — try todo, deadline, event, list, mark, unmark, delete, on, find, or bye.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] return book
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[T][ ] return book
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 12: The task list grows past the old fixed-array size

**Aim:** Verify tasks use an `ArrayList` with no fixed capacity (per the `A-Collections` extension) by adding well beyond the previous 100-task array limit, then confirm `delete` still correctly shrinks the list and updates what indices are valid.

**Input:**
```input
todo task1
todo task2
todo task3
todo task4
todo task5
todo task6
todo task7
todo task8
todo task9
todo task10
todo task11
todo task12
todo task13
todo task14
todo task15
todo task16
todo task17
todo task18
todo task19
todo task20
todo task21
todo task22
todo task23
todo task24
todo task25
todo task26
todo task27
todo task28
todo task29
todo task30
todo task31
todo task32
todo task33
todo task34
todo task35
todo task36
todo task37
todo task38
todo task39
todo task40
todo task41
todo task42
todo task43
todo task44
todo task45
todo task46
todo task47
todo task48
todo task49
todo task50
todo task51
todo task52
todo task53
todo task54
todo task55
todo task56
todo task57
todo task58
todo task59
todo task60
todo task61
todo task62
todo task63
todo task64
todo task65
todo task66
todo task67
todo task68
todo task69
todo task70
todo task71
todo task72
todo task73
todo task74
todo task75
todo task76
todo task77
todo task78
todo task79
todo task80
todo task81
todo task82
todo task83
todo task84
todo task85
todo task86
todo task87
todo task88
todo task89
todo task90
todo task91
todo task92
todo task93
todo task94
todo task95
todo task96
todo task97
todo task98
todo task99
todo task100
todo task101
todo task102
todo task103
todo task104
todo task105
mark 105
delete 105
mark 105
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task1
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task2
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task3
     Now you have 3 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task4
     Now you have 4 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task5
     Now you have 5 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task6
     Now you have 6 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task7
     Now you have 7 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task8
     Now you have 8 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task9
     Now you have 9 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task10
     Now you have 10 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task11
     Now you have 11 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task12
     Now you have 12 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task13
     Now you have 13 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task14
     Now you have 14 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task15
     Now you have 15 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task16
     Now you have 16 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task17
     Now you have 17 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task18
     Now you have 18 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task19
     Now you have 19 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task20
     Now you have 20 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task21
     Now you have 21 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task22
     Now you have 22 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task23
     Now you have 23 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task24
     Now you have 24 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task25
     Now you have 25 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task26
     Now you have 26 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task27
     Now you have 27 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task28
     Now you have 28 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task29
     Now you have 29 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task30
     Now you have 30 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task31
     Now you have 31 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task32
     Now you have 32 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task33
     Now you have 33 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task34
     Now you have 34 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task35
     Now you have 35 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task36
     Now you have 36 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task37
     Now you have 37 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task38
     Now you have 38 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task39
     Now you have 39 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task40
     Now you have 40 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task41
     Now you have 41 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task42
     Now you have 42 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task43
     Now you have 43 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task44
     Now you have 44 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task45
     Now you have 45 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task46
     Now you have 46 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task47
     Now you have 47 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task48
     Now you have 48 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task49
     Now you have 49 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task50
     Now you have 50 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task51
     Now you have 51 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task52
     Now you have 52 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task53
     Now you have 53 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task54
     Now you have 54 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task55
     Now you have 55 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task56
     Now you have 56 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task57
     Now you have 57 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task58
     Now you have 58 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task59
     Now you have 59 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task60
     Now you have 60 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task61
     Now you have 61 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task62
     Now you have 62 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task63
     Now you have 63 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task64
     Now you have 64 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task65
     Now you have 65 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task66
     Now you have 66 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task67
     Now you have 67 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task68
     Now you have 68 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task69
     Now you have 69 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task70
     Now you have 70 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task71
     Now you have 71 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task72
     Now you have 72 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task73
     Now you have 73 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task74
     Now you have 74 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task75
     Now you have 75 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task76
     Now you have 76 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task77
     Now you have 77 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task78
     Now you have 78 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task79
     Now you have 79 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task80
     Now you have 80 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task81
     Now you have 81 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task82
     Now you have 82 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task83
     Now you have 83 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task84
     Now you have 84 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task85
     Now you have 85 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task86
     Now you have 86 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task87
     Now you have 87 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task88
     Now you have 88 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task89
     Now you have 89 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task90
     Now you have 90 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task91
     Now you have 91 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task92
     Now you have 92 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task93
     Now you have 93 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task94
     Now you have 94 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task95
     Now you have 95 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task96
     Now you have 96 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task97
     Now you have 97 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task98
     Now you have 98 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task99
     Now you have 99 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task100
     Now you have 100 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task101
     Now you have 101 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task102
     Now you have 102 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task103
     Now you have 103 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task104
     Now you have 104 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] task105
     Now you have 105 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] task105
    ____________________________________________________________

    ____________________________________________________________
     Noted. I've removed this task:
       [T][X] task105
     Now you have 104 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, task 105 doesn't exist — you currently have 104 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 13: unmark errors never change any task's done status or count

**Aim:** Verify unmarking on an empty list, task number 0, an out-of-range number, and a non-numeric argument are all rejected without side effects, and a valid unmark afterward still works correctly. (Added after this exact class of bug — `unmark` skipping its bounds check — was injected and slipped past every other test case undetected, since only `mark` errors were previously covered.)

**Input:**
```input
unmark 1
todo read book
mark 1
unmark 0
unmark 5
unmark abc
unmark 1
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, task 1 doesn't exist — you currently have 0 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, task 0 doesn't exist — you currently have 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, task 5 doesn't exist — you currently have 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, "abc" doesn't look like a task number.
    ____________________________________________________________

    ____________________________________________________________
     OK, I've marked this task as not done yet:
       [T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 14: delete errors never corrupt the list, and deleting reindexes correctly

**Aim:** Verify deleting on an empty list, task number 0, an out-of-range number, and a non-numeric argument are all rejected without side effects; verify a successful delete correctly renumbers the remaining tasks (not just leaves a gap); and verify `list` on an empty list still prints cleanly.

**Input:**
```input
delete 1
todo read book
todo return book
delete 0
delete 5
delete abc
delete 1
list
delete 1
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, task 1 doesn't exist — you currently have 0 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] return book
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, task 0 doesn't exist — you currently have 2 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, task 5 doesn't exist — you currently have 2 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, "abc" doesn't look like a task number.
    ____________________________________________________________

    ____________________________________________________________
     Noted. I've removed this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] return book
    ____________________________________________________________

    ____________________________________________________________
     Noted. I've removed this task:
       [T][ ] return book
     Now you have 0 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 15: Tasks are saved to disk after every change

**Aim:** Verify that adding, marking, and deleting tasks each write the current task list to `data/lune.txt` in the pipe-separated save format (dates/times stored via `LocalDateTime`'s ISO format), and that a deleted task is actually removed from the file (not just left as a stale line).

**Input:**
```input
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-08
todo join sports club
mark 1
mark 4
delete 2
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: Jun 06 2019)
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Aug 06 2019 to: Aug 08 2019)
     Now you have 3 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] join sports club
     Now you have 4 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] join sports club
    ____________________________________________________________

    ____________________________________________________________
     Noted. I've removed this task:
       [D][ ] return book (by: Jun 06 2019)
     Now you have 3 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

**Expected file (`data/lune.txt`):**
```file:data/lune.txt
T | 1 | read book
E | 0 | project meeting | 2019-08-06T00:00 | 2019-08-08T00:00
T | 1 | join sports club
```

## Test Case 16: Tasks are loaded from an existing save file on startup

**Aim:** Verify that on startup, an existing `data/lune.txt` (in the format `saveTasks()` writes) is correctly parsed back into todos/deadlines/events — including parsing saved date/times back into `LocalDateTime` and displaying them as `MMM dd yyyy` — with the right done status, and that subsequent commands operate correctly on the loaded tasks.

**Given file:**
```given-file:data/lune.txt
T | 1 | read book
D | 0 | return book | 2019-06-06T00:00
E | 0 | project meeting | 2019-08-06T00:00 | 2019-08-08T00:00
T | 1 | join sports club
```

**Input:**
```input
list
unmark 1
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][ ] return book (by: Jun 06 2019)
     3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 08 2019)
     4.[T][X] join sports club
    ____________________________________________________________

    ____________________________________________________________
     OK, I've marked this task as not done yet:
       [T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Jun 06 2019)
     3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 08 2019)
     4.[T][X] join sports club
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 17: A corrupted save file doesn't crash startup

**Aim:** Verify a save file that doesn't match the expected format is handled gracefully — the program reports the specific unreadable line instead of crashing, and continues (here, with an empty list since the only line present is bad).

**Given file:**
```given-file:data/lune.txt
this is not a valid line at all
```

**Input:**
```input
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, skipping unreadable line 1 in data/lune.txt: expected at least 3 fields (type | done | description), found 1
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 18: Individually corrupted save-file lines are skipped, not the whole file

**Aim:** Verify that when a save file has a mix of valid and invalid lines, only the invalid ones are skipped (each reported with a specific reason and line number), while every valid line still loads correctly — a single bad line must not wipe out the rest of a person's saved tasks.

**Given file:**
```given-file:data/lune.txt
T | 1 | read book
this is garbage
D | 0 | return book | 2019-06-06T00:00
X | 0 | unknown type
E | 0 | project meeting | 2019-08-06T00:00
T | 2 | bad done flag

T | 1 | join sports club
```

**Input:**
```input
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, skipping unreadable line 2 in data/lune.txt: expected at least 3 fields (type | done | description), found 1
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, skipping unreadable line 4 in data/lune.txt: unknown task type "X"
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, skipping unreadable line 5 in data/lune.txt: an event (E) line needs exactly 5 fields with non-empty /from and /to, found 4
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, skipping unreadable line 6 in data/lune.txt: done flag must be "0" or "1", found "2"
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][ ] return book (by: Jun 06 2019)
     3.[T][X] join sports club
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 19: Input ending without "bye" exits gracefully instead of crashing

**Aim:** Verify that stdin running out without an explicit `bye` (e.g. a piped file with no trailing `bye`, or Ctrl+D) exits cleanly with the normal farewell message, rather than crashing with an uncaught `NoSuchElementException` from `Scanner.nextLine()`.

**Input:**
```input
todo read book
list
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 20: Invalid dates are rejected when adding a deadline/event

**Aim:** Verify `deadline`/`event` reject a `/by`, `/from`, or `/to` value that matches neither accepted format (`yyyy-mm-dd` or `d/m/yyyy HHmm`) — including a syntactically-plausible-but-invalid one like month 13 — without adding a task or corrupting the list, and that a valid date afterward still works.

**Input:**
```input
deadline pay rent /by not-a-date
event trip /from 2019-13-45 /to 2019-10-20
event trip /from 2019-10-20 /to nonsense
deadline pay rent /by 2019-10-20
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, "not-a-date" isn't a valid /by date/time — use yyyy-mm-dd (e.g. 2019-10-15) or d/m/yyyy HHmm (e.g. 2/12/2019 1800).
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, "2019-13-45" isn't a valid /from date/time — use yyyy-mm-dd (e.g. 2019-10-15) or d/m/yyyy HHmm (e.g. 2/12/2019 1800).
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, "nonsense" isn't a valid /to date/time — use yyyy-mm-dd (e.g. 2019-10-15) or d/m/yyyy HHmm (e.g. 2/12/2019 1800).
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] pay rent (by: Oct 20 2019)
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[D][ ] pay rent (by: Oct 20 2019)
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 21: An invalid date in the save file is skipped like any other corrupted line

**Aim:** Verify a save-file line whose date/time isn't valid (e.g. hand-edited) is skipped individually with a clear reason, consistent with how other corrupted lines are handled, rather than crashing or discarding the rest of the file.

**Given file:**
```given-file:data/lune.txt
T | 1 | read book
D | 0 | return book | not-a-date
T | 1 | join sports club
```

**Input:**
```input
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, skipping unreadable line 2 in data/lune.txt: "not-a-date" isn't a valid saved date/time
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[T][X] join sports club
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 22: deadline/event accept d/m/yyyy HHmm as well as yyyy-mm-dd

**Aim:** Verify `/by`, `/from`, and `/to` accept a date with a time attached in `d/m/yyyy HHmm` form (e.g. `2/12/2019 1800` for 6pm on 2 Dec 2019), storing and displaying the time alongside the date rather than treating the whole thing as an opaque string.

**Input:**
```input
deadline return book /by 2/12/2019 1800
event team meeting /from 2/12/2019 0900 /to 2/12/2019 1030
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: Dec 02 2019, 6:00 pm)
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] team meeting (from: Dec 02 2019, 9:00 am to: Dec 02 2019, 10:30 am)
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[D][ ] return book (by: Dec 02 2019, 6:00 pm)
     2.[E][ ] team meeting (from: Dec 02 2019, 9:00 am to: Dec 02 2019, 10:30 am)
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 23: "on <date>" lists deadlines/events occurring on that date

**Aim:** Verify `on <date>` (the date/time stretch goal) lists only deadlines/events associated with the given date — a deadline matches by exact date, an event matches if the date falls within its from/to range inclusive — excluding todos entirely, preserving each task's real list-position number (not a renumbered filtered index), and showing an empty (but non-crashing) result when nothing matches.

**Input:**
```input
todo pack bags
deadline pay rent /by 2019-12-02
event conference /from 2019-12-01 /to 2019-12-05
deadline submit report /by 2019-12-10
on 2019-12-02
on 2019-12-11
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] pack bags
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] pay rent (by: Dec 02 2019)
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] conference (from: Dec 01 2019 to: Dec 05 2019)
     Now you have 3 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] submit report (by: Dec 10 2019)
     Now you have 4 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the deadlines/events on Dec 02 2019:
     2.[D][ ] pay rent (by: Dec 02 2019)
     3.[E][ ] conference (from: Dec 01 2019 to: Dec 05 2019)
    ____________________________________________________________

    ____________________________________________________________
     Here are the deadlines/events on Dec 11 2019:
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 24: "on" rejects a missing or invalid date

**Aim:** Verify `on` with no argument and `on` with an unparseable date are both rejected with a clear message rather than crashing, and that `on` still works normally afterward.

**Input:**
```input
on
on not-a-date
todo pack bags
on 2019-12-02
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, tell me which date — try: on <date>
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, "not-a-date" isn't a valid on date/time — use yyyy-mm-dd (e.g. 2019-10-15) or d/m/yyyy HHmm (e.g. 2/12/2019 1800).
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] pack bags
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the deadlines/events on Dec 02 2019:
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 25: "find <keyword>" locates tasks by description

**Aim:** Verify `find` lists only tasks whose description contains the keyword (case-insensitively), preserving each task's real list-position number, excluding non-matching tasks, matching the requirement's own example (this exercises the exact scenario given: two matching tasks of different types both shown, "buy milk" excluded), showing an empty result when nothing matches, and rejecting a missing keyword.

**Input:**
```input
todo read book
mark 1
deadline return book /by 2019-06-06
mark 2
todo buy milk
find book
find BOOK
find nothing
find
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Hello! I'm Lune
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: Jun 06 2019)
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Nice! I've marked this task as done:
       [D][X] return book (by: Jun 06 2019)
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] buy milk
     Now you have 3 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the matching tasks in your list:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
    ____________________________________________________________

    ____________________________________________________________
     Here are the matching tasks in your list:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
    ____________________________________________________________

    ____________________________________________________________
     Here are the matching tasks in your list:
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, tell me what to search for — try: find <keyword>
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```
