package capstone.be.utility;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
@Slf4j
public class ReplacePlaceHolders {

    private static final String PLACEHOLDER_PREFIX = "{{";
    private static final String PLACEHOLDER_SUFFIX = "}}";

    public String of(String source, String placeholder, String replacement) {
        return source.replace(placeholder, replacement);
    }

    public String of(String source, List<Map<String, Object>> list) {

        // Extract every item of the List
        for (Map<String, Object> map1 : list) {

            // Loop again inside the Map<String, Object> map
            for (Map.Entry<String, Object> entry1 : map1.entrySet()) {

                // Create the placeholder
                String placeholder = PLACEHOLDER_PREFIX + entry1.getKey().toLowerCase().trim()
                        + PLACEHOLDER_SUFFIX;
                String replacementValue = (entry1.getValue() != null) ? entry1.getValue().toString() : "";

                source = source.replace(placeholder, replacementValue);
            }
        }

        // Return the source with the replaced placeholders
        return source;
    }
}