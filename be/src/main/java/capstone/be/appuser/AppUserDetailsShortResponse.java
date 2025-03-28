package capstone.be.appuser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDetailsShortResponse {

    private Long id;
    private String username;
    private String email;
    private String name;
    private String surname;
    private String nameSurname;
    private Set<AppUserRole> roles;

}
