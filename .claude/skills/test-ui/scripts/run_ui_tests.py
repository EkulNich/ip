#!/usr/bin/env python3
"""
Runs the UI test cases described in a test plan (default: test/ui-test-plan.md)
against a compiled Java console program, one fresh `java` process per test case.

Each test case in the plan supplies:
  - an aim (what the case verifies)
  - an "input" fenced block: lines fed to the program's stdin, one per line
  - an "expected" fenced block: the exact stdout the program must produce

Usage (from the repository root):
    python3 .claude/skills/test-ui/scripts/run_ui_tests.py \\
        --source-dir src/main/java --main-class Lune

Exits 0 if every test case passes, printing the full console session for each
case along the way. On the first failing test case, stops immediately and
prints the actual vs. expected output for that case, then exits 1.

Only the standard library is used.
"""
from __future__ import annotations

import argparse
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

CASE_HEADER_RE = re.compile(r"^##\s*Test Case\s*\d+\s*:\s*(.+?)\s*$", re.MULTILINE)
AIM_RE = re.compile(r"\*\*Aim:\*\*\s*(.+)")
FENCE_RE = re.compile(r"```(\w+)\n(.*?)\n```", re.DOTALL)


class TestCase:
    def __init__(self, number: int, name: str, aim: str, input_text: str, expected: str):
        self.number = number
        self.name = name
        self.aim = aim
        self.input_text = input_text
        self.expected = expected


def parse_plan(plan_path: Path) -> list[TestCase]:
    text = plan_path.read_text()
    headers = list(CASE_HEADER_RE.finditer(text))
    if not headers:
        sys.exit(f"No '## Test Case N: <name>' sections found in {plan_path}")

    cases = []
    for i, header in enumerate(headers):
        start = header.end()
        end = headers[i + 1].start() if i + 1 < len(headers) else len(text)
        body = text[start:end]

        aim_match = AIM_RE.search(body)
        if not aim_match:
            sys.exit(f"Test Case {i + 1} ({header.group(1)}) is missing an **Aim:** line")
        aim = aim_match.group(1).strip()

        fences = {tag: content for tag, content in FENCE_RE.findall(body)}
        if "input" not in fences:
            sys.exit(f"Test Case {i + 1} ({header.group(1)}) is missing an ```input``` block")
        if "expected" not in fences:
            sys.exit(f"Test Case {i + 1} ({header.group(1)}) is missing an ```expected``` block")

        cases.append(TestCase(i + 1, header.group(1).strip(), aim, fences["input"], fences["expected"]))
    return cases


def compile_sources(source_dir: Path, build_dir: Path) -> None:
    build_dir.mkdir(parents=True, exist_ok=True)
    java_files = sorted(str(p) for p in source_dir.glob("*.java"))
    if not java_files:
        sys.exit(f"No .java files found in {source_dir}")
    result = subprocess.run(
        ["javac", "-d", str(build_dir), *java_files],
        capture_output=True, text=True,
    )
    if result.returncode != 0:
        sys.exit(f"Compilation failed:\n{result.stdout}{result.stderr}")


def run_program(build_dir: Path, main_class: str, input_text: str) -> str:
    result = subprocess.run(
        ["java", "-cp", str(build_dir), main_class],
        input=input_text, capture_output=True, text=True, timeout=10,
    )
    # Combine stdout and stderr so a crash (stack trace) shows up as output
    # a test case can catch, rather than being silently dropped.
    return result.stdout + result.stderr


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", default="test/ui-test-plan.md", type=Path)
    parser.add_argument("--source-dir", default="src/main/java", type=Path)
    parser.add_argument("--main-class", default="Lune")
    args = parser.parse_args()

    cases = parse_plan(args.plan)

    with tempfile.TemporaryDirectory(prefix="test-ui-build-") as tmp:
        build_dir = Path(tmp)
        compile_sources(args.source_dir, build_dir)

        for case in cases:
            print(f"\n{'=' * 70}")
            print(f"Test Case {case.number}: {case.name}")
            print(f"Aim: {case.aim}")
            print(f"{'-' * 70}")
            print("Console input:")
            print(case.input_text)
            print(f"{'-' * 70}")
            print("Console output:")
            actual = run_program(build_dir, args.main_class, case.input_text)
            print(actual, end="" if actual.endswith("\n") else "\n")
            print(f"{'-' * 70}")

            if actual.rstrip("\n") == case.expected.rstrip("\n"):
                print(f"Result: PASS")
            else:
                print(f"Result: FAIL")
                print(f"\n{'=' * 70}")
                print(f"Test Case {case.number} ({case.name}) FAILED — stopping test session.")
                print(f"\n--- Expected output ---\n{case.expected}")
                print(f"\n--- Actual output ---\n{actual}")
                sys.exit(1)

    print(f"\n{'=' * 70}")
    print(f"All {len(cases)} test case(s) passed.")


if __name__ == "__main__":
    main()
