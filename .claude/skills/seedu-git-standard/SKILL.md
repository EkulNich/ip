---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions (https://se-education.org/guides/conventions/git.html) to every commit message and branch name in this project — subject line format, body structure (what/why not how), and kebab-case branch naming. Use whenever drafting or creating a commit, or naming a new branch.
---

# SE-EDU Git Conventions

Source: [se-education.org/guides/conventions/git.html](https://se-education.org/guides/conventions/git.html)

This is mandatory for **every** commit made in this project going forward, and for any new branch name. Apply it whenever drafting a commit message or creating a branch, whether or not the user mentions "convention" or "format" — don't wait to be asked. This governs *how* a commit is written; it does not change the separate rule in `AGENTS.md` about *whether* to commit at all (never commit or push without being explicitly asked).

## Subject line

- Every commit needs a well-written subject line.
- ≤ 50 characters (hard limit 72) — many tools truncate or display only a limited width.
- **Imperative mood**: "Add README.md", not "Added README.md" or "Adding README.md".
- Capitalize the first letter: "Move index.html to root", not "move index.html to root".
- No trailing period: "Update sample data", not "Update sample data.".
- An optional scope/category prefix is fine when it adds clarity: `Person class: Remove static imports`, `bug fix: Add space after name`, `chore: Update release date`.

## Body

Non-trivial commits should have a body. Trivial ones (a genuinely self-explanatory one-line change) can skip it.

- Blank line between subject and body.
- Wrap body text at 72 characters.
- Blank line between paragraphs; use bullet points where they read more clearly than prose.
- **Explain WHAT and WHY, not HOW** — the diff already shows how; the body should let a reader judge the merit of the change without opening the diff.
- Don't repeat what's already said in this commit's code comments.
- If the explanation is getting long, that's often a sign the commit should be split into smaller ones instead.
- Avoid temporal words like "currently"/"originally" (redundant — the diff already implies before/after).
- "Let's ..." is a natural way to introduce the imperative description of what's being done.

Loose template (not every commit needs every part):

```
{current situation, present tense}

{why it needs to change}

{what is being done about it, imperative mood}

{why it is done that way}

{any other relevant info, e.g. links}
```

Example (bug fix):

```
Find command: make matching case-insensitive

Find command is case-sensitive.

A case-insensitive find is more user-friendly because users cannot be
expected to remember the exact case of the keywords.

Let's update the search algorithm to use case-insensitive matching.
```

## Branch names

- kebab-case, made of meaningful keywords describing the change: `refactor-ui-tests`.
- Issue-related branches: `issueNumber-some-keywords-from-issue-title`, e.g. `1234-ui-freeze-error`.

## Applying this

When asked to commit (only ever after explicit permission, per `AGENTS.md`):
1. Draft the subject line first — imperative, capitalized, no period, within 50/72 chars.
2. Add a body for anything beyond a trivial change, explaining what and why.
3. Don't pad a genuinely trivial commit with an unnecessary body just to have one.

When asked to create a branch, use kebab-case with meaningful keywords (and the `issueNumber-keywords` form if it's tied to a tracked issue).
