---
name: test-ui
description: Run the console UI test cases recorded in test/ui-test-plan.md against the program, checking actual output against expected output for each case. Use when asked to test, verify, or check the UI/console behavior, run UI/text tests, or regression-test the program's output after a change.
---

# Test UI

Run the test cases described in [`test/ui-test-plan.md`](../../../test/ui-test-plan.md) against the compiled program. Each test case runs the program fresh in its own throwaway working directory, optionally seeded with pre-existing files (e.g. a save file to load on startup), feeds it a list of input commands, and checks the full console output — and optionally, the content of any files the program wrote — against expectations.

## Run the tests

1. Ensure Java 25 is active (`sdk use java 25.0.3.fx-zulu` per this project's `AGENTS.md`, if not already).
2. From the repository root, run:

   ```bash
   python3 .claude/skills/test-ui/scripts/run_ui_tests.py \
     --source-dir src/main/java --main-class Lune
   ```

   The script compiles every `.java` file in `--source-dir` into a temporary build directory, then runs each test case as its own `java` process (with a fresh temporary directory as its working directory, so file writes never touch the real project), piping that case's input lines to stdin.

3. The script prints, for every test case, the console input, the console output produced, and a PASS/FAIL result — this is the test session record.
4. On the first failing test case, the script stops immediately (does not run later cases), prints that case's expected vs. actual output, and exits non-zero. Report that diff to the user rather than trying to explain it away.
5. If all cases pass, the script prints a final "All N test case(s) passed." summary.

## Test plan format

`test/ui-test-plan.md` holds one `## Test Case N: <name>` section per case, each with:

```markdown
## Test Case 1: <short name>

**Aim:** <what this case verifies>

**Given file (optional, repeatable):**
```given-file:data/lune.txt
<content to write to that file, relative to the test's working directory, before the program starts>
```

**Input:**
```input
<command 1>
<command 2>
```

**Expected output:**
```expected
<exact expected console output, including the banner/greeting>
```

**Expected file (optional, repeatable):**
```file:data/lune.txt
<exact expected content of that file, relative to the test's working directory, after the input runs>
```
```

Expected output/file content must match **exactly** (trailing newline differences are ignored, nothing else is). When the program's boilerplate (banner, greeting text) or save-file format changes, every affected case needs updating — regenerate expected blocks by actually running the program with that case's input and capturing its real output/file, rather than hand-editing them.

`file:<relative-path>` and `given-file:<relative-path>` blocks are both optional and can each appear more than once per case (one per file). Use `given-file:` to seed a starting file (e.g. testing that the program correctly loads an existing save file on startup); use `file:` to assert what a file looks like after the case runs. Omit either entirely when a case doesn't need them.

## Adding or updating test cases

When asked to add a test case, append a new `## Test Case N: <name>` section following the format above. Generate the `expected` block (and any `file:` blocks) from a real run of the program — e.g. `mkdir -p /tmp/scratch && cd /tmp/scratch && printf '<input>\n' | java -cp <build-dir> Lune`, then `cat /tmp/scratch/data/lune.txt` for the file content — rather than guessing the output, then paste that exact text in. A hand-typed expected block is the most common source of false failures. Run from a scratch directory (not the real project root) so you don't overwrite the real `data/lune.txt` while generating a case. For a case that needs a `given-file:`, write that seed file into the scratch directory yourself before running the program, so the captured output/file reflects the real loading behavior rather than a guess.
