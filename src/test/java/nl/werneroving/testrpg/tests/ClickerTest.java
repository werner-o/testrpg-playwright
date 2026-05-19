package nl.werneroving.testrpg.tests;

import nl.werneroving.testrpg.base.BaseTest;
import nl.werneroving.testrpg.pages.PlayPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Order(4)
@DisplayName("play AC3 + AC7: 5 clicks level up the character and disable the button")
class ClickerTest extends BaseTest {

    @Test
    @DisplayName("5 clicks level up the character, show a success toast, and disable the button")
    void fiveClicksCompleteTheClickerTask() {
        page.navigate(BASE_URL + "/play");
        PlayPage play = new PlayPage(page);
        play.enterName("Hero");
        play.clickStart();

        assertThat(play.statValue("Level")).hasText("1");

        for (int i = 0; i < 5; i++) {
            play.clickClicker();
        }

        assertThat(play.statValue("Level")).hasText("2");
        assertThat(play.successToast()).isVisible();
        assertThat(play.clickerButton()).isDisabled();
        assertThat(play.uploaderInput()).isEnabled();
        assertThat(play.typerInput()).isEnabled();
        assertThat(play.slider()).hasAttribute("aria-disabled", "false");
    }
}
