package nl.werneroving.testrpg.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * The Radix dialog opened from the {@link Header}'s login button. Its inputs do
 * not expose data-testid attributes, so locators here use input type and the
 * dialog's role to scope everything inside the dialog.
 */
public class LoginDialog {

    private final Locator dialog;

    public LoginDialog(Page page) {
        this.dialog = page.getByRole(AriaRole.DIALOG);
    }

    public Locator emailInput() {
        return dialog.locator("input[type='email']");
    }

    public Locator passwordInput() {
        return dialog.locator("input[type='password']");
    }

    public Locator submitButton() {
        return dialog.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Login"));
    }

    public void loginWith(String email, String password) {
        emailInput().fill(email);
        passwordInput().fill(password);
        submitButton().click();
    }
}
