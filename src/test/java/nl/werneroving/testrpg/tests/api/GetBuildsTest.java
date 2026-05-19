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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Order(11)
@DisplayName("API: GET /api/builds")
class GetBuildsTest extends ApiBaseTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    @DisplayName("GET without parameter returns all four predefined builds")
    void getWithoutParamReturnsAllBuilds() throws IOException {
        APIResponse response = get("/api/builds");

        assertEquals(200, response.status());

        JsonNode body = MAPPER.readTree(response.text());
        assertTrue(body.has("thief"), "expected 'thief' build in response");
        assertTrue(body.has("knight"), "expected 'knight' build in response");
        assertTrue(body.has("mage"), "expected 'mage' build in response");
        assertTrue(body.has("brigadier"), "expected 'brigadier' build in response");
    }

    // This test asserts the build object has EXACTLY the fields documented in the /api
    // page (via the POST schema): name, strength, agility, wisdom, magic. The predefined
    // builds in the source include weapon/upgradedWeapon/armor/upgradedArmor and lack a
    // `name` field, so this assertion fails for all four. The failure is the finding:
    // GET response shape for predefined builds deviates from the documented schema.

    @ParameterizedTest(name = "GET ?build={0} returns exactly the documented build fields")
    @ValueSource(strings = {"thief", "knight", "mage", "brigadier"})
    void getValidBuildHasExactDocumentedShape(String buildName) throws IOException {
        APIResponse response = get("/api/builds?build=" + buildName);

        assertEquals(200, response.status());

        JsonNode body = MAPPER.readTree(response.text());
        JsonNode build = body.get(buildName);
        assertNotNull(build, "expected response to contain key '" + buildName + "'");
        assertEquals(DOCUMENTED_BUILD_FIELDS, fieldNames(build));
    }

    @Test
    @DisplayName("GET ?build=unknown returns 404 with an error body")
    void getUnknownBuildReturns404() throws IOException {
        APIResponse response = get("/api/builds?build=does-not-exist");

        assertEquals(404, response.status());

        JsonNode body = MAPPER.readTree(response.text());
        assertTrue(body.has("error"));
    }
}
