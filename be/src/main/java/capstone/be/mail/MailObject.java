package capstone.be.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@Slf4j
public class MailObject {
    private String sender;
    private String recipient;
    private String subject;
    private String style;
    private String body;
    private List<String> ccList = new ArrayList<>();
    private List<?> attachments = new ArrayList<>();
    private boolean saveToSentItems = false;

    public String getBodyHtml() {

        StringBuilder bodyHtml = new StringBuilder();

        // Start the body
        bodyHtml.append(
                "<!DOCTYPE html>\n" +
                        " <html>\n" +
                        "    <style>\n" +
                        "       .content {\n" +
                        "           width: 85%;\n" +
                        "           margin: 0 auto;\n" +
                        "       }\n" +
                        "       " + style + "\n" +
                        "    </style>\n" +
                        "    <body>\n" +
                        "       <div class=\"content\">" + body + "</div>\n" +
                        " </body>\n" +
                        " </html>");

        log.info("HTML Body: {}", bodyHtml.toString());

        // Return the HTML body
        return bodyHtml.toString();
    }

    public List<?> getAttachments() {

        List<?> attachmentsList = new ArrayList<>();

        // Check if attachments list is not empty
        if (attachments != null && !attachments.isEmpty()) {
            // Check if the first element is an instance of Path
            if (attachments.get(0) instanceof Path) {
                // Filter and cast to Path
                attachmentsList = attachments.stream()
                        .filter(Path.class::isInstance)
                        .map(Path.class::cast)
                        .toList();
                // Check if the first element is an instance of File
            } else if (attachments.get(0) instanceof File) {
                // Filter and cast to File
                attachmentsList = attachments.stream()
                        .filter(File.class::isInstance)
                        .map(File.class::cast)
                        .toList();
                // Check if the first element is an instance of String
            } else if (attachments.get(0) instanceof String) {
                attachmentsList = attachments.stream()
                        // Filter and cast to String
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        // .. and then map to Path
                        .map(Path::of)
                        .toList();
            }
        }

        // Return the list of attachments of, if attachments is empty, return an empty
        // list
        return attachmentsList;
    }
}
