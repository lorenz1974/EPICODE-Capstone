package capstone.be.configmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConfigDeserializer {

    private final ObjectMapper objectMapper;

    public Object deserialize(ConfigVariable configVariable) {
        String type = configVariable.getType();
        String value = configVariable.getValue();

        try {
            log.debug("Deserializing config '{}': Type={}, Value={}", configVariable.getVariableName(), type, value);

            // Jackson required tto deserialize objects
            Class<?> clazz = Class.forName(type);
            return objectMapper.readValue(value, clazz);

        } catch (ClassNotFoundException e) {
            log.error("Class not found: {}. Ensure the full package path is stored in the database!", type);
            throw new RuntimeException("Unknown class type: " + type, e);
        } catch (Exception e) {
            log.error("Failed to deserialize config '{}' (type: {}): {}", configVariable.getVariableName(), type,
                    e.getMessage(), e);
            throw new RuntimeException("Failed to deserialize configuration value", e);
        }
    }
}
