package nl.werneroving.testrpg.tests;

import com.microsoft.playwright.Locator;
import nl.werneroving.testrpg.base.BaseTest;
import nl.werneroving.testrpg.pages.InventoryPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Order(14)
@DisplayName("inventory AC1-AC8: build selector, bag, slots, drag-and-drop equip, all-items-equipped message")
class InventoryTest extends BaseTest {

    @Test
    @DisplayName("inventory page loads with build selector, bag and four equipment slots")
    void inventoryPageLoads() {
        page.navigate(BASE_URL + "/inventory");
        InventoryPage inventory = new InventoryPage(page);

        assertThat(inventory.pageContainer()).isVisible();
        assertThat(inventory.buildSelect()).isVisible();
        assertThat(inventory.bag()).isVisible();
        assertThat(inventory.slot("weapon-1")).isVisible();
        assertThat(inventory.slot("weapon-2")).isVisible();
        assertThat(inventory.slot("armor-1")).isVisible();
        assertThat(inventory.slot("armor-2")).isVisible();
    }

    @Test
    @DisplayName("bag initially contains four items: two weapons and two armor")
    void bagInitiallyContainsFourItemsTwoWeaponsTwoArmor() {
        page.navigate(BASE_URL + "/inventory");
        InventoryPage inventory = new InventoryPage(page);

        assertThat(inventory.bagItems()).hasCount(4);
        assertThat(inventory.bagItemsOfType("weapon")).hasCount(2);
        assertThat(inventory.bagItemsOfType("armor")).hasCount(2);
    }

    @ParameterizedTest(name = "equipping ''{3}'' in slot {2} succeeds with a matching toast")
    @CsvSource({
            "weapon, 0, weapon-1, Knife",
            "weapon, 1, weapon-2, Katana",
            "armor,  0, armor-1,  Leather Armor",
            "armor,  1, armor-2,  Silver Armor"
    })
    void equippingItemInCorrectSlot(String type, int index, String slotId, String label) {
        page.navigate(BASE_URL + "/inventory");
        InventoryPage inventory = new InventoryPage(page);

        Locator item = inventory.bagItemsOfType(type).nth(index);
        inventory.dragItemToSlot(item, slotId);

        assertThat(inventory.equippedItemIn(slotId)).hasText(label);
        assertThat(inventory.bagItems()).hasCount(3);
        assertThat(inventory.successToast()).hasText(label + " equipped!");
    }

    // Note on the second case: the source builds the error message as
    // `A ${item.type} cannot be equipped...`, with no article variation.
    // For armor that produces "A armor..." which is grammatically wrong
    // ("An armor" would be correct). The assertion below matches the actual
    // source text; the grammar issue is flagged as an observation in TESTPLAN.md.

    @ParameterizedTest(name = "dragging a {0} onto slot {1} is rejected with the documented error toast")
    @CsvSource({
            "weapon, armor-1,  A weapon cannot be equipped in the armor slot.",
            "armor,  weapon-1, A armor cannot be equipped in the weapon slot."
    })
    void equippingWrongTypeShowsErrorToast(String type, String slotId, String message) {
        page.navigate(BASE_URL + "/inventory");
        InventoryPage inventory = new InventoryPage(page);

        Locator item = inventory.bagItemsOfType(type).first();
        inventory.dragItemToSlot(item, slotId);

        assertThat(inventory.errorToast()).hasText(message);
        assertThat(inventory.equippedItemIn(slotId)).hasCount(0);
        assertThat(inventory.bagItems()).hasCount(4);
    }

    @Test
    @DisplayName("dragging an item onto an already occupied slot shows the documented error toast")
    void equippingOnOccupiedSlotShowsErrorToast() {
        page.navigate(BASE_URL + "/inventory");
        InventoryPage inventory = new InventoryPage(page);

        // Equip the basic weapon (Knife) first
        inventory.dragItemToSlot(inventory.bagItemsOfType("weapon").first(), "weapon-1");
        assertThat(inventory.equippedItemIn("weapon-1")).hasText("Knife");

        // The bag now has 3 items; the first remaining weapon is the upgraded one (Katana)
        inventory.dragItemToSlot(inventory.bagItemsOfType("weapon").first(), "weapon-1");

        assertThat(inventory.errorToast()).hasText("This slot is already occupied.");
        // Knife is still equipped, Katana is still in the bag, count unchanged at 3
        assertThat(inventory.equippedItemIn("weapon-1")).hasText("Knife");
        assertThat(inventory.bagItems()).hasCount(3);
    }

    @Test
    @DisplayName("changing the build resets equipped slots and reloads the bag with the new build's items")
    void changingBuildResetsEquippedSlotsAndRefreshesBag() {
        page.navigate(BASE_URL + "/inventory");
        InventoryPage inventory = new InventoryPage(page);

        // Equip something on the default (Thief) build
        inventory.dragItemToSlot(inventory.bagItemsOfType("weapon").first(), "weapon-1");
        assertThat(inventory.equippedItemIn("weapon-1")).isVisible();

        // Switch to Knight
        inventory.selectBuild("knight");

        // Slot is empty again and the bag has all four (Knight's) items
        assertThat(inventory.equippedItemIn("weapon-1")).hasCount(0);
        assertThat(inventory.bagItems()).hasCount(4);
    }

    @Test
    @DisplayName("equipping all four items shows the 'All items equipped' message in the bag")
    void equippingAllFourItemsShowsAllItemsEquippedMessage() {
        page.navigate(BASE_URL + "/inventory");
        InventoryPage inventory = new InventoryPage(page);

        // Equip each of the four Thief items to its matching slot.
        // After each drag, the next .first() returns the next remaining item of that type.
        inventory.dragItemToSlot(inventory.bagItemsOfType("weapon").first(), "weapon-1");
        inventory.dragItemToSlot(inventory.bagItemsOfType("weapon").first(), "weapon-2");
        inventory.dragItemToSlot(inventory.bagItemsOfType("armor").first(), "armor-1");
        inventory.dragItemToSlot(inventory.bagItemsOfType("armor").first(), "armor-2");

        assertThat(inventory.bagItems()).hasCount(0);
        assertThat(inventory.bag()).containsText("All items equipped");
    }
}


