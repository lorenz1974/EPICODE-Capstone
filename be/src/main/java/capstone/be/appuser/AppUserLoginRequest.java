package capstone.be.appuser;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AppUserLoginRequest {
    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    @NotNull
    private String password;
}
