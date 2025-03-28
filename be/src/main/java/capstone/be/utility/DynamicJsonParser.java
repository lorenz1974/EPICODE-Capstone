package capstone.be.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * DynamicJsonParser
 *
 * This class dynamically generates Java classes at runtime based on JSON
 * structure.
 * It uses ByteBuddy to create classes with getter and setter methods for each
 * field.
 *
 * Features:
 * - Generates Java classes from JSON dynamically.
 * - Supports standard types: String, Integer, Double, Boolean.
 * - Supports LocalDate and LocalDateTime parsing via custom deserializers.
 * - Provides `getValue` method for safe method invocation.
 * - Retrieves field names dynamically.
 *
 * Dependencies:
 * - ByteBuddy for class generation.
 * - Jackson for JSON deserialization.
 *
 * Example Usage:
 *
 * <pre>
 * {@code
 * String json = "{ \"id\": \"123\", \"name\": \"John Doe\", \"date\": \"2025-03-11\" }";
 *
 * // Inject the service
 * @Autowired
 * private DynamicJsonParser dynamicJsonParser;
 *
 * // Generate dynamic class and parse JSON
 * Object dynamicInstance = dynamicJsonParser.parseJsonToClass("User", json);
 *
 * // Retrieve values dynamically
 * Object id = dynamicJsonParser.getValue(dynamicInstance, "getId");
 * Object name = dynamicJsonParser.getValue(dynamicInstance, "getName");
 * Object date = dynamicJsonParser.getValue(dynamicInstance, "getDate");
 *
 * // Print results
 * System.out.println("ID: " + id);
 * System.out.println("Name: " + name);
 * System.out.println("Date: " + date);
 * }
 * </pre>
 */
@Slf4j
@Component
public class DynamicJsonParser {

    /**
     * Parses JSON into a dynamically generated Java class.
     *
     * @param className The name of the class to be created.
     * @param json      The JSON string to parse.
     * @return An instance of the dynamically created class populated with JSON
     *         data.
     * @throws Exception If an error occurs during class generation or object
     *                   creation.
     */
    public Object parseJsonToClass(String className, String json) throws Exception {
        //
        // Configure ObjectMapper with custom deserializers
        //
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        objectMapper.registerModule(module);

        //
        // Convert JSON to a map
        //
        Map<String, Object> jsonMap = objectMapper.readValue(json, Map.class);

        //
        // Generate the dynamic class
        //
        Class<?> dynamicClass = generateClass(className, jsonMap);

        //
        // Create and populate the dynamic object
        //
        return populateDynamicObject(dynamicClass, jsonMap, objectMapper);
    }

    //
    // Generate the class based on the JSON structure
    //
    private Class<?> generateClass(String className, Map<String, Object> jsonMap) {
        DynamicType.Builder<?> builder = new ByteBuddy()
                .subclass(Object.class)
                .name("generated." + className);

        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            String fieldName = entry.getKey();
            Class<?> fieldType = mapValueToClass(entry.getValue());

            builder = builder.defineField(fieldName, fieldType, Modifier.PRIVATE)
                    .defineMethod("get" + capitalize(fieldName), fieldType, Modifier.PUBLIC)
                    .intercept(FieldAccessor.ofField(fieldName))
                    .defineMethod("set" + capitalize(fieldName), void.class, Modifier.PUBLIC)
                    .withParameter(fieldType)
                    .intercept(FieldAccessor.ofField(fieldName));
        }

        //
        // Add method to retrieve field names
        //
        builder = builder.defineMethod("getFieldNames", List.class, Modifier.PUBLIC)
                .intercept(MethodDelegation.to(new FieldNameInterceptor(jsonMap.keySet())));

        return builder.make()
                .load(DynamicJsonParser.class.getClassLoader())
                .getLoaded();
    }

    //
    // Map JSON values to Java classes
    //
    private Class<?> mapValueToClass(Object value) {
        if (value instanceof Integer)
            return Integer.class;
        if (value instanceof Double)
            return Double.class;
        if (value instanceof Boolean)
            return Boolean.class;
        if (value instanceof String) {
            //
            // Check if the string matches a date pattern (YYYY-MM-DD)
            //
            String stringValue = (String) value;
            if (stringValue.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return LocalDate.class;
            }

            //
            // Check if the string starts with a date pattern (YYYY-MM-DDT), delegating
            // parsing
            // to LocalDateTimeDeserializer
            //
            if (stringValue.matches("\\d{4}-\\d{2}-\\d{2}T.*")) {
                return LocalDateTime.class;
            }

            return String.class;
        }
        // Default to Object class if no match is found
        return Object.class;
    }

    //
    // Populate the dynamic object with values from the JSON map looping through the
    // fields
    //
    private Object populateDynamicObject(Class<?> dynamicClass, Map<String, Object> jsonMap,
            ObjectMapper objectMapper) throws Exception {
        Object instance = dynamicClass.getDeclaredConstructor().newInstance();

        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            String setterName = "set" + capitalize(entry.getKey());
            Class<?> fieldType = mapValueToClass(entry.getValue());
            Method setter = dynamicClass.getMethod(setterName, fieldType);

            //
            // Convert values using ObjectMapper to ensure correct type handling
            //
            Object value;
            if (fieldType == LocalDate.class) {
                value = objectMapper.convertValue(entry.getValue(), LocalDate.class);
            } else if (fieldType == LocalDateTime.class) {
                value = objectMapper.convertValue(entry.getValue(), LocalDateTime.class);
            } else {
                value = entry.getValue();
            }

            setter.invoke(instance, value);
        }

        return instance;
    }

    /**
     * Retrieves the value of a dynamically created object's property.
     *
     * @param instance   The instance of the dynamically created class.
     * @param methodName The name of the getter method (e.g., "getId").
     * @return The value returned by the method, or null if the method does not
     *         exist.
     */
    public Object getValue(Object instance, String methodName) {
        try {
            //
            // Retrieve the method from the dynamic class
            //
            Method method = instance.getClass().getMethod(methodName);

            //
            // Invoke the method and return the result
            //
            return method.invoke(instance);
        } catch (NoSuchMethodException e) {
            log.warn("Method '{}' not found.", methodName);
            return null;
        } catch (Exception e) {
            log.error("Error invoking method '{}'", methodName, e);
            return null;
        }
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    //
    // Utility class to retrieve field names
    //
    public static class FieldNameInterceptor {
        private final List<String> fieldNames;

        public FieldNameInterceptor(Set<String> fieldNames) {
            this.fieldNames = new ArrayList<>(fieldNames);
        }

        public List<String> getFieldNames() {
            return fieldNames;
        }
    }
}
