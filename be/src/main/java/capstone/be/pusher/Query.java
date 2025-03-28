package capstone.be.pusher;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
class Query {
    private String sql;
    private String status;
    private String statusMessage;
    private List<Map<String, Object>> results;
    private List<Map<String, Object>> resultsReplaced;
    private String providerFieldValue;

    public boolean isError() {
        return status.equals("error");
    }

    public boolean isOk() {
        return status.equals("ok");
    }
}
