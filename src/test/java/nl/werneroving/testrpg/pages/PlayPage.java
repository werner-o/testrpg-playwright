package nl.werneroving.testrpg.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.nio.file.Path;
import java.util.List;

public class PlayPage {

    private final Page page;

    public PlayPage(Page page) {
        this.page = page;
    }

    public Locator nameInput() {
        return page.getByTestId("character-name-input");
    }

    public Locator startButton() {
        return page.getByTestId("character-start-button");
    }

    public Locator characterName() {
        return page.getByTestId("character-name");
    }

    public Locator statValue(String statName) {
        return page.locator("[data-character-stats='" + statName + "'] span");
    }

    public void enterName(String name) {
        nameInput().fill(name);
    }

    public void clickStart() {
        startButton().click();
    }

    public void selectBuild(String buildName) {
        page.getByTestId("character-build-select").click();
        page.getByRole(AriaRole.OPTION,
                new Page.GetByRoleOptions().setName(buildName))
                .click();
    }

    public List<String> availableBuilds() {
        return page.locator("[data-testid='character-build-select'] ~ select option")
                .allInnerTexts();
    }

    public Locator clickerButton() {
        return page.getByTestId("clicker-button");
    }

    public void clickClicker() {
        clickerButton().click();
    }

    public Locator successToast() {
        return page.locator("[data-testid='toast'][data-toast-type='success']");
    }

    public Locator uploaderInput() {
        return page.getByTestId("uploader-input");
    }

    public void uploadFile(Path file) {
        uploaderInput().setInputFiles(file);
    }

    public Locator typerInput() {
        return page.getByTestId("typer-input");
    }

    public void typeText(String text) {
        typerInput().fill(text);
    }

    public Locator slider() {
        return page.getByTestId("slider-input");
    }

    public Locator sliderHandle() {
        return slider().locator("[role='slider']");
    }

    public void moveSliderToEnd() {
        sliderHandle().focus();
        page.keyboard().press("End");
    }
}
