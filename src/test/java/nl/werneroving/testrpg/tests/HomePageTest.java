package nl.werneroving.testrpg.tests;

import nl.werneroving.testrpg.base.BaseTest;
import nl.werneroving.testrpg.pages.HomePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Order(1)
@DisplayName("Smoke (no specific AC): homepage loads and play link navigates")
class HomePageTest extends BaseTest {

    @Test
    @DisplayName("homepage loads with title 'TestRPG'")
    void homePageLoadsWithCorrectTitle() {
        page.navigate(BASE_URL);

        assertThat(page).hasTitle("TestRPG");
    }

    @Test
    @DisplayName("clicking the play link navigates to /play")
    void clickingPlayLinkNavigatesToPlayPage() {
        page.navigate(BASE_URL);

        HomePage home = new HomePage(page);
        home.clickPlay();

        assertThat(page).hasURL(BASE_URL + "/play");
    }
}
