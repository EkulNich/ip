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

**Aim:** Verify `deadline ... /by ...` adds a task tagged `[D]` with its `(by: ...)` suffix.

**Input:**
```input
deadline return book /by Sunday
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
       [D][ ] return book (by: Sunday)
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[D][ ] return book (by: Sunday)
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 4: Add an event and list it

**Aim:** Verify `event ... /from ... /to ...` adds a task tagged `[E]` with its `(from: ... to: ...)` suffix.

**Input:**
```input
event project meeting /from Mon 2pm /to 4pm
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
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
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
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
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
       [D][ ] return book (by: June 6th)
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
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
     2.[D][ ] return book (by: June 6th)
     3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
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
     Uh-oh, a deadline needs a description — try: deadline <what to do> /by <when>
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, an event needs a description — try: event <what to do> /from <start> /to <end>
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
deadline return book /by Sunday
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
     Uh-oh, a deadline needs a /by date or time — try: deadline return book /by <when>
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: Sunday)
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Sunday)
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 9: A malformed event does not consume a task slot

**Aim:** Verify an event missing '/to' is rejected and does not get numbered as a task, so the next successful add still gets task number 1.

**Input:**
```input
event meeting /from Mon
event meeting /from Mon /to 2pm
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
     Uh-oh, an event needs a /to date or time after /from — try: event meeting /from <start> /to <end>
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] meeting (from: Mon to: 2pm)
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[E][ ] meeting (from: Mon to: 2pm)
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
     Uh-oh, I don't recognize that command — try todo, deadline, event, list, mark, unmark, delete, or bye.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Uh-oh, I don't recognize that command — try todo, deadline, event, list, mark, unmark, delete, or bye.
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

**Aim:** Verify that adding, marking, and deleting tasks each write the current task list to `data/lune.txt` in the pipe-separated save format, and that a deleted task is actually removed from the file (not just left as a stale line).

**Input:**
```input
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th /to 2-4pm
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
       [D][ ] return book (by: June 6th)
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Aug 6th to: 2-4pm)
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
       [D][ ] return book (by: June 6th)
     Now you have 3 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

**Expected file (`data/lune.txt`):**
```file:data/lune.txt
T | 1 | read book
E | 0 | project meeting | Aug 6th | 2-4pm
T | 1 | join sports club
```

## Test Case 16: Tasks are loaded from an existing save file on startup

**Aim:** Verify that on startup, an existing `data/lune.txt` (in the format `saveTasks()` writes) is correctly parsed back into todos/deadlines/events with the right done status, and that subsequent commands operate correctly on the loaded tasks.

**Given file:**
```given-file:data/lune.txt
T | 1 | read book
D | 0 | return book | June 6th
E | 0 | project meeting | Aug 6th | 2-4pm
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
     2.[D][ ] return book (by: June 6th)
     3.[E][ ] project meeting (from: Aug 6th to: 2-4pm)
     4.[T][X] join sports club
    ____________________________________________________________

    ____________________________________________________________
     OK, I've marked this task as not done yet:
       [T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: June 6th)
     3.[E][ ] project meeting (from: Aug 6th to: 2-4pm)
     4.[T][X] join sports club
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 17: A corrupted save file doesn't crash startup

**Aim:** Verify a save file that doesn't match the expected format is handled gracefully — the program reports a load problem instead of crashing, and continues with an empty task list rather than a half-populated or inconsistent one.

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
     Uh-oh, I couldn't load your saved tasks (Index 1 out of bounds for length 1) — starting with an empty list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```
