# Exam Prep Guidance

This repository is often used for exam preparation and study support in addition to software work.

## Default behavior

- Default to read-only study support unless the user explicitly asks for code or file changes.
- Do not modify source files, tests, configs, or assets for exam-prep requests.
- For exam-prep tasks, prioritize understanding, summarizing, quizzing, and comparison over implementation.

## Reading priorities

- Prioritize `documentation/` before `src/` when the user is studying concepts, assignments, architecture, or delivery requirements.
- Treat `exam-prep/eksamensspørgsmål.md` as a primary source for oral-exam preparation, question selection, and answer planning.
- Use `test/` to explain expected behavior and examples when helpful.
- Only dive deeply into `src/` when the study question depends on implementation details.
- When preparing an answer to a specific exam question, map it to the most relevant files in `documentation/`, `src/`, and `test/`.

## Response style for study sessions

- Cite the specific files used when summarizing or explaining.
- Prefer concise explanations first, then expand if the user wants more depth.
- Offer useful exam-prep outputs such as topic maps, flashcards, oral-exam questions, checklists, and weak-spot reviews.
- Keep a running thread-level summary of covered topics and open gaps when the user is iterating on prep.

## Exam-prep workspace

- Treat `exam-prep/` as the place for study artifacts, prompts, summaries, and revision notes.
- When creating new exam-prep material, write it under `exam-prep/` unless the user asks for a different location.
- Reuse `exam-prep/eksamensspørgsmål.md` as the canonical list of exam topics unless the user provides a newer source.
