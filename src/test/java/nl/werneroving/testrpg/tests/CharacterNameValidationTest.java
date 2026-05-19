package nl.werneroving.testrpg.tests;

import nl.werneroving.testrpg.base.BaseTest;
import nl.werneroving.testrpg.pages.PlayPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Order(2)
@DisplayName("play AC1: character name must be 3 to 20 characters")
class CharacterNameValidationTest extends BaseTest {

    @ParameterizedTest(name = "name of {0} characters is accepted")
    @ValueSource(ints = {3, 20})
    void nameOfValidLengthIsAccepted(int length) {
        page.navigate(BASE_URL + "/play");

        String name = "a".repeat(length);
        PlayPage play = new PlayPage(page);
        play.enterName(name);
        play.clickStart();

        assertThat(play.characterName()).hasText(name);
    }

    @ParameterizedTest(name = "name of {0} characters shows: {1}")
    @CsvSource({
            "2, Name must be at least 3 characters",
            "21, Name cannot be longer than 20 characters"
    })
    void nameOfInvalidLengthIsRejected(int length, String expectedError) {
        page.navigate(BASE_URL + "/play");

        PlayPage play = new PlayPage(page);
        play.enterName("a".repeat(length));
        play.clickStart();

        assertThat(play.nameInput()).hasAttribute("aria-invalid", "true");
        assertThat(page.getByText(expectedError)).isVisible();
    }
}
