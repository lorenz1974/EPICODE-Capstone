package capstone.be.mail;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class GraphServiceTests {

    @Autowired
    private GraphService graphService;

    @Autowired
    private MailService mailService;

    @Test
    void testSendNotification() {

        String recipient = "l.lione@rmt.srl";

        String subject = "Test Email da GraphService - testSendNotification - "
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        String title = "Notifica di test";

        String bodyHtml = "<h1>Ciao!</h1><p>Questa è una mail di test inviata realmente da GraphService.</p>";

        // Assicuriamoci che l'invio non generi eccezioni
        assertDoesNotThrow(() -> mailService.sendNotification(recipient, subject, title, bodyHtml));
    }

    @Test
    void testSendAlert() {

        String recipient = "lorenzo.lione@gmail.com";

        String subject = "Test Email da GraphService - testSendAlert - "
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        String title = "Alert di test";

        String bodyHtml = "<h1>Ciao!</h1><p>Questa è una mail di test inviata realmente da GraphService.</p>";

        // Assicuriamoci che l'invio non generi eccezioni
        assertDoesNotThrow(() -> mailService.sendAlert(recipient, subject, title, bodyHtml));
    }

    @Test
    void testSendMailObject() {

        String sender = "l.lione@rmt.srl";

        String subject = "Test Email da GraphService - testSendMailObject - "
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        String bodyHtml = "<h1>Ciao!</h1><p>Questa è una mail di test inviata realmente da GraphService.</p>";

        String recipient = "l.lione@rmt.srl";

        List<String> ccList = List.of("lorenzo.lione@gmail.com");

        List<Path> attachments = List.of(
                Path.of("src/test/resources/attach1.jpg"),
                Path.of("src/test/resources/attach2.jpg"),
                Path.of("src/test/resources/attach3.docx"));

        // Creiamo un oggetto MailObject
        MailObject mailObject = new MailObject(sender, recipient, subject, null, bodyHtml, ccList, attachments, true);

        assertDoesNotThrow(() -> mailService.sendMail(mailObject));
    }
}
