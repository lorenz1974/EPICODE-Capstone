package capstone.be;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.github.javafaker.Faker;

import capstone.be.appuser.AppUser;
import capstone.be.appuser.AppUserRepository;
import capstone.be.appuser.AppUserService;
import capstone.be.config.FakerConfig;
import capstone.be.doctemplate.DocTemplate;
import capstone.be.doctemplate.DocTemplateRepository;
import capstone.be.doctemplate.DocTemplateService;
import jakarta.transaction.Transactional;

@DataJpaTest
@Import({ AppUserService.class, DocTemplateService.class, FakerConfig.class })
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@Transactional
class DeleteElements {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private DocTemplateService docTemplateService;

    @Autowired
    private DocTemplateRepository docTemplateRepository;

    @Autowired
    private Faker faker;

    @Test
    void testDeleteAppUser() {

        long count = appUserRepository.count();
        long userId = faker.number().numberBetween(4, count);
        AppUser appUser = appUserService.getUserById(userId);
        appUserRepository.delete(appUser);
        assertThrows(Exception.class, () -> appUserService.getUserById(userId));
    }

    @Test
    void testDeleteDocTemplate() {
        long count = docTemplateRepository.count();
        long docTemplateId = faker.number().numberBetween(1, count);
        DocTemplate docTemplate = docTemplateService.getDocTemplateById(docTemplateId);
        docTemplateRepository.delete(docTemplate);
        assertThrows(Exception.class, () -> docTemplateService.getDocTemplateById(docTemplateId));
    }
}