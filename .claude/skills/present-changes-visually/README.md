# Present Changes Visually

This directory packages the `present-changes-visually` project skill for Claude Code. The skill generates a self-contained, interactive HTML page that presents changed files as a GitHub-style side-by-side diff.

Adapted from [se-edu/skill-present-changes-visually](https://github.com/se-edu/skill-present-changes-visually).

## Use

Claude Code discovers this skill automatically from `SKILL.md` and can run it in response to requests to show, review, or compare changes visually.

To run the generator directly:

```bash
python3 .claude/skills/present-changes-visually/scripts/generate-split-view-diff.py \
  . HEAD WORKTREE _temp/visual-diff.html
```

The output is a single HTML file. The generator uses only Python's standard library.

## Repository layout

- `SKILL.md` — instructions for using the skill.
- `scripts/generate-split-view-diff.py` — the diff-page generator.
