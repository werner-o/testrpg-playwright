# TestRPG Playwright

Automated tests for the [TestRPG](https://github.com/micheltestcoders/testrpg) application, built as a case study to demonstrate test automation engineering practice.

## Tech stack

- Java 21
- Maven
- Playwright Java
- JUnit 5
- Chromium (headed, slowed down for observability during development)

## Running the tests

### Prerequisites
- Java 21 installed and on the path (or accessible via IntelliJ)
- Maven (or use the Maven bundled with IntelliJ)

### From the terminal
```
mvn test
```

### From IntelliJ
Open the Maven tool window on the right side, navigate to **Lifecycle**, and double-click **test**. Or right-click any test class and select **Run**.

First run downloads the Playwright Chromium binaries automatically (about 30 to 60 seconds). Subsequent runs are fast.

Tests run **headed** with `setSlowMo(500)` configured in `BaseTest.java`, so you can follow what happens on screen. To run faster, remove the `setSlowMo` line.

### All tests need the local dev servers

TestRPG runs as two processes: a Vite UI server on [http://localhost:3000](http://localhost:3000) and an Express API server on `http://localhost:3001`. The UI tests target 3000 and the API tests target 3001. Both are started together by `pnpm dev` in the TestRPG source repo.

To start them, in a separate terminal:
```
cd path/to/testrpg-main
corepack enable pnpm     # one-time setup
pnpm install             # one-time
pnpm dev                 # leave running
```

Leave that terminal open while running the test suite. If the servers are not running, every test will fail with a connection error.

### Inspecting UI test runs with Playwright Trace Viewer

Each UI test produces a trace file under `target/traces/` containing screenshots, DOM snapshots, network logs, and console output for every action. The trace is the easiest way to debug a failing test or to demo what happened during a run.

To open the Trace Viewer:

```
npx playwright show-trace
```

This opens an empty viewer in your browser; drag a `.zip` from `target/traces/` onto the page to inspect that run. Alternatively, pass a specific file:

```
npx playwright show-trace target\traces\<filename>.zip
```

No-install alternative: drag any `.zip` from `target/traces/` onto https://trace.playwright.dev. The viewer runs entirely in the browser, no upload happens.

### Inspecting API test runs

API tests print every request and response (status code plus pretty-printed JSON) to stdout under a `[TEST]` header. Open the test's Run window in IntelliJ to see the full sequence of calls per test.

## Design choices

- **Playwright Java over Selenium**: better fit for a React + Radix UI application (auto-waiting, robust role-based locators, simpler setup). The TestRPG project README itself mentions Playwright as an expected framework.
- **JUnit 5 over TestNG**: idiomatic with Playwright Java and aligns with most modern Java projects.
- **Tests organized per acceptance criterion**, not per page. Test classes map directly to AC entries for traceability. POMs (`HomePage`, `PlayPage`) are shared across test classes and reflect UI structure.
- **`getByTestId` for locators wherever possible.** The app exposes stable `data-testid` attributes, which insulates tests from CSS class changes.

## Documentation

See [TESTPLAN.md](TESTPLAN.md) for the acceptance criteria and the test coverage matrix.

## A note on AI assistance

This framework was built with significant assistance from Anthropic's Claude. I drove the design decisions, pushed back on shortcuts, verified behaviour manually, and decided scope; the code and documentation were drafted through that collaboration. I'm happy to walk through any choice in person.
