package nl.werneroving.testrpg.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.nio.file.Path;

public abstract class BaseTest {

    // The deployed app at https://testrpg-one.vercel.app does not include the API (all
    // /api/* paths return 404). To run UI and API tests against the same instance for
    // consistency, we point all tests at the local Next.js dev server.
    // Start it with `pnpm dev` from C:\Projects\testrpg-main.
    protected static final String BASE_URL = "http://localhost:3000";

    private static Playwright playwright;
    private static Browser browser;

    protected BrowserContext context;
    protected Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
//                        .setSlowMo(1000)
        );
    }

    @AfterAll
    static void closeBrowser() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();

        // Playwright tracing records every action with a screenshot, DOM snapshot, network
        // log, console messages, and source code reference. After a test run, view the
        // resulting .zip in Playwright's Trace Viewer with:
        //     npx playwright show-trace target/traces/<filename>.zip
        // One trace file is produced per test (see closeContext below).
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));

        page = context.newPage();
    }

    @AfterEach
    void closeContext(TestInfo testInfo) {
        // Save the trace under target/traces/<sanitised-test-name>-<timestamp>.zip
        // so each run produces a unique, identifiable file.
        String safeName = testInfo.getDisplayName().replaceAll("[^a-zA-Z0-9._-]", "_");
        Path tracePath = Path.of("target", "traces", safeName + "-" + System.currentTimeMillis() + ".zip");
        context.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
        context.close();
    }
}
