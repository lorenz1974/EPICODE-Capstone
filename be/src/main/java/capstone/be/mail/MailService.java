package capstone.be.mail;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    private final GraphService graphService;
    private final MailObject mailObject;

    public MailResponse sendMail(MailObject mailObject) {
        return graphService.sendMail(mailObject);
    }

    public MailResponse sendAlert(String recipient, String subject, String title, String messageBody) {

        String newSubject = "ALERT: " + subject;

        String style = getStyle("alert");

        String body = "<div class=\"alert-title\">" + title + "</div>" +
                "<div class=\"alert-body\">" + messageBody + "</div>";

        return graphService.sendMail(new MailObject(null, recipient, newSubject, style, body, null, null, false));
    }

    public MailResponse sendNotification(String recipient, String subject, String title, String messageBody) {

        String newSubject = "NOTIFICATION: " + subject;

        String style = getStyle("notification");

        String body = "<div class=\"notification-title\">" + title + "</div>" +
                "<div class=\"notification-body\">" + messageBody + "</div>";

        return graphService.sendMail(new MailObject(null, recipient, newSubject, style, body, null, null, false));
    }

    public List<MailResponse> sendBulkMail(List<MailObject> mailObjects) {

        return mailObjects.stream()
                .map(graphService::sendMail)
                .toList();
    }

    private String getStyle(String type) {

        String bgColor = "";
        String textColor = "";

        switch (type) {
            case "alert":
                bgColor = " #f44336";
                textColor = "white";
                break;
            case "notification":
                bgColor = "rgb(190, 244, 54)";
                textColor = "black";
                break;
            default:
                bgColor = "white";
                break;
        }

        return "." + type + "-title {" +
                "   background-color:" + bgColor + ";\n" +
                "   text-align: center;\n" +
                "   font-size: 1.5em;\n" +
                "   font-weight: bold;\n" +
                "   color: " + textColor + ";\n" +
                "   padding: 20px;\n" +
                "   margin-bottom: 2em;\n" +
                "}\n" +
                "." + type + "-body {\n" +
                "   background-color: white;\n" +
                "   color: black;\n" +
                "   padding: 20px;\n" +
                "}\n";
    }
}