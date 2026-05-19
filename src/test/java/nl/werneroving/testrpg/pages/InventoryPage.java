package nl.werneroving.testrpg.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * The /inventory page lets the user select a build, see four items in a bag
 * (two weapons + two armor), and drag those items into four equipment slots.
 * Drag-and-drop uses native HTML5 events (draggable + onDragStart + onDrop).
 */
public class InventoryPage {

    private final Page page;

    public InventoryPage(Page page) {
        this.page = page;
    }

    public Locator pageContainer() {
        return page.getByTestId("inventory-page");
    }

    public Locator buildSelect() {
        return page.getByTestId("inventory-build-select");
    }

    public Locator bag() {
        return page.getByTestId("inventory-bag");
    }

    public Locator bagItems() {
        return bag().locator("[data-testid='inventory-item']");
    }

    public Locator bagItemsOfType(String type) {
        return bag().locator("[data-testid='inventory-item'][data-item-type='" + type + "']");
    }

    public Locator slot(String slotId) {
        return page.getByTestId("inventory-slot-" + slotId);
    }

    public Locator equippedItemIn(String slotId) {
        return slot(slotId).locator("[data-testid='equipped-item']");
    }

    public Locator successToast() {
        return page.locator("[data-testid='toast'][data-toast-type='success']");
    }

    public Locator errorToast() {
        return page.locator("[data-testid='toast'][data-toast-type='error']");
    }

    public void selectBuild(String build) {
        buildSelect().selectOption(build);
    }

    public void dragItemToSlot(Locator item, String slotId) {
        item.dragTo(slot(slotId));
    }
}
