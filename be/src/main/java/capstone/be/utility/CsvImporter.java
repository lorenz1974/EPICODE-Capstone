package capstone.be.utility;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
public class CsvImporter {

    public <T> List<T> importCsv(String filePath, Class<T> clazz) {
        try (BOMInputStream bomInputStream = new BOMInputStream(new FileInputStream(filePath));
                Reader reader = new InputStreamReader(bomInputStream, StandardCharsets.UTF_8)) {

            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(clazz)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreQuotations(false) // Disabilita la rimozione automatica delle virgolette
                    .withSeparator(',')
                    .withFilter(line -> {
                        for (int i = 0; i < line.length; i++) {
                            if ("NULL".equals(line[i])) {
                                line[i] = null;
                            }
                        }
                        return true;
                    })
                    .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS) // Imposta i campi "NULL" a null
                    .build();

            return csvToBean.parse();
        } catch (Exception e) {
            log.error("Error reading CSV file", e);
            return List.of();
        }
    }
}
