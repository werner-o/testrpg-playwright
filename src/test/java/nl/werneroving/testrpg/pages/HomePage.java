package nl.werneroving.testrpg.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class HomePage {

    private final Page page;

    public HomePage(Page page) {
        this.page = page;
    }

    public Locator playLink() {
        return page.getByTestId("play-link");
    }

    public void clickPlay() {
        playLink().click();
    }
}
