package nl.werneroving.testrpg.tests;

import nl.werneroving.testrpg.base.BaseTest;
import nl.werneroving.testrpg.pages.PlayPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.stream.Stream;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Order(5)
@DisplayName("play AC4 + AC7: uploading a file levels up the character and disables the input")
class UploaderTest extends BaseTest {

    private static final Path FIXTURES_DIR = Path.of("src/test/resources/fixtures");

    @ParameterizedTest(name = "uploading {0} levels up the character, shows toast, and disables the input")
    @MethodSource("uploadCases")
    void uploadingFileLevelsUpCharacter(String description, Path file) {
        page.navigate(BASE_URL + "/play");
        PlayPage play = new PlayPage(page);
        play.enterName("Hero");
        play.clickStart();

        assertThat(play.statValue("Level")).hasText("1");

        play.uploadFile(file);

        assertThat(play.statValue("Level")).hasText("2");
        assertThat(play.successToast()).isVisible();
        assertThat(play.uploaderInput()).isDisabled();
        assertThat(play.clickerButton()).isEnabled();
        assertThat(play.typerInput()).isEnabled();
        assertThat(play.slider()).hasAttribute("aria-disabled", "false");
    }

    static Stream<Arguments> uploadCases() {
        return Stream.of(
                Arguments.of("a non-empty file", FIXTURES_DIR.resolve("sample.txt")),
                Arguments.of("an empty file", FIXTURES_DIR.resolve("empty.txt")),
                Arguments.of("a file without extension", FIXTURES_DIR.resolve("no-extension")),
                Arguments.of("a file with .exe extension", FIXTURES_DIR.resolve("risky.exe"))
        );
    }
}
