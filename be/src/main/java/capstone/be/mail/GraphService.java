package capstone.be.mail;

import capstone.be.configmanager.ConfigManager;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.*;
import com.microsoft.graph.requests.AttachmentCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@DependsOn({ "configManager" }) // Dipendenza da ConfigManager
public class GraphService {

    private final ConfigManager configManager;

    private String azureTenantId;
    private String azureSendmailClientId;
    private String azureSendmailClientSecret;

    private String appAdministratorEmail;

    private ClientSecretCredential clientSecretCredential;
    private TokenCredentialAuthProvider tokenCredentialAuthProvider;
    private GraphServiceClient graphServiceClient;

    @PostConstruct
    private void init() {
        this.azureTenantId = (String) configManager.getConfVariable("azureTenantId");
        this.azureSendmailClientId = (String) configManager.getConfVariable("azureSendmailClientId");
        this.azureSendmailClientSecret = (String) configManager.getConfVariable("azureSendmailClientSecret");
        this.appAdministratorEmail = (String) configManager.getConfVariable("appAdministratorEmail");

        log.debug("azureTenantId: {}", azureTenantId);
        log.debug("azureSendmailClientId: {}", azureSendmailClientId);
        log.debug("clientSendmailSecret: {}", azureSendmailClientSecret);
        log.debug("appAdministratorEmail: {}", appAdministratorEmail);

        this.clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(
                        azureSendmailClientId)
                .clientSecret(
                        azureSendmailClientSecret)
                .tenantId(
                        azureTenantId)
                .build();

        this.tokenCredentialAuthProvider = new TokenCredentialAuthProvider(
                List.of("https://graph.microsoft.com/.default"), clientSecretCredential);

        this.graphServiceClient = GraphServiceClient.builder()
                .authenticationProvider(tokenCredentialAuthProvider)
                .buildClient();
    }

    //
    // Method with MailObject as parameter
    //
    public MailResponse sendMail(MailObject mailObject) {
        return sendMail(
                mailObject.getSender(),
                mailObject.getRecipient(),
                mailObject.getCcList(),
                mailObject.getSubject(),
                mailObject.getBodyHtml(),
                mailObject.getAttachments(),
                mailObject.isSaveToSentItems());
    }

    //
    // Method with single recipient and no attachments
    // typically use to send notifications and alerts
    //
    public MailResponse sendMail(String sender, String recipient, String subject, String messageBody) {
        return sendMail(sender, recipient, null, subject, messageBody, null, false);
    }

    //
    // Method with all parameters
    //
    public MailResponse sendMail(String sender, String recipient, List<String> emailCcList, String subject,
            String messageBody, List<?> attachments, boolean saveToSentItems) {
        log.debug("Preparazione dell'email per {} con oggetto '{}'", recipient, subject);

        Message message = new Message();
        message.subject = subject;

        // Corpo del messaggio
        ItemBody body = new ItemBody();
        body.contentType = BodyType.HTML;
        body.content = messageBody;
        message.body = body;

        // Destinatario principale
        message.toRecipients = Collections.singletonList(createRecipient(recipient));

        // Destinatari in CC
        if (emailCcList != null && !emailCcList.isEmpty()) {
            message.ccRecipients = createRecipientsList(emailCcList);
        }

        // Gestione allegati
        if (attachments != null && !attachments.isEmpty()) {
            List<Attachment> attachmentList = new ArrayList<>();
            if (attachments.get(0) instanceof Path) {
                for (Path filePath : (List<Path>) attachments) {
                    try {
                        byte[] fileBytes = Files.readAllBytes(filePath);
                        FileAttachment fileAttachment = new FileAttachment();
                        fileAttachment.name = filePath.getFileName().toString();
                        fileAttachment.contentBytes = fileBytes;
                        fileAttachment.oDataType = "#microsoft.graph.fileAttachment";
                        attachmentList.add(fileAttachment);
                        log.debug("Aggiunto allegato: {}", fileAttachment.name);
                    } catch (Exception e) {
                        log.error("Errore nel caricamento dell'allegato {}: {}", filePath, e.getMessage(), e);
                    }
                }
            } else if (attachments.get(0) instanceof File) {
                for (File file : (List<File>) attachments) {
                    try {
                        byte[] fileBytes = Files.readAllBytes(file.toPath());
                        FileAttachment fileAttachment = new FileAttachment();
                        fileAttachment.name = file.getName();
                        fileAttachment.contentBytes = fileBytes;
                        fileAttachment.oDataType = "#microsoft.graph.fileAttachment";
                        attachmentList.add(fileAttachment);
                        log.debug("Aggiunto allegato: {}", fileAttachment.name);
                    } catch (Exception e) {
                        log.error("Errore nel caricamento dell'allegato {}: {}", file.getName(), e.getMessage(), e);
                    }
                }
            }
            // Conversione in AttachmentCollectionPage
            message.attachments = new AttachmentCollectionPage(attachmentList, null);
        }

        try {
            graphServiceClient.users(appAdministratorEmail)
                    .sendMail(UserSendMailParameterSet
                            .newBuilder()
                            .withMessage(message)
                            .withSaveToSentItems(saveToSentItems)
                            .build())
                    .buildRequest()
                    .post();

            log.info("Email sent succefully to: {}", recipient);
        } catch (Exception e) {
            log.error("Error sending email to {}: {}", recipient, e.getMessage(), e);
            throw new IllegalStateException("Error sending email to " + recipient + ": " + e.getMessage());
        }

        //
        // If everything went well, return a positive response
        //
        MailResponse mailResponse = new MailResponse();
        mailResponse.setMessage("Mail sent successfully via Graph to: " + recipient);
        mailResponse.setError(false);
        mailResponse.setStatus("200");

        return mailResponse;
    }

    private Recipient createRecipient(String emailAddress) {
        Recipient recipient = new Recipient();
        EmailAddress email = new EmailAddress();
        email.address = emailAddress;
        recipient.emailAddress = email;
        return recipient;
    }

    private List<Recipient> createRecipientsList(List<String> emailAddresses) {
        List<Recipient> recipients = new LinkedList<>();
        for (String email : emailAddresses) {
            recipients.add(createRecipient(email));
        }
        return recipients;
    }
}
