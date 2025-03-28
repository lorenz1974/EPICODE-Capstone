package capstone.be.utility;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Utility class to convert objects to JSON strings.
 * If the input is already a String, it returns it as-is.
 */
@Component
public class ObjectToJSONConverter {

    // Register JavaTimeModule to handle LocalDate and LocalDateTime
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * Converts an object to a JSON string. If the input is a String, returns it
     * directly.
     *
     * @param input The object to convert (can be any type, including null)
     * @return JSON string representation, or the original string if input is a
     *         String
     * @throws JsonProcessingException if JSON serialization fails
     */
    public static String convert(Object input) throws JsonProcessingException {
        if (input instanceof String) {
            return (String) input;
        } else {
            return objectMapper.writeValueAsString(input);
        }
    }
}