package capstone.be.datacreationrunners;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import capstone.be.doctemplate.DocTemplate;
import capstone.be.doctemplate.DocTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Order(2)
@Component
@RequiredArgsConstructor
@Slf4j
@DependsOn("configManager")
@Transactional
public class DocTemplateRunner implements CommandLineRunner {

    private final DocTemplateRepository docTemplateRepository;

    @Override
    public void run(String... args) throws Exception {

        if (docTemplateRepository.count() > 2) {
            log.info("Doc templates already created.");
            return;
        }

        log.info("Creating doc templates...");

        //
        // Array of templates to be created
        // name, subject, templateClass
        //
        String rndm = new Random().nextInt(1000) + "";

        // Template 1
        DocTemplate template1 = new DocTemplate();
        template1.setName("Documento Generico (" + rndm + ")");
        template1.setSubject("Richiesta firma per documento generico di {{signername}} {{signersurname}}");
        template1.setTemplateJson(readJsonTemplate("genericDocument"));
        template1.setTemplateSqlQuery(readSqlTemplate("genericDocument"));
        template1.setTemplateProvider("Namirial");
        template1.setTemplateProviderApiEndpoint("https://demo.esignanywhere.net/api/v6/envelope/send");
        template1.setTemplateApiCrudType("POST");
        template1.setTemplateApiCrudCheckEndpoint("https://demo.esignanywhere.net/api/v6/envelope/{{signRequest.Id}}");
        template1.setTemplateApiCrudCheckType("GET");
        template1.setTemplateApiCrudCheckStatusField("EnvelopeStatus");
        template1.setTemplateProviderApiEndpointIdField("EnvelopeId");
        template1.setTemplateApiCrudCheckWaitingValues(Set.of("PENDING", "WAITING", "ACTIVE"));
        template1.setTemplateApiCrudCheckCompletedValues(Set.of("COMPLETED", "OK", "SIGNED"));
        template1.setTemplateApiCrudCheckFailedValues(Set.of("FAILED", "KO", "NOT OK", "ERROR", "REJECTED"));
        template1.setTemplateApiCrudErrorField("Message");
        template1.setTemplateApiCrudErrorValues(Set.of("ERR", "KO", "ERROR"));

        // Template 2
        DocTemplate template2 = new DocTemplate();
        BeanUtils.copyProperties(template1, template2);
        template2.setName("Informativa Privacy (" + rndm + ")");
        template2.setSubject("Richiesta firma per Informativa Privacy di {{signername}} {{signersurname}}");
        template2.setTemplateApiCrudCheckFailedValues(Set.of("FAILED", "ERROR"));

        // Template 3
        DocTemplate template3 = new DocTemplate();
        BeanUtils.copyProperties(template1, template3);
        template3.setName("Contratto di Servizio (" + rndm + ")");
        template3.setSubject("Richiesta firma per Contratto di Servizio di {{signername}} {{signersurname}}");
        template3.setTemplateApiCrudErrorValues(Set.of("ERR", "CICABOOMMMMM"));

        // Save the templates
        docTemplateRepository.save(template1);
        docTemplateRepository.save(template2);
        docTemplateRepository.save(template3);
    }

    private String readJsonTemplate(String templateName) {
        try {
            String filePath = "src\\main\\resources\\filesForTemplates\\" + templateName + ".json";
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String readSqlTemplate(String templateName) {
        try {
            String filePath = "src\\main\\resources\\filesForTemplates\\" + templateName + ".sql";
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}