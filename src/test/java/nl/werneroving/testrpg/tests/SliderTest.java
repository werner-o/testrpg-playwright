package nl.werneroving.testrpg.tests;

import nl.werneroving.testrpg.base.BaseTest;
import nl.werneroving.testrpg.pages.PlayPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Order(7)
@DisplayName("play AC6 + AC7: moving the slider all the way right levels up the character and disables it")
class SliderTest extends BaseTest {

    @Test
    @DisplayName("focus slider, press End: character levels up, toast shows, slider becomes disabled")
    void movingSliderToEndLevelsUpCharacter() {
        page.navigate(BASE_URL + "/play");
        PlayPage play = new PlayPage(page);
        play.enterName("Hero");
        play.clickStart();

        assertThat(play.statValue("Level")).hasText("1");

        play.moveSliderToEnd();

        assertThat(play.statValue("Level")).hasText("2");
        assertThat(play.successToast()).isVisible();
        assertThat(play.slider()).hasAttribute("aria-disabled", "true");
        assertThat(play.clickerButton()).isEnabled();
        assertThat(play.uploaderInput()).isEnabled();
        assertThat(play.typerInput()).isEnabled();
    }
}
