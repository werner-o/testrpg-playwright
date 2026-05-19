package nl.werneroving.testrpg.tests;

import nl.werneroving.testrpg.base.BaseTest;
import nl.werneroving.testrpg.pages.Header;
import nl.werneroving.testrpg.pages.LoginDialog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Order(13)
@DisplayName("Extra (no AC): login dialog, validation, login/logout flow, persistence")
class LoginTest extends BaseTest {

    @Test
    @DisplayName("clicking login opens a dialog with email, password and submit")
    void loginDialogOpensWithFields() {
        page.navigate(BASE_URL);
        Header header = new Header(page);

        LoginDialog dialog = header.openLogin();

        assertThat(dialog.emailInput()).isVisible();
        assertThat(dialog.passwordInput()).isVisible();
        assertThat(dialog.submitButton()).isVisible();
    }

    // The email input uses type="email", which triggers HTML5 browser-native validation
    // BEFORE the React onSubmit handler runs. The two tests below cover both perspectives:
    // the user-facing behavior (the form does not log you in) and the developer's intent
    // as expressed in the source (a custom Zod error message should appear).

    @ParameterizedTest(name = "submitting with invalid email ''{0}'' does not log the user in")
    @ValueSource(strings = {"abc", "foo@", "@bar.com", "no-at-here.com"})
    void invalidEmailDoesNotLogUserIn(String invalidEmail) {
        page.navigate(BASE_URL);
        Header header = new Header(page);
        LoginDialog dialog = header.openLogin();

        dialog.loginWith(invalidEmail, "anything");

        assertThat(header.logoutButton()).not().isVisible();
    }

    // This test is written against the developer's source-defined spec, namely the Zod
    // schema in src/components/login/form.tsx:
    //     email: z.string().email("This should be an email address")
    // The developer chose a custom message that should appear on invalid input. In
    // practice this assertion fails because the browser intercepts submission first
    // (see comment above) and the React/Zod path never runs. The failing test is the
    // finding: developer intent diverges from implementation reality.

    @ParameterizedTest(name = "invalid email ''{0}'' should show the developer-defined Zod error message")
    @ValueSource(strings = {"abc", "foo@", "@bar.com", "no-at-here.com"})
    void invalidEmailShouldShowCustomZodError(String invalidEmail) {
        page.navigate(BASE_URL);
        Header header = new Header(page);
        LoginDialog dialog = header.openLogin();

        dialog.loginWith(invalidEmail, "anything");

        assertThat(page.getByText("This should be an email address")).isVisible();
    }

    @Test
    @DisplayName("empty password is rejected with an inline error")
    void emptyPasswordIsRejected() {
        page.navigate(BASE_URL);
        Header header = new Header(page);
        LoginDialog dialog = header.openLogin();

        dialog.loginWith("valid@example.com", "");

        assertThat(page.getByText("Please enter your password")).isVisible();
        assertThat(header.logoutButton()).not().isVisible();
    }

    @Test
    @DisplayName("valid email plus non-empty password logs the user in; logout reverses it")
    void loginAndLogoutFlow() {
        page.navigate(BASE_URL);
        Header header = new Header(page);

        // Initial state: logged out
        assertThat(header.loginButton()).isVisible();
        assertThat(header.logoutButton()).not().isVisible();

        // Login
        header.openLogin().loginWith("hero@example.com", "anything");

        // Logged in: logout button appears, login button is gone
        assertThat(header.logoutButton()).isVisible();
        assertThat(header.loginButton()).not().isVisible();

        // Logout
        header.clickLogout();

        // Back to initial state
        assertThat(header.loginButton()).isVisible();
        assertThat(header.logoutButton()).not().isVisible();
    }

    @Test
    @DisplayName("login state survives a page reload (Zustand persist via localStorage)")
    void loginStateSurvivesReload() {
        page.navigate(BASE_URL);
        Header header = new Header(page);

        header.openLogin().loginWith("hero@example.com", "anything");
        assertThat(header.logoutButton()).isVisible();

        page.reload();

        assertThat(header.logoutButton()).isVisible();
    }
}
