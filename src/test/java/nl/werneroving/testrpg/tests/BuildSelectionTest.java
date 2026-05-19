package nl.werneroving.testrpg.tests;

import nl.werneroving.testrpg.base.BaseTest;
import nl.werneroving.testrpg.pages.PlayPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Order(3)
@DisplayName("play AC2: changing the build type changes character stats")
class BuildSelectionTest extends BaseTest {

    @Test
    @DisplayName("changing build always changes the stats")
    void changingBuildAlwaysChangesStats() {
        page.navigate(BASE_URL + "/play");
        PlayPage play = new PlayPage(page);
        List<String> builds = play.availableBuilds();
        String previousBuild = builds.get(0);
        play.selectBuild(previousBuild);
        String previousStats = currentStats(play);

        for (int i = 1; i < builds.size(); i++) {
            String build = builds.get(i);

            play.selectBuild(build);
            String stats = currentStats(play);

            assertNotEquals(previousStats, stats,
                    "Changing build from " + previousBuild + " to " + build
                            + " should change stats but they stayed " + previousStats);

            previousBuild = build;
            previousStats = stats;
        }
    }

    private String currentStats(PlayPage play) {
        return String.join("/",
                play.statValue("Strength").textContent(),
                play.statValue("Agility").textContent(),
                play.statValue("Wisdom").textContent(),
                play.statValue("Magic").textContent()
        );
    }
}
