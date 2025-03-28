package capstone.be.datacreationrunners;

import java.io.IOException;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.github.javafaker.Faker;

import capstone.be.appuser.AppUser;
import capstone.be.appuser.AppUserRepository;
import capstone.be.appuser.AppUserRole;
import capstone.be.appuser.AppUserService;
import capstone.be.basesalary.BaseSalary;
import capstone.be.branch.Branch;
import capstone.be.city.City;
import capstone.be.city.CityService;
import capstone.be.company.Company;
import capstone.be.company.CompanyService;
import capstone.be.contract.Contract;
import capstone.be.contract.ContractService;
import capstone.be.doctemplate.DocTemplate;
import capstone.be.doctemplate.DocTemplateRepository;
import capstone.be.jobprofile.JobProfile;
import capstone.be.jobprofile.JobProfileRepository;
import capstone.be.jobprofile.JobProfileRequest;
import capstone.be.jobprofile.JobProfileService;
import capstone.be.jobprofile.JobProfileType;
import capstone.be.utility.FakeFiscalCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Order(3)
@Component
@RequiredArgsConstructor
@Slf4j
@DependsOn("configManager")
@Transactional
public class AppUserRunner implements CommandLineRunner {

    private final Faker faker;
    private final AppUserService appUserService;
    private final AppUserRepository appUserRepository;
    private final JobProfileService jobProfileService;
    private final CompanyService companyService;
    private final ContractService contractService;
    private final DocTemplateRepository templatesRepository;
    private final CityService cityService;
    private final FakeFiscalCode fakeFiscalCode;

    @Value("${APP_ADMINISTRATOR_NAME}")
    private String adminName;
    @Value("${APP_ADMINISTRATOR_EMAIL}")
    private String adminEmail;

    @Override
    public void run(String... args) throws Exception, IllegalArgumentException {

        //
        // Retrieve all the templates for all the users
        //
        final List<DocTemplate> allowedTemplates = templatesRepository.findAll();

        allowedTemplates.stream().forEach(t -> {
            log.debug("Template: {}", t);
        });

        if (appUserRepository.count() < 14) {
            //
            //
            //
            //
            //
            log.info("Creating SuperAdmin user...");
            AppUser superAdmin = createFakeUser(Set.of(AppUserRole.ROLE_ADMIN), allowedTemplates);
            superAdmin.setName(
                    adminName.substring(0, adminName.indexOf(" ")));
            superAdmin.setSurname(
                    adminName.substring(adminName.indexOf(" ") + 1));
            superAdmin.setEmail(adminEmail);
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword("SuperAdmin.1");
            superAdmin.setCellphone("+393478595000");
            superAdmin.setSex("M");
            //
            finishFakeUser(superAdmin);

            //
            //
            //
            //
            //
            log.info("Creating 3 admin users...");
            for (int i = 0; i < 3; i++) {
                AppUser admin = createFakeUser(Set.of(AppUserRole.ROLE_ADMIN), allowedTemplates);
                finishFakeUser(admin);
            }

            //
            //
            //
            //
            //
            log.info("Creating 3 operator users...");
            for (int i = 0; i < 3; i++) {
                AppUser operator = createFakeUser(Set.of(AppUserRole.ROLE_OPERATOR), allowedTemplates);
                finishFakeUser(operator);
            }

            //
            //
            //
            //
            //
            log.info("Creating 3 secretary users...");
            for (int i = 0; i < 3; i++) {
                AppUser secretary = createFakeUser(Set.of(AppUserRole.ROLE_SECRETARY), allowedTemplates);
                finishFakeUser(secretary);
            }

            //
            //
            //
            //
            //
            log.info("Creating 3 operator&secretary users...");
            for (int i = 0; i < 3; i++) {
                AppUser operatorSecretary = createFakeUser(
                        Set.of(AppUserRole.ROLE_OPERATOR, AppUserRole.ROLE_SECRETARY),
                        allowedTemplates);
                finishFakeUser(operatorSecretary);
            }
        }

        if (appUserRepository.count() > 250) {
            log.info("Application users already created.");
            return;
        }

        log.info("Creating application users...");

        log.info("Creating 250 default users...");
        for (int i = 0; i < 250; i++) {
            AppUser user = createFakeUser(Set.of(AppUserRole.ROLE_USER), allowedTemplates);
            user.setPassword("UserUser.1");
            // Disable all the standard users
            user.setEnabled(true);
            finishFakeUser(user);
        }

        log.info("Application users created successfully.");
    }

    private AppUser createFakeUser(Set<AppUserRole> roles, List<DocTemplate> allowedTemplates) throws IOException {
        AppUser newUser = new AppUser();
        String name = faker.name().firstName();
        String surname = faker.name().lastName();
        String userName = (name + "_" + surname).replace(" ", "");

        String email = name.replace(" ", "") +
                "." +
                surname.replace(" ", "") +
                "@" +
                faker.internet().domainName().replace(" ", "");

        newUser.setUsername(userName);
        newUser.setName(name);
        newUser.setSurname(surname);

        newUser.setSex(faker.random().nextBoolean() ? "F" : "M");

        // Se null viene generata una password casuale in fase di registrazione
        // dell'utente
        newUser.setPassword(null);

        // Gli utenti fake hanno un fatherId di 0 di default
        newUser.setFatherId(1L);

        newUser.setFiscalcode(fakeFiscalCode.generate());
        newUser.setBirthDate(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        newUser.setNationality("Italia");

        City birthCityState = new City();
        // Alcune città non hanno la provincia, quindi ripetiamo la ricerca finché non
        // troviamo una città con provincia
        do {
            birthCityState = cityService.findRandomCity();
        } while (birthCityState.getProvince() == null);

        newUser.setBirthPlace(birthCityState.getCityState());
        newUser.setBirthProvince(birthCityState.getProvince());

        City livingCityState = new City();
        // Alcune città non hanno la provincia, quindi ripetiamo la ricerca finché non
        // troviamo una città con provincia
        do {
            livingCityState = cityService.findRandomCity();
        } while (livingCityState.getProvince() == null);
        newUser.setZipCode(
                livingCityState.getZipCode() != null ? livingCityState.getZipCode() : faker.address().zipCode());
        newUser.setLivingCity(livingCityState.getCityState());
        newUser.setLivingProvince(livingCityState.getProvince());
        newUser.setAddress(faker.address().streetAddress());

        newUser.setPhone(faker.phoneNumber().phoneNumber());
        newUser.setCellphone(faker.phoneNumber().cellPhone());
        newUser.setEmail(email);
        newUser.setNotes(faker.lorem().sentence());

        newUser.setRoles(roles);

        newUser.setDocTemplatesAllowed(allowedTemplates);

        return newUser;

    }

    private AppUser finishFakeUser(AppUser appUser) throws Exception, IOException {

        AppUser savedAppUser = null;

        try {
            savedAppUser = appUserService.saveUser(appUser, true);

            JobProfileRequest jobProfileRequest = createFakeJobProfile(savedAppUser);

            JobProfile savedJobProfile = jobProfileService.createJobProfile(jobProfileRequest);

            // appUser.setJobProfile(savedJobProfile);

        } catch (Exception e) {
            log.error("Error finishing user: {}", e.getMessage());
        }

        return savedAppUser;
    }

    private JobProfileRequest createFakeJobProfile(AppUser appUser) {
        JobProfileRequest jobProfile = new JobProfileRequest();

        long appUserId = appUser.getId();

        log.info("Creating fake job profile for user ID: {}", appUserId);

        jobProfile.setAppUserId(appUserId);

        //
        // Create the fake Job Profile for the fake AppUser
        //
        Company fakeCompany = companyService.findRandomCompany();
        Branch fakeBranch = fakeCompany.getBranches().get(faker.random().nextInt(fakeCompany.getBranches().size()));
        Contract fakeContract = contractService.findRandomContract(fakeCompany.getSector().getId());
        BaseSalary fakeBaseSalary = fakeContract.getBaseSalaries()
                .get(faker.random().nextInt(fakeContract.getBaseSalaries().size()));

        jobProfile.setCompanyId(fakeCompany.getId());
        jobProfile.setBranchId(fakeBranch.getId());
        jobProfile.setContractId(fakeContract.getId());
        jobProfile.setBaseSalaryId(fakeBaseSalary.getId());

        int fakeHours = faker.random().nextInt(1, 5) * 8;
        jobProfile.setHoursPerWeek(fakeHours);

        jobProfile.setJobProfileType(JobProfileType.values()[faker.random().nextInt(JobProfileType.values().length)]);

        jobProfile.setStartDate(faker.date().past(3650, java.util.concurrent.TimeUnit.DAYS).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate());

        // jobProfile.setEndDate(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        jobProfile.setJobDescription(faker.lorem().sentence());

        return jobProfile;
    }
}