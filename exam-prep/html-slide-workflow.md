# HTML Slide Workflow

This document captures the current exam-prep workflow for turning per-slide markdown files into clean HTML presentation slides.

Q1 (`1. Tre-lagsarkitektur og Transaction Script`) is the reference implementation.

## Folder Structure

- Canonical source: one markdown file per slide inside a question folder, for example `1.0.md` to `1.6.md`
- HTML output: one `html/` subfolder inside the same question folder
- Shared theme: `exam-prep/html-shared/slides.css`
- Assets: keep diagrams and images in the question `resources/` folder

Example:

```text
exam-prep/
  html-shared/slides.css
  1. Tre-lagsarkitektur og Transaction Script/
    1.0.md
    1.1.md
    ...
    html/
      slide-1.html
      slide-2.html
      ...
    resources/
```

## Markdown Rules

Markdown is the canonical authoring format.

Each slide markdown file should contain:
- the visible slide content first
- a `Talepunkter` section in Danish for presenter guidance
- existing previous/next navigation links

The markdown visible content should be sparse and slide-like:
- keywords over full explanations
- short prompts over textbook prose
- diagram/image references when useful
- on `Hvilket problem løser det?` slides, prefer cue words, contrasts, or short labels instead of theory bullets

`Talepunkter` should help the speaker explain the content out loud:
- concise Danish speaking prompts
- oral-exam framing
- no long manuscript paragraphs unless truly necessary

Recommended pattern:

```md
## Visible slide heading

- Visible cue
- Visible cue

## Talepunkter

- What to explain orally
- What to connect to the project
- What follow-up question may come
```

## HTML Rules

HTML slides are examiner-facing and should stay visually clean.

HTML should render:
- titles
- cue words
- diagrams
- simple structured content

HTML should not render:
- `Talepunkter`
- `Speaker Notes`
- explicit presenter-only labels
- long theory paragraphs by default
- theory-style bullet explanations on `Hvilket problem løser det?` slides

Important principle:
- the slide should support the explanation, not replace it

If the markdown contains both visible slide content and `Talepunkter`, only the visible content should appear in HTML.

## Visual Principles

Use the Q1 deck as the default visual reference.

Current design rules:
- cue-based slides
- no hardcoded theory answers
- clean examiner-facing wording
- layout should suggest structure and hierarchy
- use cards, spacing, and grouping instead of dense text
- keep visible content short enough to talk from

For oral exam slides:
- prefer keywords and contrasts
- avoid “Talepunkt”, “Speaker Notes”, or similar visible helper labels in HTML
- if a slide becomes too explanatory, reduce it to cues
- especially on `Hvilket problem løser det?` slides, show the problem as prompts or contrasts, not as mini theory notes

## Suggested Conversion Flow

When converting a new question:

1. Clean the markdown slide content first.
2. Add Danish `Talepunkter` to the markdown only.
3. For `Hvilket problem løser det?` slides, reduce visible content to cue-level prompts before building HTML.
4. Build one HTML file per slide in the local `html/` subfolder.
5. Link each HTML file to `../../html-shared/slides.css`.
6. Keep diagrams/resources referenced from the question folder.
7. Check that the HTML contains only visible presentation content.

## Pre-flight Checklist

Before writing or approving HTML for a question folder, run this checklist:

- Use Q1 as the reference deck, not memory and not older question folders
- Confirm the markdown is still the canonical source
- Check that visible markdown content is sparse, cue-based, and slide-like
- Check that `Talepunkter` stay in markdown only and do not appear in HTML
- Check that `Hvilket problem løser det?` slides use prompts, contrasts, or cue words rather than mini theory explanations
- Check that HTML mirrors only the visible markdown content and does not add presenter paraphrases
- Check that diagram captions stay short and examiner-facing
- Check that the slide supports speaking rather than replacing speaking

Recommended micro-sequence:

1. Clean markdown visible content
2. Review visible content only
3. Re-read this workflow
4. Compare with Q1
5. Build HTML
6. Verify that no presenter-only content leaked into HTML

## Applying This To Later Questions

Later question folders should be normalized toward the Q1 pattern.

That means:
- reduce theory-heavy markdown into visible cues plus `Talepunkter`
- keep `Hvilket problem løser det?` slides especially lean and non-explanatory
- keep HTML minimal
- reuse the shared theme first
- only add per-question styling overrides if there is a clear presentation need

Question 2 currently still reflects the older rough template style, so it should be simplified before HTML conversion rather than copied directly.
