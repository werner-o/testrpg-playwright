package nl.werneroving.testrpg.base;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.util.HashSet;
import java.util.Set;

public abstract class ApiBaseTest {

    // The TestRPG project runs the UI on port 3000 (Vite) and the API on port 3001
    // (a separate Express server defined in server.ts). Both are started together by
    // `pnpm dev` in C:\Projects\testrpg-main.
    protected static final String BASE_URL = "http://localhost:3001";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static Playwright playwright;
    protected APIRequestContext request;

    @BeforeAll
    static void initPlaywright() {
        playwright = Playwright.create();
    }

    @AfterAll
    static void closePlaywright() {
        playwright.close();
    }

    @BeforeEach
    void setupTest(TestInfo testInfo) {
        // Print the test name so the [API] lines below it can be linked back to a test.
        System.out.println();
        System.out.println("[TEST] " + testInfo.getDisplayName());
        request = playwright.request().newContext(
                new APIRequest.NewContextOptions().setBaseURL(BASE_URL)
        );
    }

    @AfterEach
    void closeRequestContext() {
        request.dispose();
    }

    // Playwright's tracing only works on a BrowserContext, not on a pure APIRequestContext.
    // To still give us visibility in the Run window for API tests, the two helpers below
    // wrap request.get() and request.post() and print the call plus the response status
    // and pretty-printed body to stdout. Tests should use these methods instead of calling
    // `request` directly so every call shows up.

    protected APIResponse get(String url) {
        APIResponse response = request.get(url);
        logResponse("GET", url, null, response);
        return response;
    }

    protected APIResponse post(String url, Object jsonBody) {
        APIResponse response = request.post(url,
                RequestOptions.create().setData(jsonBody));
        logResponse("POST", url, jsonBody, response);
        return response;
    }

    private void logResponse(String method, String url, Object body, APIResponse response) {
        System.out.println("[API] " + method + " " + url + " -> " + response.status());
        if (body != null) {
            System.out.println("Request:");
            System.out.println(prettyPrint(body));
        }
        System.out.println("Response:");
        System.out.println(prettyPrint(response.text()));
    }

    // The /api documentation defines a build as exactly these five fields (via the POST
    // schema; GET response shape is not separately documented). Tests use this set to
    // assert strict shape compliance.
    protected static final Set<String> DOCUMENTED_BUILD_FIELDS =
            Set.of("name", "strength", "agility", "wisdom", "magic");

    protected static Set<String> fieldNames(JsonNode node) {
        Set<String> set = new HashSet<>();
        node.fieldNames().forEachRemaining(set::add);
        return set;
    }

    private String prettyPrint(Object value) {
        try {
            // Route everything through a JsonNode so we can use toPrettyString() uniformly,
            // whether the input is a raw JSON string (response body) or a Java object like
            // a Map (request body).
            JsonNode node = (value instanceof String s) ? MAPPER.readTree(s) : MAPPER.valueToTree(value);
            return node.toPrettyString();
        } catch (Exception e) {
            // Not valid JSON (e.g. an empty body or an HTML error page). Fall back to raw.
            return String.valueOf(value);
        }
    }
}
