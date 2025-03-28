package capstone.be.observer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderStatus {

    private String status;
    private String statusMessage;

    public boolean isError() {
        return status.equals("error");
    }

    public boolean isCompleted() {
        return status.equals("completed");
    }

    public boolean isFailed() {
        return status.equals("failed");
    }

    public boolean isWaiting() {
        return status.equals("waiting");
    }
}
