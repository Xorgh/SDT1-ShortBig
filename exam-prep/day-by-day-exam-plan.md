# Day-by-Day Exam Prep Plan

Exam date: Thursday, June 11, 2026
Today: Saturday, June 6, 2026

This plan turns the 8 exam topics from `exam-prep/eksamensspørgsmål.md` into a focused checklist for the days leading up to the exam.

## Main goal for the week

- Build confident 5-7 minute answers for all 8 topics
- Be able to connect theory, UML, and the concrete project implementation
- Practice speaking out loud, not just reading notes
- Finish with a light review on the day before the exam

## Saturday, June 6, 2026
Focus: architecture foundation and planning

- [ ] Read all 8 exam questions in `exam-prep/eksamensspørgsmål.md`
- [ ] Review `documentation/Requirements.md` so the system purpose is clear
- [ ] Open `documentation/ClassDiagram.puml` and identify the main packages
- [ ] Prepare a short system overview: what the app does, main layers, and key features
- [ ] Study Question 1: Three-layer architecture and Transaction Script
- [ ] Identify examples from `src/presentation/`, `src/business/`, and `src/persistence/`
- [ ] Find one strong Transaction Script example in the service layer
- [ ] Create short speaking notes for Question 1
- [ ] Study Question 4: DAO and Unit of Work
- [ ] Identify DAO interfaces and file-based implementations
- [ ] Explain how Unit of Work coordinates persistence operations
- [ ] Create short speaking notes for Question 4
- [ ] End the day by explaining Questions 1 and 4 out loud without reading

## Sunday, June 7, 2026
Focus: UI architecture and testing

- [ ] Study Question 6: MVVM
- [ ] Inspect view, controller, and view model examples in `src/presentation/views/`
- [ ] Note how JavaFX bindings support MVVM
- [ ] Create short speaking notes for Question 6
- [ ] Study Question 5: Testing
- [ ] Review V-model theory from course material and assignment docs
- [ ] Compare unit tests in `test/business/services/` with integration tests in `test/integration/`
- [ ] Identify examples of mocks in `test/mocks/`
- [ ] Prepare one concrete unit test example and one concrete integration test example
- [ ] Explain out loud how MVVM and testing show quality and structure in the project

## Monday, June 8, 2026
Focus: behavioral patterns in the stock market simulation

- [ ] Study Question 2: Observer pattern
- [ ] Identify where updates are published and where listeners react
- [ ] Prepare one concrete example from the event/handler flow
- [ ] Create short speaking notes for Question 2
- [ ] Study Question 7: State pattern
- [ ] Review the stock market simulation classes in `src/business/stockmarket/simulation/`
- [ ] Understand the role of `LiveStockState`, concrete state classes, and transition management
- [ ] Be able to explain why State is better than large condition chains here
- [ ] Create short speaking notes for Question 7
- [ ] End the day with a 10-15 minute mini mock where you answer Questions 2 and 7 aloud

## Tuesday, June 9, 2026
Focus: structural and interchangeable behavior patterns

- [ ] Study Question 3: Adapter pattern
- [ ] Choose one clear adapter example from the project
- [ ] Be ready to explain the target interface, adaptee, and why it is an adapter
- [ ] Create short speaking notes for Question 3
- [ ] Study Question 8: Strategy pattern
- [ ] Review `src/business/fees/`
- [ ] Explain the strategy interface and the concrete fee strategies
- [ ] Compare Strategy and State so you are ready for follow-up questions
- [ ] Create short speaking notes for Question 8
- [ ] Practice all four pattern questions briefly: Observer, Adapter, State, Strategy

## Wednesday, June 10, 2026
Focus: full rehearsal and weak spots

- [ ] Make one-page summaries or one slide per question
- [ ] For each of the 8 questions, write:
- [ ] A 1-sentence definition
- [ ] A 3-point explanation of theory
- [ ] 2-3 project file references
- [ ] 1 concrete code example to show
- [ ] Do a random draw of 3 questions and answer them under time pressure
- [ ] Note weak spots: UML, terminology, code navigation, or theory
- [ ] Revisit only the weak areas instead of rereading everything
- [ ] Practice transitions between theory and code: "generally this pattern does X, in our project it appears in Y"
- [ ] Prepare your exam setup: IntelliJ, presentation, and diagram ready
- [ ] Finish with a calm final run-through, not a heavy cram session

## Thursday, June 11, 2026
Focus: calm review and exam execution

- [ ] Spend 30-60 minutes on light review only
- [ ] Skim your summaries for all 8 questions
- [ ] Review the class diagram and main package structure one last time
- [ ] Practice one random question out loud
- [ ] Do not start any new deep study topics
- [ ] Make sure your presentation, IDE, and project files are ready to open
- [ ] Join the exam meeting 30 minutes early as instructed in `exam-prep/eksamensspørgsmål.md`

## Daily routine checklist

- [ ] Start with 5 minutes of recalling yesterday's topics from memory
- [ ] Study theory first, then connect it to project files
- [ ] Speak answers out loud every day
- [ ] Keep notes in keywords, not full manuscript form
- [ ] Track weak spots immediately when you notice them
- [ ] End each day by naming the 1-2 topics that still feel shaky

## Priority sources

- `exam-prep/eksamensspørgsmål.md`
- `documentation/Requirements.md`
- `documentation/ClassDiagram.puml`
- `documentation/assignment7/`
- `documentation/assignment9/`
- `src/business/`
- `src/presentation/`
- `src/persistence/`
- `test/`

## Success criteria before the exam

- [ ] I can explain all 8 topics without reading full sentences
- [ ] I can connect each topic to specific files in the project
- [ ] I can answer follow-up questions about purpose, structure, and tradeoffs
- [ ] I can navigate quickly to the main code examples in IntelliJ
- [ ] I feel ready to handle a random draw from any of the 8 questions
