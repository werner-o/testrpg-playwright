package nl.werneroving.testrpg.tests;

import nl.werneroving.testrpg.base.BaseTest;
import nl.werneroving.testrpg.pages.PlayPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Order(8)
@DisplayName("play AC7 (cumulative): completing all four tasks reaches level 5 with every input disabled")
class FullProgressionTest extends BaseTest {

    @Test
    @DisplayName("complete clicker, uploader, typer, slider: level 1 to 5, each input disabled in turn")
    void completingAllTasksReachesLevelFiveAndDisablesAllInputs() {
        page.navigate(BASE_URL + "/play");
        PlayPage play = new PlayPage(page);
        play.enterName("Hero");
        play.clickStart();

        assertThat(play.statValue("Level")).hasText("1");

        for (int i = 0; i < 5; i++) play.clickClicker();
        assertThat(play.statValue("Level")).hasText("2");
        assertThat(play.clickerButton()).isDisabled();

        play.uploadFile(Path.of("src/test/resources/fixtures/sample.txt"));
        assertThat(play.statValue("Level")).hasText("3");
        assertThat(play.uploaderInput()).isDisabled();

        play.typeText("Lorem Ipsum");
        assertThat(play.statValue("Level")).hasText("4");
        assertThat(play.typerInput()).isDisabled();

        play.moveSliderToEnd();
        assertThat(play.statValue("Level")).hasText("5");
        assertThat(play.slider()).hasAttribute("aria-disabled", "true");
    }
}
