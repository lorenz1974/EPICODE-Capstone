package capstone.be.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import capstone.be.configmanager.ConfigManager;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetVariables {

    private final ConfigManager configManager;

    // @PostConstruct
    // public void init() {
    // }

    public int getDefaultPageSize() {
        Object pageSizeValue = configManager.getConfVariable("pageDefaultSize");

        log.info("pageSizeValue: {}", pageSizeValue);
        int defaultPageSize = (pageSizeValue instanceof Integer) ? (int) pageSizeValue : 30;
        return defaultPageSize;
    }
}
