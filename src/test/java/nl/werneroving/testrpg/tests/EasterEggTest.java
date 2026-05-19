package nl.werneroving.testrpg.tests;

import nl.werneroving.testrpg.base.BaseTest;
import nl.werneroving.testrpg.pages.PlayPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Order(10)
@DisplayName("play Bonus: easter egg in the typer maxes the character's stats on the next level up")
class EasterEggTest extends BaseTest {

    @Test
    @DisplayName("typing 'all your base are belong to us' then triggering a level up sets every stat to 10")
    void easterEggMaxesStatsOnLevelUp() {
        page.navigate(BASE_URL + "/play");
        PlayPage play = new PlayPage(page);
        play.enterName("Hero");
        play.clickStart();

        // Typing the easter egg phrase activates a hidden 'berserk' state
        // but does not level up by itself.
        play.typeText("all your base are belong to us");
        assertThat(play.statValue("Level")).hasText("1");

        // Triggering any level up while berserk is active grants +10 to each stat
        // (capped at 10), bringing every stat to its maximum.
        for (int i = 0; i < 5; i++) play.clickClicker();

        assertThat(play.statValue("Level")).hasText("2");
        assertThat(play.statValue("Strength")).hasText("10");
        assertThat(play.statValue("Agility")).hasText("10");
        assertThat(play.statValue("Wisdom")).hasText("10");
        assertThat(play.statValue("Magic")).hasText("10");
    }

    @Test
    @DisplayName("easter egg plus all four tasks: level reaches 5 and every stat stays at 10")
    void easterEggPlusAllTasksMaxesLevelAndStats() {
        page.navigate(BASE_URL + "/play");
        PlayPage play = new PlayPage(page);
        play.enterName("Hero");
        play.clickStart();

        play.typeText("all your base are belong to us");

        for (int i = 0; i < 5; i++) play.clickClicker();
        play.uploadFile(Path.of("src/test/resources/fixtures/sample.txt"));
        play.typeText("Lorem Ipsum");
        play.moveSliderToEnd();

        assertThat(play.statValue("Level")).hasText("5");
        assertThat(play.statValue("Strength")).hasText("10");
        assertThat(play.statValue("Agility")).hasText("10");
        assertThat(play.statValue("Wisdom")).hasText("10");
        assertThat(play.statValue("Magic")).hasText("10");
    }
}
