package capstone.be.configmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigManagerService {

    private final ConfigVariableRepository configVariableRepository;
    private final ConfigDeserializer configDeserializer;
    private final ObjectMapper objectMapper;
    private final Map<String, Object> configCache = new ConcurrentHashMap<>();

    // @PostConstruct
    // public void loadConfigFromDB() {
    // log.info("Loading configuration variables from the database...");
    // loadConfigurations();
    // log.info("Configuration variables loaded into cache.");
    // }

    public Object getConfVariable(String variableName) {
        log.debug("Fetching configuration variable: {}", variableName);
        return Optional.ofNullable(configCache.get(variableName))
                .orElseGet(() -> {
                    log.debug("Variable {} not found in cache, checking database.", variableName);
                    return configVariableRepository.findByVariableName(variableName)
                            .map(configDeserializer::deserialize)
                            .orElse(null);
                });
    }

    public ConfigVariable findByVariableId(Long id) {
        return configVariableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Configuration variable not found: " + id));
    }

    public ConfigVariable findByVariableName(String variableName) {
        return configVariableRepository.findByVariableName(variableName)
                .orElseThrow(() -> new EntityNotFoundException("Configuration variable not found: " + variableName));
    }

    public Page<ConfigVariable> getAllConfigurations(Pageable pageable, String query) {
        log.debug("Fetching all configuration variables with query: '{}'", query);
        return query.isEmpty() ? configVariableRepository.findAll(pageable)
                : configVariableRepository.omniSearch(query.toLowerCase(), pageable);
    }

    @Transactional
    public void setConfVariable(String variableName, Object value) {
        log.debug("Setting configuration variable: {}", variableName);
        String jsonValue;
        String type = value.getClass().getName(); // Salviamo il tipo completo della classe

        try {
            jsonValue = objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            log.error("Failed to serialize configuration value for {}: {}", variableName, e.getMessage(), e);
            throw new RuntimeException("Failed to serialize configuration value", e);
        }

        ConfigVariable configVariable = configVariableRepository.findByVariableName(variableName)
                .orElse(new ConfigVariable(variableName, jsonValue, type));

        configVariable.setValue(jsonValue);
        configVariable.setType(type);
        configCache.put(variableName, value);
    }

    public Map<String, Object> loadConfigurations() {
        log.info("Loading all configuration variables...");
        Map<String, Object> configurations = new ConcurrentHashMap<>();

        configVariableRepository.findAll().forEach(var -> {
            Object deserializedValue = configDeserializer.deserialize(var);
            configurations.put(var.getVariableName(), deserializedValue);
        });

        log.info("Loaded {} configuration variables.", configurations.size());
        return configurations;
    }

    public ConfigVariable saveConfigVariable(ConfigVariable configVariable) {
        log.debug("Saving configuration variable: {}", configVariable.getVariableName());
        if (configVariableRepository.findByVariableName(configVariable.getVariableName()).isPresent()) {
            throw new EntityExistsException(
                    "Configuration variable already exists: " + configVariable.getVariableName());
        }
        // Save the variable in the database
        ConfigVariable savedVariable = configVariableRepository.save(configVariable);
        // Put the variable in the cache
        setConfVariable(configVariable.getVariableName(), configVariable.getValue());
        // Return the saved variable
        return savedVariable;
    }

    public void deleteConfigVariable(Long id) {
        log.debug("Deleting configuration variable with id: {}", id);
        configVariableRepository.deleteById(id);
    }

    public ConfigVariable updateConfigVariable(Long id, ConfigVariable configVariable) {

        if (!configVariableRepository.existsById(id)) {
            throw new EntityNotFoundException("Configuration variable not found: " + id);
        }

        ConfigVariable existingVariable = findByVariableId(id);
        existingVariable.setVariableName(configVariable.getVariableName());
        existingVariable.setValue(configVariable.getValue());
        existingVariable.setType(configVariable.getType());
        existingVariable.setDescription(configVariable.getDescription());

        // Save the variable in the database
        ConfigVariable savedVariable = configVariableRepository.save(existingVariable);
        // Put the variable in the cache
        setConfVariable(configVariable.getVariableName(), configVariable.getValue());
        // Return the saved variable
        return savedVariable;
    }

}