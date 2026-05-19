package nl.werneroving.testrpg.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * The site header bar that appears on every page. Holds navigation links and
 * the login/logout button. The login button opens a {@link LoginDialog}.
 */
public class Header {

    private final Page page;

    public Header(Page page) {
        this.page = page;
    }

    public Locator loginButton() {
        return page.getByTestId("login-button");
    }

    public Locator logoutButton() {
        return page.getByTestId("logout-button");
    }

    public LoginDialog openLogin() {
        loginButton().click();
        return new LoginDialog(page);
    }

    public void clickLogout() {
        logoutButton().click();
    }
}
