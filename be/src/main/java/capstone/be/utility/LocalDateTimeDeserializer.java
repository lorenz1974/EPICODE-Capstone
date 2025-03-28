package capstone.be.utility;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSS"), // 8 cifre decimali
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS"), // 7 cifre decimali
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"), // 6 cifre decimali
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSS"), // 5 cifre decimali
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSS"), // 4 cifre decimali
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"), // 3 cifre decimali
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"), // Senza frazioni di secondo
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"), // Spazio invece di 'T'
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"), // Formato senza secondi
            DateTimeFormatter.ofPattern("yyyy-MM-dd"), // Solo data, senza ora
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX") // ISO-8601
    );

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateStr = p.getText();
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateStr, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }

        // Se il parsing come LocalDateTime fallisce, prova a deserializzare come
        // LocalDate
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(dateStr, formatter).atStartOfDay();
            } catch (DateTimeParseException ignored) {
            }
        }

        throw new IOException("Invalid date format: " + dateStr);
    }
}
