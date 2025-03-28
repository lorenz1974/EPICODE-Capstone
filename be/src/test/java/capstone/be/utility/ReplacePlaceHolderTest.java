package capstone.be.utility;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.Order;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

//
// Unit test for ReplacePlaceHolders class
//
@ExtendWith(MockitoExtension.class)
@Slf4j
public class ReplacePlaceHolderTest {

    private final ReplacePlaceHolders replacePlaceHolders = new ReplacePlaceHolders(); // Direct instance

    //
    // Tests if the query retrieves the correct user with ID 1 and username
    // "superadmin"

    @Order(1)
    @Test
    public void testASimpleReplacement() {
        String input = "Hello {{name}}!";
        List<Map<String, Object>> replacements = List.of(Map.of("name", "World"));

        String output = replacePlaceHolders.of(input, replacements);
        assertEquals("Hello World!", output, "Replacement did not work as expected!");
    }

    @Order(2)
    @Test
    public void testMultipleReplacements() {
        String input = "Hello {{name}}! You are {{age}} years old and usually play {{game}}.";
        List<Map<String, Object>> replacements = List.of(
                Map.of("name", "World"),
                Map.of("age", 30),
                Map.of("game", "Chess"));

        // log.info("\n\nReplacements: " + replacements + "\n\n");

        String output = replacePlaceHolders.of(input, replacements);
        assertEquals("Hello World! You are 30 years old and usually play Chess.",
                output,
                "Replacement did not work as expected!");
    }

    // @Order(3)
    // @Test
    // public void testJsonReplacements() {
    // String input = readTxtTemplate("genericDocument.json");
    // List<Map<String, Object>> replacements = List.of(
    // Map.of("envelopname", "Richiesta firma per documento generico per
    // {{signername}} {{signersurname}}"),
    // Map.of("signeremail", "matilde.russo@ferrara.it"),
    // Map.of("signername", "Matilde"),
    // Map.of("signersurname", "Russo"),
    // Map.of("signercellphone", "1-717-841-4063"),
    // Map.of("creatoremail", "gianmaria.messina@martini.com"),
    // Map.of("creatorname", "Gianmaria"),
    // Map.of("creatorsurname", "Messina"),
    // Map.of("creatorcellphone", "1-608-959-9943"),
    // Map.of("emailsubject", "Richiesta firma per documento generico per
    // {{signername}} {{signersurname}}"));

    // log.info("\n\nReplacements: " + replacements + "\n\n");

    // String output = replacePlaceHolders.of(input, replacements);

    // log.info("\n\nOutput: " + output + "\n\n");

    // assertNotNull(output, "Output is null!");
    // }

    private String readTxtTemplate(String templateName) {
        try {
            String filePath = "src\\main\\resources\\filesForTemplates\\" + templateName;
            return new String(Files.readAllBytes(Paths.get(filePath)), java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
