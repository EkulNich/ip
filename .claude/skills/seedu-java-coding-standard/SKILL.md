---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard (https://se-education.org/guides/conventions/java/intermediate.html) to all Java code in this project — naming, layout, statements, and comments/Javadoc conventions. Use whenever writing, editing, or reviewing any .java file, not only when asked about style.
---

# SE-EDU Java Coding Standard

Source: [se-education.org/guides/conventions/java/intermediate.html](https://se-education.org/guides/conventions/java/intermediate.html)

This is mandatory for **all** Java source in this project (`src/main/java`, `src/test/java`) — apply it by default whenever writing new code or editing existing code, whether or not the user mentions "style" or "convention". When reviewing or refactoring existing code, fix violations you notice along the way rather than leaving them.

## Naming

- **Packages**: all lowercase, no underscores (`lune.task`).
- **Classes/enums**: PascalCase nouns (`TaskList`, `Deadline`).
- **Variables**: camelCase (`taskList`, `dueDate`).
- **Constants** (`static final`): `UPPER_SNAKE_CASE`; related constants share a common prefix (`COLOR_RED`, `COLOR_GREEN`, `COLOR_BLUE`).
- **Methods**: camelCase verbs (`getName()`, `computeTotal()`).
- **Test methods**: `featureUnderTest_testScenario_expectedBehavior()` (e.g. `occursOn_dateBeforeRange_falseReturned()`).
- **Abbreviations**: not all-caps as part of a name — `exportHtmlSource()`, not `exportHTMLSource()`.
- **Language**: English only.
- **Scope vs. length**: short names (`i`, `j`, `k` for ints; `c`, `d` for chars) only for small-scope loop counters/iterators; everything else gets a descriptive name proportional to its scope.
- **Booleans**: read as booleans — `isSet`, `hasData`, `boolean canEvaluate()`; a boolean setter's parameter mirrors the getter name, e.g. `void setFound(boolean isFound)`.
- **Collections**: plural names (`List<Task> tasks`, `int[] values`).

## Layout

- 4-space indentation, never tabs.
- Line length ≤ 120 chars (soft limit 110 — prefer wrapping before that if it reads cleanly).
- Wrapped continuation lines: 8-space indent (double the normal 4).
- K&R/Egyptian brace style — opening brace on the same line, `} else {` / `} catch (...) {` on one line, closing brace starts its own line aligned with the block's opener.
- **Always brace loop and conditional bodies**, even a single statement — no bare `if (x) return;`.
- Whitespace: spaces around binary operators (`a = (b + c) * d;`), a space after reserved words before `(` (`while (true) {`, not `while(true){`), a space after every comma.
- One blank line between logical units within a block (not between every statement — group related lines, separate distinct steps).
- `switch`: every `case` without its own `break` (including intentional fallthrough into the next case, or an empty case falling through to `default`) needs an explicit `// Fallthrough` comment. Prefer arrow-style `switch` (`case ABC -> ...`) for new code where it fits, since it has no fallthrough footgun at all.

## Statements

- Every class lives in a package — no default/unnamed package.
- **Import order** (each present group separated from the next, no blank-line requirement within a group): static imports → `java.*` → `javax.*` → third-party (`org.*`, `com.*`, etc.) → this project's own packages (`lune.*`) last.
- Always explicit imports — never `import java.util.*;`.
- Array brackets attach to the type, not the variable: `int[] a`, not `int a[]`.
- Declare and initialize variables at their point of first use, in the smallest enclosing scope that works.
- No public instance/class fields except constants (`static final`) or a pure data class with no behavior — use `private`/`protected` plus accessors otherwise.
- Loop bodies: always `{ }`, regardless of body length.
- Conditionals: the condition goes on its own line; the body is always `{ }`.

## Comments / Javadoc

- English, American spelling.
- **Every public class and public method needs a header Javadoc comment.** Optional (but still welcome) for: getters/setters, method overrides whose parent Javadoc already applies exactly, and test classes/methods.
- Format:
  ```java
  /**
   * Returns lateral location of the specified position.
   * If the position is unset, NaN is returned.
   *
   * @param x X coordinate of position.
   * @param y Y coordinate of position.
   * @return Lateral location.
   * @throws IllegalArgumentException If zone is <= 0.
   */
  public double computeLocation(double x, double y) throws IllegalArgumentException {
  ```
  - `/**` alone on its own opening line; a space after every `*` on continuation lines.
  - First sentence is a short summary. For methods, it starts with a third-person verb — `Returns`, `Creates`, `Parses`, `Whether` is **not** a verb; write `Returns whether ...` instead.
  - A blank `*` line between the description and the `@param`/`@return`/`@throws` block, if any.
  - Each `@param`/`@return`/`@throws` description ends with a period.
  - `@param`/`@return` can be omitted when the method is simple enough that they'd add nothing beyond the summary — don't force boilerplate tags onto an obvious one-liner.
  - No blank line between the closing `*/` and the class/method it documents.
- Inline/trailing comments are indented to match their position in the code.

## Applying this to a change

1. Before finishing any edit to a `.java` file, check it against the sections above — especially import order, brace/whitespace style, and Javadoc on anything public.
2. When adding a new public class or method, write its Javadoc as part of writing the method, not as an afterthought.
3. If you spot an existing violation in code you're already touching for another reason, fix it in the same change rather than leaving it for later.
