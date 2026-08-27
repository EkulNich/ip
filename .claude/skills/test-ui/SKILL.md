---
name: test-ui
description: Run the console UI test cases recorded in test/ui-test-plan.md against the program, checking actual output against expected output for each case. Use when asked to test, verify, or check the UI/console behavior, run UI/text tests, or regression-test the program's output after a change.
---

# Test UI

Run the test cases described in [`test/ui-test-plan.md`](../../../test/ui-test-plan.md) against the compiled program. Each test case runs the program fresh, feeds it a list of input commands, and checks the full console output against an expected transcript.

## Run the tests

1. Ensure Java 25 is active (`sdk use java 25.0.3.fx-zulu` per this project's `AGENTS.md`, if not already).
2. From the repository root, run:

   ```bash
   python3 .claude/skills/test-ui/scripts/run_ui_tests.py \
     --source-dir src/main/java --main-class Lune
   ```

   The script compiles every `.java` file in `--source-dir` into a temporary build directory, then runs each test case as its own `java` process, piping that case's input lines to stdin.

3. The script prints, for every test case, the console input, the console output produced, and a PASS/FAIL result — this is the test session record.
4. On the first failing test case, the script stops immediately (does not run later cases), prints that case's expected vs. actual output, and exits non-zero. Report that diff to the user rather than trying to explain it away.
5. If all cases pass, the script prints a final "All N test case(s) passed." summary.

## Test plan format

`test/ui-test-plan.md` holds one `## Test Case N: <name>` section per case, each with:

```markdown
## Test Case 1: <short name>

**Aim:** <what this case verifies>

**Input:**
```input
<command 1>
<command 2>
```

**Expected output:**
```expected
<exact expected console output, including the banner/greeting>
```
```

Expected output must match the program's stdout **exactly** (trailing newline differences are ignored, nothing else is). When the program's boilerplate (banner, greeting text) changes, every case's expected output needs updating — regenerate it by actually running the program with that case's input and capturing its real output, rather than hand-editing the expected block.

## Adding or updating test cases

When asked to add a test case, append a new `## Test Case N: <name>` section following the format above. Generate the `expected` block from a real run of the program (e.g. `printf '<input>\n' | java -cp <build-dir> Lune`) rather than guessing the output, then paste that exact text in — a hand-typed expected block is the most common source of false failures.
