package nl.werneroving.testrpg.tests;

import nl.werneroving.testrpg.base.BaseTest;
import nl.werneroving.testrpg.pages.PlayPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Order(6)
@DisplayName("play AC5 + AC7: typing 'Lorem Ipsum' levels up the character and disables the input")
class TyperTest extends BaseTest {

    @Test
    @DisplayName("typing 'Lorem Ipsum' levels up the character, shows toast, and disables the input")
    void typingLoremIpsumLevelsUpCharacter() {
        page.navigate(BASE_URL + "/play");
        PlayPage play = new PlayPage(page);
        play.enterName("Hero");
        play.clickStart();

        assertThat(play.statValue("Level")).hasText("1");

        play.typeText("Lorem Ipsum");

        assertThat(play.statValue("Level")).hasText("2");
        assertThat(play.successToast()).isVisible();
        assertThat(play.typerInput()).isDisabled();
        assertThat(play.clickerButton()).isEnabled();
        assertThat(play.uploaderInput()).isEnabled();
        assertThat(play.slider()).hasAttribute("aria-disabled", "false");
    }

    @ParameterizedTest(name = "typing ''{0}'' does not level up the character")
    @ValueSource(strings = {"lorem ipsum", "Lorem ipsum", "Hello World", "*orem Ipsum", " Lorem Ipsum " })
    void typingOtherTextDoesNotLevelUpCharacter(String wrongText) {
        page.navigate(BASE_URL + "/play");
        PlayPage play = new PlayPage(page);
        play.enterName("Hero");
        play.clickStart();

        play.typeText(wrongText);

        assertThat(play.statValue("Level")).hasText("1");
    }
}
