package nl.werneroving.testrpg.tests.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIResponse;
import nl.werneroving.testrpg.base.ApiBaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Order(12)
@DisplayName("API: POST /api/builds")
class PostBuildsTest extends ApiBaseTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // The API stores POSTed builds in an in-memory map until the dev server restarts.
    // Tests that succeed leave a record behind, so we use a unique random name per
    // request to avoid collisions across tests and across repeated runs.
    private static String uniqueName() {
        return "necromancer-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    @DisplayName("a valid new build returns 201 with a body matching the documented schema")
    void validNewBuildIsCreated() throws IOException {
        String name = uniqueName();
        APIResponse response = post("/api/builds", wrap(validBuild(name, 2, 3, 3, 2)));

        assertEquals(201, response.status());

        JsonNode json = MAPPER.readTree(response.text());
        JsonNode build = json.get("build");
        assertEquals(name, build.get("name").asText());
        // The POST round-trip is documented: request schema is {name, strength, agility,
        // wisdom, magic} and the response build should match. Strict shape check.
        assertEquals(DOCUMENTED_BUILD_FIELDS, fieldNames(build));
    }

    @Test
    @DisplayName("sum of stats exactly 10 (boundary) is accepted")
    void sumExactlyTenIsAccepted() {
        APIResponse response = post("/api/builds", wrap(validBuild(uniqueName(), 2, 2, 3, 3)));

        assertEquals(201, response.status());
    }

    @ParameterizedTest(name = "name ''{0}'' already exists, POST is rejected with 409")
    @ValueSource(strings = {"thief", "knight", "mage", "brigadier"})
    void existingNameIsRejected(String existingName) {
        APIResponse response = post("/api/builds", wrap(validBuild(existingName, 1, 1, 1, 1)));

        assertEquals(409, response.status());
    }

    @ParameterizedTest(name = "{0} above 10 is rejected with 400")
    @ValueSource(strings = {"strength", "agility", "wisdom", "magic"})
    void skillAboveMaxIsRejected(String skill) {
        Map<String, Object> stats = validBuild(uniqueName(), 1, 1, 1, 1);
        stats.put(skill, 11);

        APIResponse response = post("/api/builds", wrap(stats));

        assertEquals(400, response.status());
    }

    @Test
    @DisplayName("sum of stats above 10 (e.g. 3+3+3+2=11) is rejected with 400")
    void sumAboveMaxIsRejected() {
        APIResponse response = post("/api/builds", wrap(validBuild(uniqueName(), 3, 3, 3, 2)));

        assertEquals(400, response.status());
    }

    @ParameterizedTest(name = "missing field ''{0}'' is rejected with 400")
    @ValueSource(strings = {"name", "strength", "agility", "wisdom", "magic"})
    void missingFieldIsRejected(String missingField) {
        Map<String, Object> stats = validBuild(uniqueName(), 1, 1, 1, 1);
        stats.remove(missingField);

        APIResponse response = post("/api/builds", wrap(stats));

        assertEquals(400, response.status());
    }

    private static Map<String, Object> validBuild(String name, int str, int agi, int wis, int mag) {
        Map<String, Object> build = new HashMap<>();
        build.put("name", name);
        build.put("strength", str);
        build.put("agility", agi);
        build.put("wisdom", wis);
        build.put("magic", mag);
        return build;
    }

    private static Map<String, Object> wrap(Map<String, Object> build) {
        return Map.of("build", build);
    }
}
