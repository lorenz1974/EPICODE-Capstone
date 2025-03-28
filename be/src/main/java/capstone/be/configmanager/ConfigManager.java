package capstone.be.configmanager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigManager {

    private final ConfigManagerService configService;
    private final Map<String, Object> configCache = new ConcurrentHashMap<>();

    //
    // ----------------------------------------------
    // Configuration variables
    // ----------------------------------------------
    //
    // @Value("${azure.tenant.id}")
    // private String azureTenantId;

    // @Value("${azure.sendmail.client.id}")
    // private String azureSendmailClientId;

    // @Value("${azure.sendmail.client-secret}")
    // private String azureSendmailClientSecret;

    // @Value("${app.administrator.email}")
    // private String appAdministratorEmail;

    // @Value("${app.administrator.name}")
    // private String appAdministratorName;

    // @Value("${namirial.token}")
    // private String namirialToken;

    @PostConstruct
    public void initializeCache() {
        log.info("Initializing configuration cache...");
        initializeConfigurations();
        refreshCache();
    }

    public Object getConfVariable(String variableName) {
        log.debug("Fetching configuration variable: {}", variableName);
        return configCache.getOrDefault(variableName, null);
    }

    //
    // ----------------------------------------------
    // Refresh cache every 10 minutes
    // ----------------------------------------------
    //
    @Scheduled(fixedRate = 600000) // 10 minutes
    public void refreshCache() {
        Map<String, Object> latestConfigurations = configService.loadConfigurations();
        configCache.clear();
        configCache.putAll(latestConfigurations);
        log.info("Configuration cache refreshed with {} items.", configCache.size());
        log.debug("Configuration cache refreshed with: {} ", latestConfigurations.toString());
    }

    public void setConfVariable(String variableName, Object value) {
        log.debug("Updating configuration variable: {}", variableName);
        configService.setConfVariable(variableName, value);
        configCache.put(variableName, value);
        log.debug("Configuration variable '{}' updated successfully.", variableName);
    }

    //
    // ----------------------------------------------
    // Private method to initialize configurations for the first time and for
    // al the other components and classes
    // ----------------------------------------------
    //
    private void initializeConfigurations() {

        log.info("Initializing configuration variables...");

        Map<String, Object> configurations = new LinkedHashMap<>();

        // ** Push the variables **
        // configurations.put("pageDefaultSize", 50);
        // configurations.put("cityCsvFile",
        // "src\\main\\localDataSources\\citiesProvincesRegionsStates.csv");
        // configurations.put("companiesCsvFile",
        // "src\\main\\localDataSources\\companies.csv");
        // configurations.put("branchesCsvFile",
        // "src\\main\\localDataSources\\branches.csv");
        // configurations.put("sectorsCsvFile",
        // "src\\main\\localDataSources\\sectors.csv");
        // configurations.put("contractsCsvFile",
        // "src\\main\\localDataSources\\contracts.csv");
        // configurations.put("baseSalariesCsvFile",
        // "src\\main\\localDataSources\\baseSalaries.csv");

        // configurations.put("azureTenantId", azureTenantId);
        // configurations.put("azureSendmailClientId", azureSendmailClientId);
        // configurations.put("azureSendmailClientSecret", azureSendmailClientSecret);

        // configurations.put("appAdministratorEmail", appAdministratorEmail);
        // configurations.put("appAdministratorName", appAdministratorName);

        // configurations.put("filesArchiveRootPath",
        // "C:\\Epicode-Capstone\\be\\uploaded-files");

        // configurations.put("namirialToken", namirialToken);

        for (Map.Entry<String, Object> entry : configurations.entrySet()) {
            try {
                setConfVariable(entry.getKey(), entry.getValue());
                log.debug("Stored '{}' -> {}", entry.getKey(), entry.getValue());
            } catch (Exception e) {
                log.error("Error setting variable '{}': {}", entry.getKey(), e.getMessage(), e);
            }
        }
        log.info("Configuration initialization completed.");
    }
}

// // **Tipi primitivi**
// configurations.put("intExample", 42);
// configurations.put("longExample", 10000000000L);
// configurations.put("booleanExample", true);
// configurations.put("doubleExample", 3.14159);
// configurations.put("stringExample", "Hello World");

// // **Array**
// configurations.put("arrayStringExample", new String[] { "value1",
// "value2", "value3" });
// configurations.put("arrayIntegerExample", new Integer[] { 10, 20, 30 });
// configurations.put("arrayBooleanExample", new Boolean[] { true, false,
// true });

// // **Liste**
// configurations.put("listStringExample", List.of("alpha", "beta",
// "gamma"));
// configurations.put("listIntegerExample", List.of(100, 200, 300));
// configurations.put("listBooleanExample", List.of(true, false, true));

// // **Mappe**
// configurations.put("mapStringToString", Map.of("key1", "value1", "key2",
// "value2"));
// configurations.put("mapStringToInt", Map.of("one", 1, "two", 2, "three",
// 3));
// configurations.put("mapLongToObject", Map.of(1L, "one", 2L, 42, 3L,
// true));

// // **Hashtable (simile a Map, ma sincronizzata)**
// Hashtable<String, Object> hashtableExample = new Hashtable<>();
// hashtableExample.put("A", "Alpha");
// hashtableExample.put("B", "Beta");
// configurations.put("hashtableExample", hashtableExample);
