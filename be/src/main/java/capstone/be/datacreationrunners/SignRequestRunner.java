package capstone.be.datacreationrunners;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import capstone.be.restapi.RestApiClient;
import capstone.be.signrequest.SignRequestRepository;
import capstone.be.signrequest.SignRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Order(4)
@Component
@RequiredArgsConstructor
@Slf4j
@DependsOn("configManager")
public class SignRequestRunner implements CommandLineRunner {

    private final SignRequestRepository signRequestRepository;
    private final RestApiClient restApiClient;
    private static String token;
    private final Random random = new Random();

    @Override
    public void run(String... args) {

        if (signRequestRepository.count() > 200) {
            log.info("Sign requests already exist in the database. Skipping SignRequestRunner...");
            return;
        }

        log.info("Running SignRequestRunner...");

        // Authenticate the SuperUser
        authenticateUser(restApiClient, "superadmin", "SuperAdmin.1");

        for (int i = 0; i < 250; i++) {
            // Create a random number of sign requests
            int createdByUserId = random.nextInt(15) + 1;
            int signedByUserId = random.nextInt(260) + 1;
            int docTemplateId = random.nextInt(3) + 1;

            log.info("Creating sign request with createdByUserId: " + createdByUserId + ", signedByUserId: "
                    + signedByUserId + ", docTemplateId: " + docTemplateId);

            String requestJson = "" +
                    "{" +
                    "\"signedByAppUserId\": " + signedByUserId + "," +
                    "\"createdByAppUserId\": " + createdByUserId + "," +
                    "\"docTemplateId\": " + docTemplateId +
                    " }";
            try {
                // Send the sign request to the API
                String response = restApiClient.postDataToApi("http://localhost:8080/api/signrequests", requestJson,
                        token);
            } catch (Exception e) {
                log.error("Error during sign request creation", e);
            }

        }

    }

    static boolean authenticateUser(@Autowired RestApiClient restApiClient, String username, String password) {
        String url = "http://localhost:8080/api/users/authentication";
        String requestBody = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";
        try {
            String response = restApiClient.postDataToApi(url, requestBody, null);
            // Extract token from response
            token = extractTokenFromResponse(response);

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
