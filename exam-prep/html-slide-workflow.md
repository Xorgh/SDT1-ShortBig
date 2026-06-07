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

## Suggested Conversion Flow

When converting a new question:

1. Clean the markdown slide content first.
2. Add Danish `Talepunkter` to the markdown only.
3. Build one HTML file per slide in the local `html/` subfolder.
4. Link each HTML file to `../../html-shared/slides.css`.
5. Keep diagrams/resources referenced from the question folder.
6. Check that the HTML contains only visible presentation content.

## Applying This To Later Questions

Later question folders should be normalized toward the Q1 pattern.

That means:
- reduce theory-heavy markdown into visible cues plus `Talepunkter`
- keep HTML minimal
- reuse the shared theme first
- only add per-question styling overrides if there is a clear presentation need

Question 2 currently still reflects the older rough template style, so it should be simplified before HTML conversion rather than copied directly.
