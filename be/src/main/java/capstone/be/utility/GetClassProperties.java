package capstone.be.utility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public final class GetClassProperties {

    /**
     * Restituisce un Set contenente tutti i nomi delle proprietà definite nella
     * classe.
     *
     * @param className Nome della classe
     * @return Set di stringhe con i nomi delle proprietà
     */
    public static Set<String> getAllPropertyNames(String className) {
        Set<String> propertyNames = new HashSet<>();
        Field[] fields;

        try {
            Class<?> clazz = Class.forName(className);
            fields = clazz.getDeclaredFields();
        } catch (ClassNotFoundException e) {
            return propertyNames;
        }

        for (Field field : fields) {
            if (field.getType().equals(String.class)) {
                propertyNames.add(field.getName());
            }
        }
        return propertyNames;
    }
}
