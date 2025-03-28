package capstone.be.utility;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
// Enables Spring support for JUnit 5
@ExtendWith(SpringExtension.class)
// Loads only the required configuration
@ContextConfiguration(classes = EnvelopePollResponseDynamicObjectTest.TestConfig.class)
public class EnvelopePollResponseDynamicObjectTest {

    @Autowired
    private DynamicJsonParser dynamicJsonParser;

    //
    // Minimal Spring configuration for the test
    //
    @Configuration
    static class TestConfig {
        @Bean
        public DynamicJsonParser dynamicJsonParser() {
            return new DynamicJsonParser();
        }
    }

    //
    // Test for a successful API response using a dynamic object
    //
    @Test
    public void shouldDeserializeSuccessResponseDynamically() throws Exception {
        String jsonResponse = """
                {
                  "Id": "d3bb33e2-36a4-43b1-9178-537e5c3fc875",
                  "EnvelopeStatus": "Completed",
                  "Name": "TEST API01 - Documento",
                  "SentDate": "2025-03-11T09:25:11+00:00"
                }
                """;

        log.info("Testing successful API response deserialization...");

        //
        // Generate dynamic class and deserialize the JSON into it
        //
        Object dynamicInstance = dynamicJsonParser.parseJsonToClass("EnvelopePollResponse", jsonResponse);

        //
        // Validate that the instance is created correctly
        //
        assertThat(dynamicInstance).isNotNull();
        log.info("Dynamic instance created successfully: {}", dynamicInstance.getClass().getSimpleName());

        //
        // Validate getter methods using reflection
        //
        assertThat(dynamicJsonParser.getValue(dynamicInstance, "getId"))
                .isEqualTo("d3bb33e2-36a4-43b1-9178-537e5c3fc875");
        assertThat(dynamicJsonParser.getValue(dynamicInstance, "getEnvelopeStatus")).isEqualTo("Completed");
        assertThat(dynamicJsonParser.getValue(dynamicInstance, "getName")).isEqualTo("TEST API01 - Documento");
        assertThat(dynamicJsonParser.getValue(dynamicInstance, "getSentDate")).isNotNull();

        log.info("All getter methods validated successfully.");

        //
        // Check that getFieldNames contains all expected properties
        //
        List<String> fieldNames = (List<String>) dynamicJsonParser.getValue(dynamicInstance, "getFieldNames");
        assertThat(fieldNames).containsExactlyInAnyOrder("Id", "EnvelopeStatus", "Name", "SentDate");

        log.info("Field names validation successful: {}", fieldNames);
    }

    //
    // Test for an error API response using a dynamic object
    //
    @Test
    public void shouldDeserializeErrorResponseDynamically() throws Exception {
        String errorResponse = """
                {
                  "ErrorId": "ERR0007",
                  "Message": "ERR0007 - Envelope not available. - [EnvelopeId: d3bb33e2-36a4-43b1-9178-537e5c3fc876]",
                  "TraceId": "5bfbc692-e3f4-4f69-b661-9a58859cd92e"
                }
                """;

        log.info("Testing error API response deserialization...");

        //
        // Generate dynamic class and deserialize the JSON into it
        //
        Object dynamicInstance = dynamicJsonParser.parseJsonToClass("EnvelopePollErrorResponse", errorResponse);

        //
        // Validate that the instance is created correctly
        //
        assertThat(dynamicInstance).isNotNull();
        log.info("Dynamic instance created successfully: {}", dynamicInstance.getClass().getSimpleName());

        //
        // Validate getter methods using reflection
        //
        assertThat(dynamicJsonParser.getValue(dynamicInstance, "getErrorId")).isEqualTo("ERR0007");
        assertThat(dynamicJsonParser.getValue(dynamicInstance, "getMessage"))
                .isEqualTo("ERR0007 - Envelope not available. - [EnvelopeId: d3bb33e2-36a4-43b1-9178-537e5c3fc876]");

        log.info("All getter methods validated successfully.");

        //
        // Check that getFieldNames contains all expected properties
        //
        List<String> fieldNames = (List<String>) dynamicJsonParser.getValue(dynamicInstance, "getFieldNames");
        assertThat(fieldNames).containsExactlyInAnyOrder("ErrorId", "Message", "TraceId");

        log.info("Field names validation successful: {}", fieldNames);
    }
}
