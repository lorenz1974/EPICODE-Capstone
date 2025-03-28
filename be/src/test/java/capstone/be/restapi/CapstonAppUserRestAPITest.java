package capstone.be.restapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.BeanUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

import capstone.be.appuser.AppUserJSON;
import capstone.be.appuser.AppUserRegistrationRequest;
import capstone.be.jobprofile.JobProfileRequest;
import capstone.be.jobprofile.JobProfileType;

@Slf4j
@ExtendWith(SpringExtension.class) // Enable Spring support for JUnit 5
@ContextConfiguration(classes = CapstonAppUserRestAPITest.TestConfig.class) // Load only necessary beans
@JsonIgnoreProperties(ignoreUnknown = true)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CapstonAppUserRestAPITest {

    @Autowired
    private RestApiClient restApiClient;

    //
    // Minimal Spring configuration for the test
    //
    @Configuration
    static class TestConfig {

        @Bean
        public WebClient webClient() {
            return WebClient.builder().build(); // versione "basic" per i test
        }

        @Bean
        public RestApiClient restApiClient(WebClient webClient) {
            return new RestApiClient(webClient);
        }
    }

    private static String token;
    private static AppUserJSON createdUser;
    private static AppUserJSON retrievedUser;
    private static long fakeUserId;

    @BeforeAll
    static void setup(@Autowired RestApiClient restApiClient) {
        authenticateUser(restApiClient, "superadmin", "SuperAdmin.1");

        log.info("----- First of all try to delete the Fake User if it exists because of previous test runs");
        ArrayList<String> fakeUsernames = new ArrayList<>(Arrays.asList("fakeuser", "fakeuser_updated"));
        for (String username : fakeUsernames) {
            try {
                log.info("Searching user: {}...", username);
                String searchUrl = "http://localhost:8080/api/users/" + username;
                String searchResponse = restApiClient.getDataFromApi(searchUrl, token);

                if (searchResponse != null) {
                    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
                    AppUserJSON user = objectMapper.readValue(searchResponse, AppUserJSON.class);
                    Long userId = user.getId();

                    log.info("Deleting user with ID: {}...", userId);
                    String deleteUrl = "http://localhost:8080/api/users/" + userId + "?forceDeletion=true";
                    String deleteResponse = restApiClient.deleteDataFromApi(deleteUrl, token);
                    log.info("DELETE Response for {}: {}", username, deleteResponse);
                } else {
                    log.info("User {} not found", username);
                }
            } catch (Exception e) {
                log.info("User {} doesn't exist or cannot be deleted", username);
            }
        }
    }

    @Test
    @Order(1)
    void checkUserToken() {
        assertNotNull(token, "Token is null and the user is not authenticated");
    }

    // @Test
    // @Order(2)
    // void test3rdPartyPostRestApi(@Autowired RestApiClient restApiClient) {
    // String url = "https://api.restful-api.dev/objects";
    // String requestBody = """
    // {"name": "Apple iPad Air",
    // "data": {
    // "Generation": "4th",
    // "Price": "519.99",
    // "Capacity": "256 GB" }
    // }
    // """;

    // String response = restApiClient.postDataToApi(url, requestBody, null);
    // log.info("Response: {}", response);

    // assertNotNull(response, "Response is not null");
    // }

    @Test
    @Order(2)
    void testGetMe() {
        String url = "http://localhost:8080/api/users/me";

        try {
            String response = restApiClient.getDataFromApi(url, token);
            log.info("TEST testGetMe() - response: {}", response);
            assertNotNull(response, "GET response is null");
        } catch (Exception e) {
            log.error("Error during GET request", e);
        }
    }

    @Test
    @Order(3)
    void crudOnFakeAppUser() throws JsonMappingException, JsonProcessingException {

        String url = "http://localhost:8080/api/";

        log.info("----- Create Fake User");

        url = "http://localhost:8080/api/users/register";
        AppUserRegistrationRequest appUser = new AppUserRegistrationRequest();
        appUser.setUsername("fakeuser");
        appUser.setName("Fake");
        appUser.setSurname("User");
        appUser.setSex("F");
        appUser.setFiscalcode("FAKUSR01A01H501A");
        appUser.setBirthDate(LocalDate.parse("1990-01-01"));
        appUser.setNationality("Italia");
        appUser.setBirthProvince("Padova");
        appUser.setBirthPlace("Abano Terme");
        appUser.setLivingProvince("Torino");
        appUser.setLivingCity("Abbadia Alpina");
        appUser.setAddress("123 Fake Street");
        appUser.setAddressco("c/o Fake");
        appUser.setZipCode("12345");
        appUser.setPhone("123-456-7890");
        appUser.setCellphone("098-765-4321");
        appUser.setEmail("fakeuser@example.com");
        appUser.setZipCode("54321");
        appUser.setEmail("fakeuser@mail.com");
        appUser.setNotes("This is a fake user for testing purposes.");

        JobProfileRequest dto = new JobProfileRequest();
        dto.setCompanyId(2506L);
        dto.setBranchId(4090L);
        dto.setContractId(1L);
        dto.setBaseSalaryId(1L);
        dto.setJobProfileType(JobProfileType.FULL_TIME);
        dto.setHoursPerWeek(40);

        appUser.setJobProfile(dto);

        String response = restApiClient.postDataToApi(url, appUser, token);
        log.info("POST Response: {}", response);
        assertNotNull(response, "Response is not null");

        // Convert response to a new AppUser object do a specific DTO
        try {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            createdUser = objectMapper.readValue(response, AppUserJSON.class);

            // Log the created user's details
            log.info("Created User: {}", createdUser);
        } catch (JsonMappingException e) {
            log.error("Error mapping JSON to AppUserJSON", e);
        }
        assertNotNull(createdUser, "Created user is null");

        // Extract the ID from the response
        fakeUserId = createdUser.getId();
        log.info("Post Fake User ID: {}", fakeUserId);
        assertNotNull(fakeUserId, "Fake user ID is null");

        //
        // Retrieve the fake user
        //
        log.info("----- Retrieve Fake User");

        log.info("Get Fake User ID: {}", fakeUserId);
        url = "http://localhost:8080/api/users/" + fakeUserId;

        response = restApiClient.getDataFromApi(url, token);
        assertNotNull(response, "GET response is null");

        // Convert response to a new AppUser object do a specific DTO
        try {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            retrievedUser = objectMapper.readValue(response, AppUserJSON.class);

            // Log the retrieved user's details
            log.info("Retrieved User: {}", retrievedUser);
        } catch (JsonMappingException e) {
            log.error("Error mapping JSON to AppUserJSON", e);
        }
        assertNotNull(retrievedUser, "Retrieved user is null");
        // Compare name and surname of createdUser and retrievedUser
        String createdUserNameSurname = createdUser.getNameSurname();
        String retrievedUserNameSurname = retrievedUser.getNameSurname(); // update method call to getNameSurname
        assertEquals(createdUserNameSurname, retrievedUserNameSurname,
                "Name and Surname do not match between created and retrieved user");
        //
        // Update the fake user
        //
        log.info("----- Update Fake User");

        log.info("Get Fake User ID: {}", fakeUserId);
        url = "http://localhost:8080/api/users/" + fakeUserId;

        AppUserJSON updatedUser = new AppUserJSON();
        BeanUtils.copyProperties(retrievedUser, updatedUser);
        updatedUser.setName("Updated");
        updatedUser.setSurname("UserTest");
        updatedUser.setUsername(updatedUser.getUsername() + "_updated");

        response = restApiClient.putDataToApi(url, updatedUser, token);
        assertNotNull(response, "Response is null after update");

        // Convert response to a new AppUser object to a specific DTO
        AppUserJSON checkUser = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            checkUser = objectMapper.readValue(response, AppUserJSON.class);

            // Log the retrieved user's details
            log.info("Updated User: {}", checkUser);
        } catch (JsonMappingException e) {
            log.error("Error mapping JSON to AppUserJSON", e);
        }
        assertNotNull(checkUser, "CheckUser is null after update");
        // Compare name and surname of createdUser and retrievedUser
        assertEquals("Updated",
                checkUser.getName(), "Name is not updated");
        assertEquals("UserTest",
                checkUser.getSurname(), "Surname is not updated");
        assertEquals(checkUser.getUsername(), "fakeuser_updated",
                "Username is not updated");

        //
        // Delete the fake user
        //
        log.info("----- Delete Fake User");
        log.info("Delete Fake User ID: {}", fakeUserId);

        url = "http://localhost:8080/api/users/" + fakeUserId + "?forceDeletion=true";
        response = restApiClient.deleteDataFromApi(url, token);
        assertNull(response, "DELETE response is NOT null");
    }

    static boolean authenticateUser(@Autowired RestApiClient restApiClient, String username, String password) {
        String url = "http://localhost:8080/api/users/authentication";
        String requestBody = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";
        try {
            String response = restApiClient.postDataToApi(url, requestBody, null);
            assertNotNull(response, "Authentication response is null");

            // Extract token from response
            token = extractTokenFromResponse(response);
            assertNotNull(token, "Token is null");

            // Print the token to the console
            log.info("Authentication token: " + token);
        } catch (Exception e) {
            log.error("Error during authentication", e);
            return false;
        }
        return true;
    }

    private static String extractTokenFromResponse(String response) {
        int tokenStartIndex = response.indexOf("\"token\":\"") + 9;
        int tokenEndIndex = response.indexOf("\"", tokenStartIndex);
        return response.substring(tokenStartIndex, tokenEndIndex);
    }

}
