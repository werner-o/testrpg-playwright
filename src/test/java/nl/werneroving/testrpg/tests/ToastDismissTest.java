package nl.werneroving.testrpg.tests;

import nl.werneroving.testrpg.base.BaseTest;
import nl.werneroving.testrpg.pages.PlayPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Order(9)
@DisplayName("play AC8: completing a task shows a toast that disappears automatically after 3 seconds")
class ToastDismissTest extends BaseTest {

    @Test
    @DisplayName("success toast appears after completing the clicker task and is gone within ~3 seconds")
    void successToastDisappearsAfterThreeSeconds() {
        page.navigate(BASE_URL + "/play");
        PlayPage play = new PlayPage(page);
        play.enterName("Hero");
        play.clickStart();
        for (int i = 0; i < 5; i++) play.clickClicker();

        assertThat(play.successToast()).isVisible();
        // The toast is removed from the DOM by the documented 3000 ms dismiss timer.
        // Playwright's default 5-second assertion timeout absorbs that wait.
        assertThat(play.successToast()).not().isVisible();
    }
}
